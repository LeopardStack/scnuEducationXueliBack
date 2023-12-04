package com.scnujxjy.backendpoint.paymentInfoImport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.TeacherInformationTest.TeacherInformationErrorRecord;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoImportRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoOldSystemVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationExcelImportVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class PaymentInfoListener extends AnalysisEventListener<PaymentInfoImportRO> {
    public int dataCount = 0;

    private PaymentInfoMapper paymentInfoMapper;

    private StudentStatusMapper studentStatusMapper;

    private List<PaymentInfoImportErrorRecord> errorRecords = new ArrayList<>();
    private List<PaymentInfoOldSystemVO> validRecords = new ArrayList<>();

    private final String outputDirectory; // 结果文件存储目录
    private final String originalFileName; // 原始文件名

    public PaymentInfoListener(PaymentInfoMapper paymentInfoMapper, StudentStatusMapper studentStatusMapper,
                               String outputDirectory,
                               String originalFileName){
        this.paymentInfoMapper = paymentInfoMapper;
        this.studentStatusMapper = studentStatusMapper;
        this.outputDirectory = outputDirectory;
        this.originalFileName = originalFileName;
    }

    @Override
    public void invoke(PaymentInfoImportRO data, AnalysisContext context) {
        try {
//            log.info("获取一条缴费记录 " + data);
            String idNumber = data.getIdNumber();
            String grade = data.getGrade().replace("级", "");
            data.setGrade(grade);

            StudentStatusPO studentStatusPO = studentStatusMapper.selectOne(new LambdaQueryWrapper<StudentStatusPO>()
                    .eq(StudentStatusPO::getGrade, grade)
                    .eq(StudentStatusPO::getIdNumber, idNumber)
            );
            if(studentStatusPO == null){
                // 没学号
                PaymentInfoOldSystemVO paymentInfoOldSystemVO = new PaymentInfoOldSystemVO();
                BeanUtils.copyProperties(data, paymentInfoOldSystemVO);
                validRecords.add(paymentInfoOldSystemVO);
                throw new IllegalArgumentException("通过年级和身份证号码未找到学生 ");
            }
            PaymentInfoOldSystemVO paymentInfoOldSystemVO = new PaymentInfoOldSystemVO();
            BeanUtils.copyProperties(data, paymentInfoOldSystemVO);
            paymentInfoOldSystemVO.setStudentNumber(studentStatusPO.getStudentNumber());
//            log.info("导入旧系统的数据 " + paymentInfoOldSystemVO);
            validRecords.add(paymentInfoOldSystemVO);
        }catch (Exception e){
            PaymentInfoImportErrorRecord paymentInfoImportErrorRecord = new PaymentInfoImportErrorRecord();
            BeanUtils.copyProperties(data, paymentInfoImportErrorRecord);
            paymentInfoImportErrorRecord.setResult(e.toString());
            errorRecords.add(paymentInfoImportErrorRecord);
        }
        dataCount += 1;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("总共读入了 " + dataCount + " 条数据");

        if (!errorRecords.isEmpty()) {
            log.error("存在导入失败的数据 " + errorRecords.size() + " 条");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/paymentInformation";
            String errorFileName = currentDateTime + "_errorImportPaymentInformation.xlsx";

            // 创建目录
            File directory = new File(relativePath);
            if (!directory.exists()) {
                boolean dirsCreated = directory.mkdirs();
                if (!dirsCreated) {
                    log.error("Failed to create directories: " + relativePath);
                    return;  // 或者抛出异常
                }
            }

            EasyExcel.write(relativePath + "/" + errorFileName,
                    PaymentInfoImportErrorRecord.class).sheet("ErrorRecords").doWrite(errorRecords);


        }
        String resultFileName = createResultFileName(originalFileName);
        saveToExcelFile(validRecords, outputDirectory, resultFileName);
    }


    private String createResultFileName(String originalFileName) {
        // 去除扩展名，添加“结果”
        int extensionIndex = originalFileName.lastIndexOf(".");
        String fileNameWithoutExtension = originalFileName.substring(0, extensionIndex);
        return fileNameWithoutExtension + "_结果.xlsx";
    }

    private void saveToExcelFile(List<PaymentInfoOldSystemVO> data, String directory, String fileName) {
        File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            boolean dirsCreated = directoryFile.mkdirs();
            if (!dirsCreated) {
                log.error("无法创建目录: " + directory);
                return;
            }
        }

        String filePath = directory + "/" + fileName;
        EasyExcel.write(filePath, PaymentInfoOldSystemVO.class).sheet("Sheet1").doWrite(data);
    }

}
