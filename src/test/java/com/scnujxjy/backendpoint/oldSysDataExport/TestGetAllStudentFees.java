package com.scnujxjy.backendpoint.oldSysDataExport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.model.vo.oa.MajorChangeRecordExcelVO;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentFees;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ErrorPaymentInfoData extends PaymentInfoPO {
    @ExcelProperty(value = "导入失败原因", index = 12)
    private String errorReason;
}

@SpringBootTest
@Slf4j
public class TestGetAllStudentFees {
    @Autowired(required = false)
    private PaymentInfoMapper paymentInfoMapper;

    @Resource
    private OldDataSynchronize oldDataSynchronize;

    public void insertStudentFeesByGrade(int grade, ArrayList<ErrorPaymentInfoData> errorList){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai"); // 设置为北京时间
        dateFormat1.setTimeZone(timeZone);
        dateFormat2.setTimeZone(timeZone);
        dateFormat3.setTimeZone(timeZone);

        ArrayList<HashMap<String, String>> studentFees = getStudentFees("" + grade);
//        log.info(String.valueOf(studentFees.size()));
        ErrorPaymentInfoData errorPaymentInfoData = new ErrorPaymentInfoData();

        int success_insert = 0;
        int failed_insert = 0;

        for (HashMap<String, String> studentData : studentFees) {
            PaymentInfoPO paymentInfo = new PaymentInfoPO();

            try {
                // 请根据实际的字段名和数据类型调整以下代码
                paymentInfo.setStudentNumber(studentData.get("XHAO"));
//            paymentInfo.setAdmissionNumber(studentData.get("NJ"));
                paymentInfo.setName(studentData.get("XM"));
                paymentInfo.setIdCardNumber(studentData.get("SFZH"));

                String feeDateString = studentData.get("RQ");
                Date feeDate = null;
                try {
                    feeDate = dateFormat1.parse(feeDateString);
                } catch (ParseException e) {
                    try{
                        feeDate = dateFormat2.parse(feeDateString);
                    }catch (ParseException e1){
                        try{
                            feeDate = dateFormat3.parse(feeDateString);
                        }catch (ParseException e2){
                            errorPaymentInfoData.setStudentNumber(studentData.get("XHAO"));
                            errorPaymentInfoData.setName(studentData.get("XM"));
                            errorPaymentInfoData.setIdCardNumber(studentData.get("SFZH"));
                            errorPaymentInfoData.setPaymentCategory(studentData.get("LB"));
                            errorPaymentInfoData.setAcademicYear(studentData.get("XN"));
                            errorPaymentInfoData.setPaymentType(studentData.get("JFFS"));
//                errorPaymentInfoData.setam(studentData.get("JFFS"));
                            errorPaymentInfoData.setErrorReason(e.toString());
                            errorList.add(errorPaymentInfoData);
                            log.error("缴费日期格式解析失败 " + e2.toString());
                        }

                    }
                }
                paymentInfo.setPaymentDate(feeDate);

                paymentInfo.setPaymentCategory(studentData.get("LB"));
                paymentInfo.setAcademicYear(studentData.get("XN"));
                paymentInfo.setPaymentType(studentData.get("JFFS"));
                String fee = studentData.get("JINE");
                if(fee == null){
                    throw new RuntimeException("学费金额为空 " + studentData.toString());
                }
                else{
                    if(fee.trim().equals("NULL")){
                        throw new RuntimeException("学费金额为空 " + studentData.toString());
                    }
                }
                paymentInfo.setAmount(Double.parseDouble(fee));
                paymentInfo.setPaymentMethod("学年");
                paymentInfo.setIsPaid("是");
                paymentInfoMapper.insert(paymentInfo);
                success_insert += 1;
            }catch (Exception e){
                log.error(e.toString());
                errorPaymentInfoData.setStudentNumber(studentData.get("XHAO"));
                errorPaymentInfoData.setName(studentData.get("XM"));
                errorPaymentInfoData.setIdCardNumber(studentData.get("SFZH"));
                errorPaymentInfoData.setPaymentCategory(studentData.get("LB"));
                errorPaymentInfoData.setAcademicYear(studentData.get("XN"));
                errorPaymentInfoData.setPaymentType(studentData.get("JFFS"));
//                errorPaymentInfoData.setam(studentData.get("JFFS"));
                errorPaymentInfoData.setErrorReason(e.toString());
                errorList.add(errorPaymentInfoData);
                failed_insert += 1;
            }
        }
        log.info("成功插入 " + success_insert + " 条数据 " + " 失败插入 " + failed_insert + " 条数据 \n" +
                " 总共从旧系统获取 " + String.valueOf(studentFees.size() + " 条数据"));
        log.error("错误数据如下 \n" + errorList);
    }

    /**
     * 导入缴费信息
     */
    @Test
    public void test1(){
        ArrayList<ErrorPaymentInfoData> errorList = new ArrayList<>();
        StringBuilder allGrades = new StringBuilder();
        // 2000 - 至今 是全部的缴费数据 退学、休学、转学是学籍异动产生的费用
        for(int i = 2023; i > 2000; i--){
            insertStudentFeesByGrade(i, errorList);
            allGrades.append(i).append("_");
        }

        // 调用新方法导出errorList
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/studentfees/";
        String errorFileName = relativePath + currentDateTime + "_" + allGrades + "导入缴费数据失败的部分数据.xlsx";

        // 创建目录
        File directory = new File(relativePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 使用EasyExcel写入数据
        try {
            EasyExcel.write(errorFileName, ErrorPaymentInfoData.class).sheet("Error Data").doWrite(errorList);
            System.out.println("Excel写入成功，路径：" + errorFileName);
        } catch (Exception e) {
            log.error("写入Excel时出现异常", e);
        }


    }


    /**
     * 采用多线程同步旧系统中的财务数据
     */
    @Test
    public void test2(){
        paymentInfoMapper.truncateTable();
        log.info("清除了所有的缴费数据 目前缴费数据库中的数据量为 " + paymentInfoMapper.selectCount(null));
        oldDataSynchronize.synchronizePaymentInfoDataByInterval(
                true, 2023, 2001,
                new ArrayList<>(Arrays.asList("休学", "退学", "转学")));
    }

    @Test
    public void test3(){
        paymentInfoMapper.truncateTable();

        oldDataSynchronize.synchronizePaymentInfoDataAll(true,
                new ArrayList<>(Arrays.asList("休学", "退学", "转学")));
    }
}
