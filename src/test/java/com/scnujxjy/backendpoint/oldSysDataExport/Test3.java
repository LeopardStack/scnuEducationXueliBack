package com.scnujxjy.backendpoint.oldSysDataExport;

import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfo;
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
public class Test3 {
    @Autowired(required = false)
    private PaymentInfoMapper paymentInfoMapper;

    /**
     * 导入缴费信息
     */
    @Test
    public void test1(){
        ArrayList<HashMap<String, String>> studentFees = getStudentFees("2018");
        log.info(String.valueOf(studentFees.size()));

        for (HashMap<String, String> studentData : studentFees) {
            PaymentInfo paymentInfo = new PaymentInfo();

            // 请根据实际的字段名和数据类型调整以下代码
            paymentInfo.setStudentNumber(studentData.get("XHAO"));
//            paymentInfo.setAdmissionNumber(studentData.get("NJ"));
            paymentInfo.setName(studentData.get("XM"));
            paymentInfo.setIdCardNumber(studentData.get("SFZH"));
            paymentInfo.setPaymentCategory(studentData.get("LB"));
            paymentInfo.setAcademicYear(studentData.get("XN"));
            paymentInfo.setPaymentType(studentData.get("JFFS"));
            paymentInfo.setAmount(BigDecimal.valueOf(Double.parseDouble(studentData.get("XF"))));
            paymentInfo.setPaymentMethod("学年");

            paymentInfoMapper.insert(paymentInfo);
        }
    }
}
