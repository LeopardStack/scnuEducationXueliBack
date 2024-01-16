package com.scnujxjy.backendpoint.util.excelListener;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoImportRO;
import com.scnujxjy.backendpoint.model.vo.core_data.NewStudentPaymentInfoOldSystemVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoOldSystemVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Data
public class NewStudentPaymentInfoConvertToOldSystemListener extends AnalysisEventListener<PaymentInfoImportRO> {
    private AtomicInteger dataCount = new AtomicInteger(0);

    private AdmissionInformationMapper admissionInformationMapper;


    private List<PaymentInfoImportErrorRecord> errorRecords = Collections.synchronizedList(new ArrayList<>());
    private List<NewStudentPaymentInfoOldSystemVO> validRecords = new CopyOnWriteArrayList<>();

    private final String outputDirectory; // 结果文件存储目录
    private final String originalFileName; // 原始文件名

    private ExecutorService executorService = Executors.newFixedThreadPool(100); // 根据需要调整线程池大小
    private List<Future<NewStudentPaymentInfoOldSystemVO>> futures = new ArrayList<>();


    public NewStudentPaymentInfoConvertToOldSystemListener(AdmissionInformationMapper admissionInformationMapper,
                               String outputDirectory,
                               String originalFileName){
        this.admissionInformationMapper = admissionInformationMapper;
        this.outputDirectory = outputDirectory;
        this.originalFileName = originalFileName;
    }

    public int getCurrentDataCount() {
        return dataCount.get();
    }


    @Override
    public void invoke(PaymentInfoImportRO data, AnalysisContext context) {
        futures.add(executorService.submit(() -> processRecord(data)));
    }

    private NewStudentPaymentInfoOldSystemVO processRecord(PaymentInfoImportRO data) throws Exception {
        try {
//            log.info("获取一条缴费记录 " + data);
            String idNumber = data.getIdNumber();
            String grade = data.getGrade().replace("级", "");
            data.setGrade(grade);

            AdmissionInformationPO admissionInformationPO = admissionInformationMapper.selectOne(new LambdaQueryWrapper<AdmissionInformationPO>()
                    .eq(AdmissionInformationPO::getGrade, grade)
                    .eq(AdmissionInformationPO::getIdCardNumber, idNumber)
            );
            if(admissionInformationPO == null){
                // 没录取信息
                throw new IllegalArgumentException("通过年级和身份证号码未找到学生 ");
            }
            NewStudentPaymentInfoOldSystemVO newStudentPaymentInfoOldSystemVO = new NewStudentPaymentInfoOldSystemVO();
            newStudentPaymentInfoOldSystemVO.setShortAdmissionNumber(admissionInformationPO.getShortStudentNumber());
            newStudentPaymentInfoOldSystemVO.setIdNumber(data.getIdNumber());
            newStudentPaymentInfoOldSystemVO.setName(data.getName());
            newStudentPaymentInfoOldSystemVO.setCollege(data.getCollege());
            newStudentPaymentInfoOldSystemVO.setMajorName(data.getMajorName());
            newStudentPaymentInfoOldSystemVO.setGrade(grade);
            newStudentPaymentInfoOldSystemVO.setLevel(data.getLevel());
            newStudentPaymentInfoOldSystemVO.setClassName(data.getClassName());
            newStudentPaymentInfoOldSystemVO.setAmount(data.getAmount());
            newStudentPaymentInfoOldSystemVO.setPayDate(data.getPayDate());
            newStudentPaymentInfoOldSystemVO.setPayType(data.getPayType());
            return newStudentPaymentInfoOldSystemVO;
        }catch (Exception e){
            PaymentInfoImportErrorRecord paymentInfoImportErrorRecord = new PaymentInfoImportErrorRecord();
            BeanUtils.copyProperties(data, paymentInfoImportErrorRecord);
            paymentInfoImportErrorRecord.setResult(e.toString());
            errorRecords.add(paymentInfoImportErrorRecord);
        }
        dataCount.incrementAndGet();
        return null;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        for (Future<NewStudentPaymentInfoOldSystemVO> future : futures) {
            try {
                NewStudentPaymentInfoOldSystemVO result = future.get();
                if (result != null) {
                    validRecords.add(result);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error processing record", e);
            }
        }
        executorService.shutdown();
        log.info("总共读入了 " + dataCount + " 条数据");

        if (!errorRecords.isEmpty()) {
            log.error("存在导入失败的数据 " + errorRecords.size() + " 条");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/paymentInformation";
            String errorFileName = currentDateTime + "_errorNewStudentImportPaymentInformation.xlsx";

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

    private void saveToExcelFile(List<NewStudentPaymentInfoOldSystemVO> data, String directory, String fileName) {
        File directoryFile = new File(directory);
        if (!directoryFile.exists()) {
            boolean dirsCreated = directoryFile.mkdirs();
            if (!dirsCreated) {
                log.error("无法创建目录: " + directory);
                return;
            }
        }

        String filePath = directory + "/" + fileName;
        EasyExcel.write(filePath, NewStudentPaymentInfoOldSystemVO.class).sheet("Sheet1").doWrite(data);
    }
}
