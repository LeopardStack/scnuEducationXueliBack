package com.scnujxjy.backendpoint.oldSysDataExport;

import com.alibaba.excel.EasyExcel;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentFees;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class ErrorData{
    private Integer index;
    private String data;
    private String reason;
}


@SpringBootTest
@Slf4j
public class TestGetAllGradeInfos {
    @Autowired(required = false)
    private PaymentInfoMapper paymentInfoMapper;

    public void exportErrorListToExcel(ArrayList<HashMap<String, String>> errorList, String outputPath) {
        ArrayList<ErrorData> exportList = new ArrayList<>();

        for (int i = 0; i < errorList.size(); i++) {
            HashMap<String, String> errorDataMap = errorList.get(i);
            ErrorData errorData = new ErrorData();
            errorData.setIndex(i + 1);
            errorData.setData(errorDataMap.toString());  // 你可以根据需要更改这里的数据格式
            errorData.setReason("数据插入失败原因");  // 如果你可以从errorList获取具体的失败原因，则替换这里
            exportList.add(errorData);
        }

        EasyExcel.write(outputPath, ErrorData.class).sheet("Error Data").doWrite(exportList);
    }

    public void insertStudentFeesByGrade(int grade, ArrayList<HashMap<String, String>> errorList){
        ArrayList<HashMap<String, String>> studentFees = getStudentFees("" + grade);
//        log.info(String.valueOf(studentFees.size()));

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
                paymentInfo.setPaymentCategory(studentData.get("LB"));
                paymentInfo.setAcademicYear(studentData.get("XN"));
                paymentInfo.setPaymentType(studentData.get("JFFS"));
                String fee = studentData.get("XF");
                if(fee == null || fee.trim().length() == 0){
                    fee = studentData.get("JINE");
                }
                paymentInfo.setAmount(BigDecimal.valueOf(Double.parseDouble(fee)));

                paymentInfo.setPaymentMethod("学年");

                paymentInfoMapper.insert(paymentInfo);
                success_insert += 1;
            }catch (Exception e){
                log.error(e.toString());
                HashMap<String, String> errorData = new HashMap<>();
                errorData.put(studentData.toString(), e.toString());
                errorList.add(errorData);
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
        ArrayList<HashMap<String, String>> errorList = new ArrayList<>();
        // 2023 - 2016 已导入，闭区间

        for(int i = 2015; i > 2000; i--){
            insertStudentFeesByGrade(i, errorList);
        }

        // 调用新方法导出errorList
        exportErrorListToExcel(errorList, "data_import_error_excel/studentfees/20230905导入缴费数据失败的部分数据.xlsx");
    }
}
