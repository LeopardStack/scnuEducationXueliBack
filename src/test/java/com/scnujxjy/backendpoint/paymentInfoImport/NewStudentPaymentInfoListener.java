package com.scnujxjy.backendpoint.paymentInfoImport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoImportRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoOldSystemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class NewStudentPaymentInfoListener extends AnalysisEventListener<PaymentInfoImportRO> {

    public int dataCount = 0;

    private PaymentInfoMapper paymentInfoMapper;

    private AdmissionInformationMapper admissionInformationMapper;

    private List<PaymentInfoImportErrorRecord> errorRecords = new ArrayList<>();
    private List<PaymentInfoOldSystemVO> validRecords = new ArrayList<>();

    public NewStudentPaymentInfoListener(PaymentInfoMapper paymentInfoMapper,
                                         AdmissionInformationMapper admissionInformationMapper){
        this.paymentInfoMapper = paymentInfoMapper;
        this.admissionInformationMapper = admissionInformationMapper;
    }

    @Override
    public void invoke(PaymentInfoImportRO paymentInfoImportRO, AnalysisContext analysisContext) {
//        log.info("读取一行新生数据 " + paymentInfoImportRO);
        try {
            String grade = paymentInfoImportRO.getGrade().replace("级", "");
            AdmissionInformationPO admissionInformationPO = admissionInformationMapper.selectOne(new LambdaQueryWrapper<AdmissionInformationPO>()
                    .eq(AdmissionInformationPO::getGrade, grade)
                    .eq(AdmissionInformationPO::getIdCardNumber, paymentInfoImportRO.getIdNumber())
            );
            PaymentInfoPO paymentInfoPO = new PaymentInfoPO();
            paymentInfoPO.setAdmissionNumber(admissionInformationPO.getAdmissionNumber());
            paymentInfoPO.setName(admissionInformationPO.getName());
            paymentInfoPO.setIdCardNumber(admissionInformationPO.getIdCardNumber());

            // 设置日期格式
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                // 将字符串转换为Date类型
                Date payDate = dateFormat.parse(paymentInfoImportRO.getPayDate());

                // 假设有一个paymentInfoPO对象，并且它有一个setPaymentDate方法接受Date类型
                // paymentInfoPO.setPaymentDate(payDate);
                paymentInfoPO.setPaymentDate(payDate);

            } catch (Exception e) {
                // 异常处理
                throw new IllegalArgumentException("日期转换失败: " + e.getMessage());
            }

            // 新生缴费都是第一学年
            paymentInfoPO.setAcademicYear("1");
            paymentInfoPO.setPaymentType(paymentInfoImportRO.getPayType());
            paymentInfoPO.setAmount(Double.parseDouble(paymentInfoImportRO.getAmount()));
            paymentInfoPO.setIsPaid("是");
            paymentInfoPO.setPaymentMethod("学年");
            paymentInfoPO.setGrade(grade);
            // 区分转学、退学、休学产生的费用
            paymentInfoPO.setRemark("普通");
            int insert = paymentInfoMapper.insert(paymentInfoPO);
            if(insert <= 0){
                throw new IllegalArgumentException("插入数据库失败 " + insert);
            }
        }catch (Exception e1){
            log.info("插入新生缴费数据失败 " + e1);
            PaymentInfoImportErrorRecord paymentInfoImportErrorRecord = new PaymentInfoImportErrorRecord();
            BeanUtils.copyProperties(paymentInfoImportRO, paymentInfoImportErrorRecord);
            paymentInfoImportErrorRecord.setResult(e1.toString());
            errorRecords.add(paymentInfoImportErrorRecord);
        }
        dataCount += 1;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("总共读入了 " + dataCount + " 条数据");

        if (!errorRecords.isEmpty()) {
            log.error("存在导入失败的数据 " + errorRecords.size() + " 条");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/paymentInformation";
            String errorFileName = currentDateTime + "_errorImportPaymentInformation.xlsx";
            EasyExcel.write(relativePath + "/" + errorFileName,
                    PaymentInfoImportErrorRecord.class).sheet("ErrorRecords").doWrite(errorRecords);
        }
    }
}
