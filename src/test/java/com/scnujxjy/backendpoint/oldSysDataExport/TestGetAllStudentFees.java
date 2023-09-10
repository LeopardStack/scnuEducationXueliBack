package com.scnujxjy.backendpoint.oldSysDataExport;

import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentFees;

@SpringBootTest
@Slf4j
public class TestGetAllStudentFees {
    @Autowired(required = false)
    private PaymentInfoMapper paymentInfoMapper;

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

                paymentInfoMapper.insert(paymentInfo);
                success_insert += 1;
            }catch (Exception e){
                log.error(e.toString());
                errorList.add(studentData);
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
        for(int i = 2023; i > 2001; i--){
            insertStudentFeesByGrade(i, errorList);
        }
    }
}
