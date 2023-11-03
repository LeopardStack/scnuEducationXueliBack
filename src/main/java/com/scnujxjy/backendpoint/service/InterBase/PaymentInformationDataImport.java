package com.scnujxjy.backendpoint.service.InterBase;

import com.alibaba.excel.annotation.ExcelProperty;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import static com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize.CONSUMER_COUNT;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ErrorPaymentInfoData extends PaymentInfoPO {
    @ExcelProperty(value = "导入失败原因", index = 12)
    private String errorReason;
}

@Data
@Slf4j
public class PaymentInformationDataImport {
    private PaymentInfoMapper paymentInfoMapper;

    private MinioService minioService;

    // 是否强行覆盖
    private static boolean updateAny = false;

    // 记录每年的更新记录数
    public Map<String, Long> updateCountMap = new ConcurrentHashMap<>();


    // 存储插入日志
    public List<String> insertLogsList = Collections.synchronizedList(new ArrayList<>());

    public List<ErrorPaymentInfoData> errorList = new ArrayList<>();


    public void setUpdateAny(boolean updateAnySet){
        updateAny = updateAnySet;
    }

    public PaymentInformationDataImport(){
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        this.paymentInfoMapper = ctx.getBean(PaymentInfoMapper.class);
        this.minioService = ctx.getBean(MinioService.class);

        this.init();
    }

    public ExecutorService executorService;

    public BlockingQueue<HashMap<String, String>> queue = new LinkedBlockingQueue<>();  // Unbounded queue

    public CountDownLatch latch;

    public void init() {

        latch = new CountDownLatch(CONSUMER_COUNT);
        // 创建消费者线程
        executorService = Executors.newFixedThreadPool(CONSUMER_COUNT);

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    while (true) {
                        HashMap<String, String> hashMap = queue.take();
                        if (hashMap.containsKey("END")) {
                            break;
                        }
                        insertData(hashMap);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();  // decrement the count
                }
            });
        }
    }

    int success_insert = 0;
    int failed_insert = 0;
    @Transactional(rollbackFor = Exception.class)
    int insertData(HashMap<String, String> studentData) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd");

        PaymentInfoPO paymentInfo = new PaymentInfoPO();
        ErrorPaymentInfoData errorPaymentInfoData = new ErrorPaymentInfoData();
        try {
            // 请根据实际的字段名和数据类型调整以下代码
            paymentInfo.setStudentNumber(studentData.get("XHAO"));
//            paymentInfo.setAdmissionNumber(studentData.get("NJ"));
            paymentInfo.setName(studentData.get("XM"));
            paymentInfo.setIdCardNumber(studentData.get("SFZH"));

            String feeDateString = studentData.get("RQ");
            Date feeDate = null;
            if(feeDateString != null){
                try {
                    feeDate = dateFormat1.parse(feeDateString);
                } catch (ParseException e) {
                    try {
                        feeDate = dateFormat2.parse(feeDateString);
                    } catch (ParseException e1) {
                        try {
                            feeDate = dateFormat3.parse(feeDateString);
                        } catch (ParseException e2) {
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
            }

            paymentInfo.setPaymentCategory(studentData.get("LB"));
            paymentInfo.setAcademicYear(studentData.get("XN"));
            paymentInfo.setPaymentType(studentData.get("JFFS"));
            String fee = studentData.get("JINE");
            if (fee == null) {
                throw new RuntimeException("学费金额为空 " + studentData.toString());
            } else {
                if (fee.trim().equals("NULL")) {
                    throw new RuntimeException("学费金额为空 " + studentData.toString());
                }
            }
            paymentInfo.setAmount(Double.parseDouble(fee));
            paymentInfo.setPaymentMethod("学年");
            paymentInfo.setIsPaid("是");
            synchronized(this) {
                paymentInfoMapper.insert(paymentInfo);
                success_insert += 1;
            }
            return 1;
        } catch (Exception e) {
            log.error(e.toString());
            errorPaymentInfoData.setStudentNumber(studentData.get("XHAO"));
            errorPaymentInfoData.setName(studentData.get("XM"));
            errorPaymentInfoData.setIdCardNumber(studentData.get("SFZH"));
            errorPaymentInfoData.setPaymentCategory(studentData.get("LB"));
            errorPaymentInfoData.setAcademicYear(studentData.get("XN"));
            errorPaymentInfoData.setPaymentType(studentData.get("JFFS"));
            errorPaymentInfoData.setErrorReason(e.toString());
            synchronized(this) {
                errorList.add(errorPaymentInfoData);
                failed_insert += 1;
            }
        }

        return -1;
    }

}
