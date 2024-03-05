package com.scnujxjy.backendpoint.service.InterBase;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.oa.MajorChangeRecordPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.OriginalEducationInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.oa.*;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.*;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.model.ro.oa.MajorChangeRecordRO;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.DataImportScnuOldSys;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.*;

@Service
@Slf4j
public class OldDataSynchronize {

    public static final int CONSUMER_COUNT = 200;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private GraduationInfoMapper graduationInfoMapper;

    @Resource
    private ScoreInformationMapper scoreInformationMapper;

    @Resource
    private PersonalInfoMapper personalInfoMapper;

    @Resource
    private OriginalEducationInfoMapper originalEducationInfoMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    @Resource
    private DropoutRecordMapper dropoutRecordMapper;
    @Resource
    private MajorChangeRecordMapper majorChangeRecordMapper;
    @Resource
    private ResumptionRecordMapper resumptionRecordMapper;
    @Resource
    private RetentionRecordMapper retentionRecordMapper;
    @Resource
    private SuspensionRecordMapper suspensionRecordMapper;

    @Resource
    private MinioService minioService;

    /**
     * 将 list 存储到 minio 中的一个 txt 文件中
     * @param data
     * @param fileName
     * @param diyBucketName
     */
    public void exportListToTxtAndUploadToMinio(List<String> data, String fileName, String diyBucketName) {
        // Step 1: Convert the map to a string
        StringBuilder stringBuilder = new StringBuilder();
        for (String entry : data) {
            stringBuilder.append(entry).append("\n");
        }

        // Step 2: Convert the string to ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes());

        // Step 3: Upload to Minio
        boolean success = minioService.uploadStreamToMinio(inputStream, fileName.endsWith(".txt") ? fileName : fileName + ".txt", diyBucketName);

        if (success) {
            log.info("同步日志已成功上传 Minio 服务器");
        } else {
            log.error("同步日志未成功上传 Minio 服务器");
        }

        // Close the stream
        try {
            inputStream.close();
        } catch (IOException e) {
            log.error("Error closing stream: " + e.getMessage());
        }
    }


    /**
     * 将 map 存储到 minio 中的一个 txt 文件中
     * @param data
     * @param fileName
     * @param diyBucketName
     */
    public void exportMapToTxtAndUploadToMinio(Map<String, Long> data, String fileName, String diyBucketName) {
        // Step 1: Convert the map to a string
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append(System.lineSeparator());
        }

        // Step 2: Convert the string to ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes());

        // Step 3: Upload to Minio
        boolean success = minioService.uploadStreamToMinio(inputStream, fileName.endsWith(".txt") ? fileName : fileName + ".txt", diyBucketName);

        if (success) {
            log.info("Successfully uploaded to Minio.");
        } else {
            log.error("Failed to upload to Minio.");
        }

        // Close the stream
        try {
            inputStream.close();
        } catch (IOException e) {
            log.error("Error closing stream: " + e.getMessage());
        }
    }


    /**
     * 将 数据同步的结果 excel 存储到 minio
     * @param errorList
     * @param fileName
     * @param diyBucketName
     */
    public <T> boolean exportErrorListToExcelAndUploadToMinio(List<T> errorList, Class<T> type, String fileName, String diyBucketName) {
        // Step 1: Write data to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, type).sheet("Sheet1").doWrite(errorList); // 注意，这里直接使用 T.class 是不允许的。我们需要其他方式来传递类的类型。

        // Step 2: Convert ByteArrayOutputStream to ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Step 3: Upload to Minio
        boolean success = minioService.uploadStreamToMinio(inputStream, fileName, diyBucketName);

        // Close the streams
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            log.error("Error closing streams: " + e.getMessage());
        }

        if (success) {
            log.info("Successfully uploaded to Minio.");
            return true;
        } else {
            log.error("Failed to upload to Minio.");
            return false;
        }


    }



    private void exportErrorListToExcel(List<ErrorStudentStatusExportExcel> errorList, String outputPath) {
        EasyExcel.write(outputPath, ErrorStudentStatusExportExcel.class).sheet("Sheet1").doWrite(errorList);
    }


    public void synchronizeStudentStatusData(int startYear, int endYear, boolean updateAny) throws InterruptedException {
        StudentStatusDataImport studentStatusDataImport = new StudentStatusDataImport();

        studentStatusDataImport.setUpdateAny(updateAny);

        StringBuilder allGrades = new StringBuilder();

        boolean updated = false;

        for (int i = startYear; i >= endYear; i--) {
            boolean updateInsert = true;
            SCNUXLJYDatabase scnuxljyDatabase = null;
            try{
                // 检测新旧系统的学籍数据数目是否相同，相同则不需要更新
                Integer integer = studentStatusMapper.selectCount(new
                        LambdaQueryWrapper<StudentStatusPO>().eq(StudentStatusPO::getGrade,
                        i));
                log.info(i + "年 新系统中导入的学籍数据记录数 " + integer);
                studentStatusDataImport.updateCountMap.put(i + "年 新系统中导入的学籍数据记录数 ", Long.valueOf(integer));

                scnuxljyDatabase = new SCNUXLJYDatabase();
                Object value = scnuxljyDatabase.getValue("select count(*) from STUDENT_VIEW_WITHPIC where nj='" + i +
                        "' and bshi not LIKE 'WP%';");
                Object value_fwp = scnuxljyDatabase.getValue("select count(*) from STUDENT_VIEW_WITHPIC where nj='" + i +
                        "' and bshi LIKE 'WP%';");

                studentStatusDataImport.updateCountMap.put(i + "年 旧系统中的非学历在籍学生数量为 ", (long)(int) value_fwp);
                studentStatusDataImport.updateCountMap.put(i + "年 学历学生数量为 ", (long)(int) value);
                log.info(i + "年 旧系统中的非学历在籍学生数量为 " + value_fwp + " 学历学生数量为 " + value);

                int oldAll = (int) value;
                int oldFwpAll = (int) value_fwp;
                if(oldFwpAll + oldAll == integer){
                    updateInsert = false;
                }

            }catch (Exception e){
                log.error("获取新旧系统学籍数据失败 " + i + "\n" + e.toString());
            }finally {
                if(scnuxljyDatabase != null){
                    scnuxljyDatabase.close();
                }
            }

            if(updateInsert || updateAny){
                if(updateAny){
                    // 删除所有学籍数据
                    int delete1 = studentStatusMapper.delete(new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, i + ""));
                    int delete2 = graduationInfoMapper.delete(new LambdaQueryWrapper<GraduationInfoPO>().
                            eq(GraduationInfoPO::getGrade, i + ""));
                    int delete3 = personalInfoMapper.delete(new LambdaQueryWrapper<PersonalInfoPO>().
                            eq(PersonalInfoPO::getGrade, i + ""));
                    int delete4 = originalEducationInfoMapper.delete(new LambdaQueryWrapper<OriginalEducationInfoPO>().
                            eq(OriginalEducationInfoPO::getGrade, i + ""));
                    if(delete1 > 0){
                        log.info("已删除所有学籍数据 " + i + " 年的记录 " + delete1);
                    }
                }

                ArrayList<HashMap<String, String>> studentStatusData = getStudentInfos(String.valueOf(i));
                for (HashMap<String, String> hashMap : studentStatusData) {

                    studentStatusDataImport.queue.put(hashMap); // Put the object in the queue
                }
                allGrades.append(i).append("_");

                // 更新相应的目录下的照片到 minio 中
                String bucketName = "xuelistudentpictures";
                String projectRootPath = System.getProperty("user.dir");
                File rootDir = new File(projectRootPath, "xuelistudentpictures/" + i);
                String rootDirPath;
                rootDirPath = rootDir.getAbsolutePath();

                Map<String, Long> picCountMap = new HashMap<>();
                picCountMap.put("入学照片", 0L);
                picCountMap.put("毕业照片", 0L);

                try {
                    // update 为 true 哪怕照片存在 也会覆盖
                    minioService.uploadDirectory(bucketName, rootDir, rootDirPath, picCountMap, false);
                    studentStatusDataImport.updateCountMap.put(i + "年入学照片上传数量", picCountMap.get("入学照片"));
                    studentStatusDataImport.updateCountMap.put(i + "年毕业照片上传数量", picCountMap.get("毕业照片"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                updated = true;
            }
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("END", "TRUE");
            studentStatusDataImport.queue.put(hashMap);
        }

        studentStatusDataImport.latch.await();

        if (studentStatusDataImport.executorService != null) {
            studentStatusDataImport.executorService.shutdown();
        }

        if(updated){
            for (int i = startYear; i >= endYear; i--) {
                Integer integer1 = studentStatusMapper.selectCount(new
                        LambdaQueryWrapper<StudentStatusPO>().eq(StudentStatusPO::getGrade,
                        i));
                log.info(i + "年 新系统中更新后的学籍数据记录数 " + integer1);
                studentStatusDataImport.updateCountMap.put(i + "年 新系统中更新后的学籍数据记录数 ", Long.valueOf(integer1));
            }
        }

        // 调用新方法导出errorList
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/studentStatusData/";
        String errorFileName = relativePath + currentDateTime + "_" + allGrades + "导入学籍数据失败的部分数据.xlsx";
        String errorFileName1 = relativePath + currentDateTime + "_" + "导入学籍数据总览.txt";
        exportMapToTxtAndUploadToMinio(studentStatusDataImport.updateCountMap, errorFileName1, "datasynchronize");
        log.info(studentStatusDataImport.updateCountMap.toString());
        exportErrorListToExcelAndUploadToMinio(studentStatusDataImport.errorList, ErrorStudentStatusExportExcel.class,
                errorFileName, "datasynchronize");

    }

    /**
     * 同步旧系统与新系统的教学计划
     */
    public void synchronizeTeachingPlansData(boolean updateAny, boolean truncateTable) throws InterruptedException {
        TeachingPlansDataImport teachingPlansDataImport = new TeachingPlansDataImport();
        teachingPlansDataImport.setUpdateAny(updateAny);

        ArrayList<HashMap<String, String>> teachingPlans = getTeachingPlans();
        teachingPlansDataImport.insertLogsList.add("旧系统教学计划总数 " + teachingPlans.size());

        Integer integer = courseInformationMapper.selectCount(null);
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        Object value = scnuxljyDatabase.getValue("select count(*) from courseDATA");
        Object value_fwp = scnuxljyDatabase.getValue("select count(*) from courseDATA where bshi LIKE 'WP%';");
        teachingPlansDataImport.insertLogsList.add("新系统中导入的教学计划记录数 " + integer);
        teachingPlansDataImport.insertLogsList.add("旧系统中的总教学计划记录数 " + value);
        teachingPlansDataImport.insertLogsList.add("新系统中的非学历教育教学计划记录数 " + value_fwp);

        if(integer != ((int)value - (int)value_fwp) && updateAny && truncateTable){
            courseInformationMapper.truncateTable();
            if(courseInformationMapper.selectList(null).size() == 0){
                log.info("成功清除教学计划表，开始教学计划新旧系统数据同步，注意：仅同步学历教育的教学计划");
            }

        }

        for (HashMap<String, String> hashMap : teachingPlans) {

            teachingPlansDataImport.queue.put(hashMap); // Put the object in the queue
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("END", "TRUE");
            teachingPlansDataImport.queue.put(hashMap);
        }

        teachingPlansDataImport.latch.await();

        if (teachingPlansDataImport.executorService != null) {
            teachingPlansDataImport.executorService.shutdown();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/courseInformationData/";
        String errorFileName = relativePath + currentDateTime + "_"  + "导入教学计划结果总览.txt";
        String errorFileName1 = relativePath + currentDateTime + "_"  + "导入教学计划结果总览日志.txt";
        teachingPlansDataImport.insertLogsList.add("文凭班教学计划总数为 " + teachingPlansDataImport.getWpCount());
        teachingPlansDataImport.insertLogsList.add("非文凭班教学计划总数为 " + teachingPlansDataImport.getFwpCount());

        exportMapToTxtAndUploadToMinio(teachingPlansDataImport.updateCountMap, errorFileName1,
                "datasynchronize");
        exportListToTxtAndUploadToMinio(teachingPlansDataImport.insertLogsList,
                errorFileName, "datasynchronize");
    }


    /**
     * 同步旧系统与新系统的班级数据
     */
    public void synchronizeClassInformationData(boolean updateAny) throws InterruptedException {
        ClassInformationDataImport classInformationDataImport = new ClassInformationDataImport();
        classInformationDataImport.setUpdateAny(updateAny);

        ArrayList<HashMap<String, String>> teachingPlans = getClassDatas();
        classInformationDataImport.insertLogs.add("旧系统班级数据总数 " + teachingPlans.size());

        Integer integer = classInformationMapper.selectCount(null);
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        Object value = scnuxljyDatabase.getValue("select count(*) from classdata");
        Object value_fwp = scnuxljyDatabase.getValue("select count(*) from classdata where bshi LIKE 'WP%';");
        classInformationDataImport.insertLogs.add("新系统中导入的班级记录数 " + integer);
        classInformationDataImport.insertLogs.add("旧系统中的总班级记录数 " + value);
        classInformationDataImport.insertLogs.add("新系统中的非学历教育班级记录数 " + value_fwp);


        for (HashMap<String, String> hashMap : teachingPlans) {

            classInformationDataImport.queue.put(hashMap); // Put the object in the queue
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("END", "TRUE");
            classInformationDataImport.queue.put(hashMap);
        }

        classInformationDataImport.latch.await();

        if (classInformationDataImport.executorService != null) {
            classInformationDataImport.executorService.shutdown();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/classInformationData/";
        String errorFileName = relativePath + currentDateTime + "_"  + "导入班级结果总览.txt";
        String errorFileName1 = relativePath + currentDateTime + "_"  + "导入班级结果总览日志.txt";
        classInformationDataImport.insertLogs.add("文凭班班级总数为 " + classInformationDataImport.getFxl_class_count());
        classInformationDataImport.insertLogs.add("非文凭班班级总数为 " + classInformationDataImport.getXl_class_count());

        exportMapToTxtAndUploadToMinio(classInformationDataImport.updateCountMap, errorFileName1,
                "datasynchronize");
        exportListToTxtAndUploadToMinio(classInformationDataImport.insertLogs,
                errorFileName, "datasynchronize");
    }

    /**
     * 同步旧系统与新系统的学籍异动数据
     */
    public void synchronizeStudentStatusChangeData(boolean updateAny) throws InterruptedException {
        StudentStatusChangeDataImport studentStatusChangeDataImport = new StudentStatusChangeDataImport();
        if(updateAny){
            // 如果这个标志打开 意味着全体更新
            int delete = dropoutRecordMapper.delete(null);
            MajorChangeRecordRO majorChangeRecordRO = new MajorChangeRecordRO();
            majorChangeRecordRO.setRemark("新生转专业");
            int delete1 = majorChangeRecordMapper.deleteNe(majorChangeRecordRO);
            int delete2 = resumptionRecordMapper.delete(null);
            int delete3 = retentionRecordMapper.delete(null);
            int delete4 = suspensionRecordMapper.delete(null);
        }

        ArrayList<HashMap<String, String>> studentStatusChangeData = getStudentStatusChangeData();
        studentStatusChangeDataImport.insertLogsList.add("旧系统学籍异动数据总数 " + studentStatusChangeData.size());

        Integer dropoutRecordCount = dropoutRecordMapper.selectCount(null);
        Integer majorChangeRecordCount = majorChangeRecordMapper.selectCount(new LambdaQueryWrapper<MajorChangeRecordPO>()
                .ne(MajorChangeRecordPO::getRemark, "新生转专业"));
        Integer resumptionRecordCount = resumptionRecordMapper.selectCount(null);
        Integer retentionRecordCount = retentionRecordMapper.selectCount(null);
        Integer suspensionRecordCount = suspensionRecordMapper.selectCount(null);
        Integer integer = dropoutRecordCount + resumptionRecordCount + retentionRecordCount +
                suspensionRecordCount;
        if(updateAny && integer == 0){
            log.info("已全部删除，并开始同步");
        }

        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
        String query = "select count(*) from classdata c1,classdata c2,stuchangedata , " +
                "studentdata where (substr(xhao,1,2)>='07') and (substr(stuchangedata.xhao,1,1)<>'9') " +
                "and (oldbshi=c1.bshi) and (newbshi=c2.bshi) and (stuchangedata.xhao=studentdata.xhao) ";
        Object value = scnuxljyDatabase.getValue(query);

        studentStatusChangeDataImport.insertLogsList.add("同步前新系统中导入的学籍异动总数为 " + integer);
        studentStatusChangeDataImport.insertLogsList.add("旧系统中的学籍异动总数为 " + value);


        for (HashMap<String, String> hashMap : studentStatusChangeData) {

            studentStatusChangeDataImport.queue.put(hashMap); // Put the object in the queue
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("END", "TRUE");
            studentStatusChangeDataImport.queue.put(hashMap);
        }

        studentStatusChangeDataImport.latch.await();

        if (studentStatusChangeDataImport.executorService != null) {
            studentStatusChangeDataImport.executorService.shutdown();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/studentStatusChangeData/";
        String errorFileName = relativePath + currentDateTime + "_"  + "导入学籍异动结果总览.txt";
        studentStatusChangeDataImport.insertLogsList.add("各个学籍异动数据的总数为 " + studentStatusChangeDataImport.getApplyCount());
        log.info("各个学籍异动数据的总数为\n " + studentStatusChangeDataImport.getApplyCount());
        log.info("各个学籍异动插入的数据条数为\n " + studentStatusChangeDataImport.getApplyImportCount());

        dropoutRecordCount = dropoutRecordMapper.selectCount(null);
        majorChangeRecordCount = majorChangeRecordMapper.selectCount(null);
        resumptionRecordCount = resumptionRecordMapper.selectCount(null);
        retentionRecordCount = retentionRecordMapper.selectCount(null);
        suspensionRecordCount = suspensionRecordMapper.selectCount(null);
        integer = dropoutRecordCount + majorChangeRecordCount + resumptionRecordCount + retentionRecordCount +
                suspensionRecordCount;
        studentStatusChangeDataImport.insertLogsList.add("同步后新系统中导入的学籍异动总数为 " + integer);
        studentStatusChangeDataImport.insertLogsList.add("旧系统中的学籍异动总数为 " + value);
        exportListToTxtAndUploadToMinio(studentStatusChangeDataImport.insertLogsList,
                errorFileName, "datasynchronize");
    }

    /**
     * 同步旧系统与新系统的缴费数据
     */
    public void synchronizePaymentInfoData(boolean updateAny, boolean truncateTable, String year) {
        try {
            PaymentInformationDataImport paymentInformationDataImport = new PaymentInformationDataImport();
            paymentInformationDataImport.setUpdateAny(updateAny);
            ArrayList<HashMap<String, String>> studentFees = getStudentFees(year);

            paymentInformationDataImport.insertLogsList.add(year + "年旧系统缴费总数 " + studentFees.size());

            Integer integer = paymentInfoMapper.selectCount(null);
            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            Object value = scnuxljyDatabase.getValue("select count(*) from CWPAY_VIEW");
            paymentInformationDataImport.insertLogsList.add("新系统中导入的总缴费记录数 " + integer);
            paymentInformationDataImport.insertLogsList.add("旧系统中的总缴费记录数 " + value);


            for (HashMap<String, String> hashMap : studentFees) {

                paymentInformationDataImport.queue.put(hashMap); // Put the object in the queue
            }

            // 传递毒药对象
            for (int i = 0; i < CONSUMER_COUNT; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("END", "TRUE");
                paymentInformationDataImport.queue.put(hashMap);
            }

            paymentInformationDataImport.latch.await();

            if (paymentInformationDataImport.executorService != null) {
                paymentInformationDataImport.executorService.shutdown();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/paymentInformationData/";
            String errorFileName = relativePath + currentDateTime + "_" + "导入缴费数据结果总览.txt";
            String errorFileName1 = relativePath + currentDateTime + "_" + "导入缴费数据结果总览日志.txt";
            String errorFileName2 = relativePath + currentDateTime + "_" + "导入缴费数据失败结果集.xlsx";
            paymentInformationDataImport.insertLogsList.add(year + "年插入成功的缴费数据数量为 " + paymentInformationDataImport.getSuccess_insert());
            paymentInformationDataImport.insertLogsList.add(year + "年插入失败的缴费数据数量为 " + paymentInformationDataImport.getFailed_insert());
            exportErrorListToExcelAndUploadToMinio(paymentInformationDataImport.errorList, ErrorPaymentInfoData.class,
                    errorFileName2, "datasynchronize");
            exportMapToTxtAndUploadToMinio(paymentInformationDataImport.updateCountMap, errorFileName1,
                    "datasynchronize");
            exportListToTxtAndUploadToMinio(paymentInformationDataImport.insertLogsList,
                    errorFileName, "datasynchronize");
        }catch (Exception e){
            log.error(year + "年校验同步缴费数据失败 " + e);
        }
    }

    /**
     * 同步旧系统与新系统的缴费数据 区间年份作为输入
     */
    public void synchronizePaymentInfoDataByInterval(boolean updateAny, int startYear, int endYear, List<String> specialYears) {
        try {
            PaymentInformationDataImport paymentInformationDataImport = new PaymentInformationDataImport();
            paymentInformationDataImport.setSpecialYears(specialYears);
            paymentInformationDataImport.setUpdateAny(updateAny);

            for(int i = startYear; i >= endYear; i--){
                ArrayList<HashMap<String, String>> studentFees = getStudentFees(String.valueOf(i));
                paymentInformationDataImport.insertLogsList.add(i + "年旧系统缴费总数 " + studentFees.size());

                Integer integer = paymentInfoMapper.selectCount(null);
                SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
                Object value = scnuxljyDatabase.getValue("select count(*) from CWPAY_VIEW");
                paymentInformationDataImport.insertLogsList.add("新系统中导入的总缴费记录数 " + integer);
                paymentInformationDataImport.insertLogsList.add("旧系统中的总缴费记录数 " + value);

                for (HashMap<String, String> hashMap : studentFees) {

                    paymentInformationDataImport.queue.put(hashMap); // Put the object in the queue
                }
            }

            for(String year : specialYears){
                ArrayList<HashMap<String, String>> studentFees = getStudentFees(year);
                paymentInformationDataImport.insertLogsList.add(year + "年旧系统缴费总数 " + studentFees.size());

                Integer integer = paymentInfoMapper.selectCount(null);
                SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
                Object value = scnuxljyDatabase.getValue("select count(*) from CWPAY_VIEW");
                paymentInformationDataImport.insertLogsList.add("新系统中导入的总缴费记录数 " + integer);
                paymentInformationDataImport.insertLogsList.add("旧系统中的总缴费记录数 " + value);

                for (HashMap<String, String> hashMap : studentFees) {

                    paymentInformationDataImport.queue.put(hashMap); // Put the object in the queue
                }
            }


            // 传递毒药对象
            for (int i = 0; i < CONSUMER_COUNT; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("END", "TRUE");
                paymentInformationDataImport.queue.put(hashMap);
            }

            paymentInformationDataImport.latch.await();

            if (paymentInformationDataImport.executorService != null) {
                paymentInformationDataImport.executorService.shutdown();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/paymentInformationData/";
            String errorFileName = relativePath + currentDateTime + "_" + "导入缴费数据结果总览.txt";
            String errorFileName1 = relativePath + currentDateTime + "_" + "导入缴费数据结果总览日志.txt";
            String errorFileName2 = relativePath + currentDateTime + "_" + "导入缴费数据失败结果集.xlsx";
            paymentInformationDataImport.insertLogsList.add(startYear + " 年 - " + endYear + " 年插入成功的缴费数据数量为 " + paymentInformationDataImport.getSuccess_insert());
            paymentInformationDataImport.insertLogsList.add(startYear + " 年 - " + endYear + "年插入失败的缴费数据数量为 " + paymentInformationDataImport.getFailed_insert());

            exportMapToTxtAndUploadToMinio(paymentInformationDataImport.updateCountMap, errorFileName1,
                    "datasynchronize");
            exportListToTxtAndUploadToMinio(paymentInformationDataImport.insertLogsList,
                    errorFileName, "datasynchronize");
            exportErrorListToExcelAndUploadToMinio(paymentInformationDataImport.errorList, ErrorPaymentInfoData.class,
                    errorFileName2, "datasynchronize");
        }catch (Exception e){
            log.error(startYear + " 年 - " + endYear + "年校验同步缴费数据失败 " + e);
        }
    }


    /**
     * 同步旧系统与新系统的缴费数据 区间年份作为输入
     */
    public void synchronizePaymentInfoDataAll(boolean updateAny, List<String> specialYears) {
        try {
            PaymentInformationDataImport paymentInformationDataImport = new PaymentInformationDataImport();
            paymentInformationDataImport.setSpecialYears(specialYears);
            paymentInformationDataImport.setUpdateAny(updateAny);

            ArrayList<HashMap<String, String>> studentFees = getStudentFeesAll();
            paymentInformationDataImport.insertLogsList.add("旧系统缴费总数 " + studentFees.size());

            Integer integer = paymentInfoMapper.selectCount(null);
            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            Object value = scnuxljyDatabase.getValue("select count(*) from CWPAY_VIEW");
            paymentInformationDataImport.insertLogsList.add("新系统中导入的总缴费记录数 " + integer);
            paymentInformationDataImport.insertLogsList.add("旧系统中的总缴费记录数 " + value);

            for (HashMap<String, String> hashMap : studentFees) {

                paymentInformationDataImport.queue.put(hashMap); // Put the object in the queue
            }


            // 传递毒药对象
            for (int i = 0; i < CONSUMER_COUNT; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("END", "TRUE");
                paymentInformationDataImport.queue.put(hashMap);
            }

            paymentInformationDataImport.latch.await();

            if (paymentInformationDataImport.executorService != null) {
                paymentInformationDataImport.executorService.shutdown();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/paymentInformationData/";
            String errorFileName = relativePath + currentDateTime + "_" + "导入缴费数据结果总览.txt";
            String errorFileName1 = relativePath + currentDateTime + "_" + "导入缴费数据结果总览日志.txt";
            String errorFileName2 = relativePath + currentDateTime + "_" + "导入缴费数据失败结果集.xlsx";
            paymentInformationDataImport.insertLogsList.add("插入成功的缴费数据数量为 " + paymentInformationDataImport.getSuccess_insert());
            paymentInformationDataImport.insertLogsList.add("插入失败的缴费数据数量为 " + paymentInformationDataImport.getFailed_insert());

            exportMapToTxtAndUploadToMinio(paymentInformationDataImport.updateCountMap, errorFileName1,
                    "datasynchronize");
            exportListToTxtAndUploadToMinio(paymentInformationDataImport.insertLogsList,
                    errorFileName, "datasynchronize");
            exportErrorListToExcelAndUploadToMinio(paymentInformationDataImport.errorList, ErrorPaymentInfoData.class,
                    errorFileName2, "datasynchronize");
        }catch (Exception e){
            log.error("校验同步缴费数据失败 " + e);
        }
    }

    /**
     * 同步旧系统与新系统的成绩数据
     */
    public void synchronizeGradeInformationData(int startYear, int endYear, boolean update) throws InterruptedException {

        for(int i = startYear; i >= endYear; i--){
            SCNUXLJYDatabase scnuxljyDatabase = null;

            try {
                GradeInfoDataImport gradeInfoDataImport = new GradeInfoDataImport();
                ArrayList<HashMap<String, String>> gradeInfos = getGradeInfos("" + i);
                gradeInfoDataImport.insertLogs.add("旧系统成绩数据总数 " + gradeInfos.size());

                // 获取新系统中指定年级的成绩记录总数
                Integer integer = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                        eq(ScoreInformationPO::getGrade, "" + i));
                scnuxljyDatabase = new SCNUXLJYDatabase();
                int value = (int) scnuxljyDatabase.getValue("select count(*) from RESULT_VIEW_FULL where nj='" + i + "';");
                // 获取旧系统中非学历教育生的成绩
                int value_fwp = (int) scnuxljyDatabase.getValue("select count(*) from RESULT_VIEW_FULL where bshi LIKE 'WP%' and nj='" + i + "';");
                // 获取旧系统中学历教育生的成绩
                Object value_xl = scnuxljyDatabase.getValue("select count(*) from RESULT_VIEW_FULL where bshi not LIKE 'WP%' and nj='" + i + "';");
                gradeInfoDataImport.insertLogs.add(i + "年新系统中导入的成绩记录数 " + integer);
                gradeInfoDataImport.insertLogs.add(i + "年旧系统中的学历教育生成绩记录总数 " + value);
                gradeInfoDataImport.insertLogs.add(i + "年旧系统中的非学历教育成绩记录总数 " + value_fwp);

                if(update){
                    int delete = scoreInformationMapper.delete(new LambdaQueryWrapper<ScoreInformationPO>()
                            .eq(ScoreInformationPO::getGrade, "" + i));
                    log.info("已删除 " + i + " 年的成绩数据，记录数为 " + delete);
                }

                if(value_xl == integer){
                    // 相等
                    if(update){
                        // 开启强制更新 哪怕记录都相同
                        GradeInfoDataImport.update = true;
                    }else{
                        // 相等 不覆盖  直接跳过
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                        String currentDateTime = LocalDateTime.now().format(formatter);
                        String relativePath = "data_import_error_excel/gradeInformationData/";
                        String errorFileName = relativePath + currentDateTime + "_" + i + "导入成绩结果总览.txt";
                        exportListToTxtAndUploadToMinio(gradeInfoDataImport.insertLogs,
                                errorFileName, "datasynchronize");
                        continue;
                    }
                }

                for (HashMap<String, String> hashMap : gradeInfos) {

                    gradeInfoDataImport.queue.put(hashMap); // Put the object in the queue
                }

                // 传递毒药对象
                for (int j = 0; j < CONSUMER_COUNT; j++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("END", "TRUE");
                    gradeInfoDataImport.queue.put(hashMap);
                }

                gradeInfoDataImport.latch.await();

                if (gradeInfoDataImport.executorService != null) {
                    gradeInfoDataImport.executorService.shutdown();
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String currentDateTime = LocalDateTime.now().format(formatter);
                String relativePath = "data_import_error_excel/gradeInformationData/";
                String errorFileName = relativePath + currentDateTime + "_" + i + "导入成绩结果总览.txt";
                String errorFileName2 = relativePath + currentDateTime + "_" + i + "导入成绩异常结果.xlsx";
                gradeInfoDataImport.insertLogs.add("文凭班成绩总数为 " + gradeInfoDataImport.getFxl_grade_count());
                gradeInfoDataImport.insertLogs.add("非文凭异常成绩总数为 " + gradeInfoDataImport.getError_grade_count());
                exportListToTxtAndUploadToMinio(gradeInfoDataImport.insertLogs,
                        errorFileName, "datasynchronize");
                exportErrorListToExcelAndUploadToMinio(gradeInfoDataImport.errorList, ErrorGradeData.class,
                        errorFileName2, "datasynchronize");
            }catch (Exception e){
                log.info("同步成绩出现问题 " + e.toString());
            }finally {
                scnuxljyDatabase.close();
            }
        }
    }


    /**
     * 统计新旧系统 学籍信息、成绩信息、教学计划、班级信息、成绩信息的异同
     * @return
     */
    public ArrayList<String> calculateStatistics(boolean ident){
        log.info("开始校验新旧两个系统的核心数据差异");
        ArrayList<String> dataCheckLogs = new ArrayList<>();

//        log.info(msg1);
        // 获取当前时间
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 获取当前的方法名 (需要考虑性能开销)
        String methodName = new Throwable().getStackTrace()[0].getMethodName();

        // 获取当前的类名
        String className = this.getClass().getSimpleName();

        String formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统学籍数据的校验");
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        int startYear = 2023;
        int endYear = 2010;
        boolean allEqual = true;
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();

        for(int i = startYear; i >= endYear; i--){
            Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                    StudentStatusPO::getGrade, "" + i
            ));

            int value_xl = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                    "' and bshi not like'WP%';");
            if(value_xl != integer){
                allEqual = false;
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                        integer + ((int)value_xl == integer ? "  一致" : "  不同"));
                if(ident){
                    log.info(formattedMsg);
                }else{
                    dataCheckLogs.add(formattedMsg);
                }

            }

        }

        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的学籍数据完全相等");
            if(ident){
                log.info(formattedMsg);
            }else{
                dataCheckLogs.add(formattedMsg);
            }
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统班级信息对比");
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        Integer new_class_count = classInformationMapper.selectCount(null);
        int old_class_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
        if(new_class_count != old_class_count){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 不同");
            if(ident){
                log.info(formattedMsg);
            }else{
                dataCheckLogs.add(formattedMsg);
            }
        }else{
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 相同");
            if(ident){
                log.info(formattedMsg);
            }else{
                dataCheckLogs.add(formattedMsg);
            }
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统缴费数据对比");
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        for(int payYear = 2023; payYear >= 2000; payYear--){
            Integer new_pay_count = paymentInfoMapper.selectCount(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + payYear));
            int old_pay_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM CWPAY_VIEW WHERE NJ='" + payYear + "'");
            if(new_pay_count != old_pay_count){
                formattedMsg = String.format(payYear +"年 [%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 不同");
                if(ident){
                    log.info(formattedMsg);
                }else{
                    dataCheckLogs.add(formattedMsg);
                }
            }else{
                formattedMsg = String.format(payYear +"年 [%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 相同");
                if(ident){
                    log.info(formattedMsg);
                }else{
                    dataCheckLogs.add(formattedMsg);
                }
            }
        }

        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        startYear = 2023;
        endYear = 2015;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_teachingPlans_count1 = courseInformationMapper.selectCount(new LambdaQueryWrapper<CourseInformationPO>().
                    eq(CourseInformationPO::getGrade, "" + i));
            String year_c = i + "";
            year_c = year_c.substring(year_c.length()-2);
            int old_teachingPlans_count1 = (int) scnuxljyDatabase.getValue(
                    "select count(*) from courseDATA where bshi not LIKE 'WP%' and bshi LIKE '" + year_c + "%';");

            if(new_teachingPlans_count1 != old_teachingPlans_count1){
                allEqual = false;
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i +
                        " 年，新系统学历教育教学计划数量 " + new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                        old_teachingPlans_count1 + " 两者不同");
                if(ident){
                    log.info(formattedMsg);
                }else{
                    dataCheckLogs.add(formattedMsg);
                }
            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
            if(ident){
                log.info(formattedMsg);
            }else{
                dataCheckLogs.add(formattedMsg);
            }
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统成绩数量对比");
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        startYear = 2023;
        endYear = 2015;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                    eq(ScoreInformationPO::getGrade, "" + i));

            int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                    "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年，新系统学历教育成绩数量 " + new_gradeInformation_count + " 旧系统学历教育成绩数量 " +
                    old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count ? " 两者不同" :
                    " 两者相同"));
            if(ident){
                log.info(formattedMsg);
            }else{
                dataCheckLogs.add(formattedMsg);
            }
            if(new_gradeInformation_count != old_gradeInformation_count){
                allEqual = false;
            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
            if(ident){
                log.info(formattedMsg);
            }else{
                dataCheckLogs.add(formattedMsg);
            }
        }

        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统学籍异动数量对比");
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }
        DataImportScnuOldSys dataImportScnuOldSys = new DataImportScnuOldSys();
        ArrayList<HashMap<String, String>> studentStatusData = dataImportScnuOldSys.getStudentStatusChangeData();
        formattedMsg = String.format("旧系统中总学籍异动数量 %s", studentStatusData.size());
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }
        // 使用流来统计 name 属性值为 "复学" 的记录数量
        long count = studentStatusData.stream()
                .filter(map -> "复学".equals(map.get("CTYPE")))
                .count();

        formattedMsg = String.format("旧系统中 '复学' 的学籍异动数量: %s\n" +
                "新系统中 复学 的学籍异动数据 %s", count, resumptionRecordMapper.selectCount(null));
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        long count2 = studentStatusData.stream()
                .filter(map -> "转学".equals(map.get("CTYPE")))
                .count();

        formattedMsg = String.format("旧系统中 转专业 的学籍异动数量: %s\n" +
                "新系统中 转专业 的学籍异动数据 %s", count2,
                majorChangeRecordMapper.selectCount(new LambdaQueryWrapper<MajorChangeRecordPO>()
                        .ne(MajorChangeRecordPO::getRemark, "新生转专业")));
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        long count3 = studentStatusData.stream()
                .filter(map -> "休学".equals(map.get("CTYPE")))
                .count();

        formattedMsg = String.format("旧系统中 休学 的学籍异动数量: %s\n" +
                "新系统中 休学 的学籍异动数据 %s", count3, suspensionRecordMapper.selectCount(null));
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        long count4 = studentStatusData.stream()
                .filter(map -> "退学".equals(map.get("CTYPE")))
                .count();

        formattedMsg = String.format("旧系统中 退学 的学籍异动数量: %s\n" +
                "新系统中 退学 的学籍异动数据 %s", count4, dropoutRecordMapper.selectCount(null));
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        long count5 = studentStatusData.stream()
                .filter(map -> "留级".equals(map.get("CTYPE")))
                .count();

        formattedMsg = String.format("旧系统中 留级 的学籍异动数量: %s\n" +
                "新系统中 留级 的学籍异动数据 %s", count5, retentionRecordMapper.selectCount(null));
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }

        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统新生转专业数量对比");
        if(ident){
            log.info(formattedMsg);
        }else{
            dataCheckLogs.add(formattedMsg);
        }
        for(int insertGrade = 2024; insertGrade >= 2010; insertGrade--){
            String grade = String.valueOf(insertGrade-1);
            if(insertGrade == 2024){
                grade = "-1";
            }
            ArrayList<HashMap<String, String>> studentLuqus = getStudentLuqus(Integer.parseInt(grade));

            // 使用 stream 过滤 CHANGEZYMC 为 null 的数据
            ArrayList<HashMap<String, String>> filteredList = studentLuqus.stream()
                    .filter(map -> (map.get("CHANGEZYMC") != null && !map.get("CHANGEZYMC").equalsIgnoreCase("NULL")) ||
                            (map.get("CHANGETIME") != null && !map.get("CHANGETIME").equalsIgnoreCase("NULL")) ||
                            (map.get("CHANGEXXXS") != null && !map.get("CHANGEXXXS").equalsIgnoreCase("NULL")))
                    .collect(Collectors.toCollection(ArrayList::new));
            formattedMsg = String.format(insertGrade + "年，旧系统中 新生转专业 的学籍异动数量: %s\n" +
                    "新系统中 新生转专业 的学籍异动数据 %s", filteredList.size(), majorChangeRecordMapper.
                    selectCount(new LambdaQueryWrapper<MajorChangeRecordPO>()
                            .eq(MajorChangeRecordPO::getRemark, "新生转专业")
                            .eq(MajorChangeRecordPO::getOldGrade, "" + insertGrade)
                    ));
            if(ident){
                log.info(formattedMsg);
            }else{
                dataCheckLogs.add(formattedMsg);
            }
        }



        if(ident){
            log.info("校验新旧两个系统的核心数据差异完毕");
        }else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/statistics/";
            String errorFileName = relativePath + currentDateTime + "_" + "新旧系统数据同步总览.txt";
            exportListToTxtAndUploadToMinio(dataCheckLogs, errorFileName, "datasynchronize");
            log.info("校验新旧两个系统的核心数据差异完毕，已写入 Minio 日志");
        }
        return dataCheckLogs;
    }


    /**
     * 同步所有旧系统数据，包含学籍、教学计划、学生照片、成绩、班级数据
     */
    @Async
    public void synchronizeAllData(){
        ArrayList<String> dataCheckLogs = new ArrayList<>();
//        log.info(msg1);
        // 获取当前时间
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // 获取当前的方法名 (需要考虑性能开销)
        String methodName = new Throwable().getStackTrace()[0].getMethodName();

        // 获取当前的类名
        String className = this.getClass().getSimpleName();

        String formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统学籍数据的校验");
        dataCheckLogs.add(formattedMsg);

        int startYear = 2023;
        int endYear = 2010;
        boolean allEqual = true;
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();

        for(int i = startYear; i >= endYear; i--){
            Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                    StudentStatusPO::getGrade, "" + i
            ));

            int value_xl = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                    "' and bshi not like 'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                    integer + ((int)value_xl == integer ? "  一致" : "  不同"));
            dataCheckLogs.add(formattedMsg);
            if(value_xl != integer){
                allEqual = false;

                try {
                    int delete = studentStatusMapper.delete(new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, i + ""));
                    if(studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().
                            eq(StudentStatusPO::getGrade, i + "")) == 0){
                        // 成功清除脏数据，同步旧系统最新的数据
                        synchronizeStudentStatusData(i, i, true);
                    }

                }catch (Exception e){
                    formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年同步旧系统学籍数据失败 " + e.toString());
                    dataCheckLogs.add(formattedMsg);
                }
            }

        }

        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的学籍数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统班级信息对比");
        dataCheckLogs.add(formattedMsg);

        Integer new_class_count = classInformationMapper.selectCount(null);
        int old_class_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
        if(new_class_count != old_class_count){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 不同");
            dataCheckLogs.add(formattedMsg);
        }else{
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count + " 相同");
            dataCheckLogs.add(formattedMsg);
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统缴费数据对比");
        dataCheckLogs.add(formattedMsg);

        for(int payYear = 2023; payYear >= 2000; payYear--){
            Integer new_pay_count = paymentInfoMapper.selectCount(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + payYear));
            int old_pay_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM CWPAY_VIEW WHERE NJ='" + payYear + "'");
            if(new_pay_count != old_pay_count){
                formattedMsg = String.format(payYear +"年 [%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 不同");
                dataCheckLogs.add(formattedMsg);
                int delete = paymentInfoMapper.delete(new LambdaQueryWrapper<PaymentInfoPO>().eq(PaymentInfoPO::getGrade, "" + payYear));
                synchronizePaymentInfoData(true, true, String.valueOf(payYear));
            }else{
                formattedMsg = String.format(payYear +"年 [%s] [%s.%s] %s", timeStamp, className, methodName, "新系统学历教育缴费数据 " + new_pay_count + " 旧系统学历教育缴费数据 " + old_pay_count + " 相同");
                dataCheckLogs.add(formattedMsg);
            }
        }
//        Integer new_pay_count = paymentInfoMapper.selectCount(null);
//        int old_pay_count = (int) scnuxljyDatabase.getValue("SELECT count(*) FROM CWPAY_VIEW;");


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
        dataCheckLogs.add(formattedMsg);



        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
        dataCheckLogs.add(formattedMsg);


        startYear = 2023;
        endYear = 2015;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_teachingPlans_count1 = courseInformationMapper.selectCount(new LambdaQueryWrapper<CourseInformationPO>().
                    eq(CourseInformationPO::getGrade, "" + i));
            String year_c = i + "";
            year_c = year_c.substring(year_c.length()-2);
            int old_teachingPlans_count1 = (int) scnuxljyDatabase.getValue(
                    "select count(*) from courseDATA where bshi not LIKE 'WP%' and bshi LIKE '" + year_c + "%';");

            if(new_teachingPlans_count1 != old_teachingPlans_count1){
                allEqual = false;
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i +
                        " 年，新系统学历教育教学计划数量 " + new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                        old_teachingPlans_count1 + " 两者不同");
                dataCheckLogs.add(formattedMsg);
            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
            dataCheckLogs.add(formattedMsg);
        }else{
            // 存在不同，直接将教学计划完全替换
            try {
                synchronizeTeachingPlansData(true, true);
            }catch (Exception e){
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "清除教学计划并同步旧系统数据失败 " + e.toString());
                dataCheckLogs.add(formattedMsg);
            }
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统成绩数量对比");
        dataCheckLogs.add(formattedMsg);

        startYear = 2023;
        endYear = 2015;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                    eq(ScoreInformationPO::getGrade, "" + i));

            int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                    "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年，新系统学历教育成绩数量 " +
                    new_gradeInformation_count + " 旧系统学历教育成绩数量 " +
                    old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count
                    ? " 两者不同" : " 两者相同"));
            dataCheckLogs.add(formattedMsg);

            if(new_gradeInformation_count != old_gradeInformation_count){
                allEqual = false;

                try{
                    int delete = scoreInformationMapper.delete(new LambdaQueryWrapper<ScoreInformationPO>().
                            eq(ScoreInformationPO::getGrade, "" + i));
                    if(scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                            eq(ScoreInformationPO::getGrade, "" + i)) == 0){
                        // 清除成绩
                        synchronizeGradeInformationData(i, i, true);
                    }
                }catch (Exception e){
                    formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年清除成绩并同步旧系统成绩" +
                            "失败 " + e.toString());
                    dataCheckLogs.add(formattedMsg);
                }

            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }

        ArrayList<String> checkLogs = calculateStatistics(false);
        dataCheckLogs.addAll(checkLogs);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/statistics/";
        String errorFileName = relativePath + currentDateTime + "_" + "新旧系统数据同步总览.txt";
        exportListToTxtAndUploadToMinio(dataCheckLogs, errorFileName, "datasynchronize");

        log.info("新旧系统校验同步结束");
        log.info("开始记录校验同步之后的新旧系统的数据差异");
        calculateStatistics(false);

    }

    @Async
    public void printSynchronizeDataCheck(){
        log.info("新旧系统学籍数据的校验");
        int startYear = 2023;
        int endYear = 2010;
        SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();

        for(int i = startYear; i >= endYear; i--){
            Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().eq(
                    StudentStatusPO::getGrade, "" + i
            ));

            Object value_xl = scnuxljyDatabase.getValue("SELECT count(*) FROM STUDENT_VIEW_WITHPIC WHERE NJ='" + i +
                    "' and bshi not like'WP%';");
            log.info(i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " + integer + ((int)value_xl == integer ? "  一致" : "  不同"));
        }

        log.info("新旧系统班级信息对比");
        Integer new_class_count = classInformationMapper.selectCount(null);
        Object old_class_count = scnuxljyDatabase.getValue("SELECT count(*) FROM classdata where bshi not like'WP%';");
        log.info("新系统学历教育班级数量 " + new_class_count + " 旧系统学历教育班级数量 " + old_class_count);

        log.info("新旧系统教学计划对比");
        Integer new_teachingPlans_count = courseInformationMapper.selectCount(null);
        Object old_teachingPlans_count = scnuxljyDatabase.getValue("select count(*) from courseDATA where bshi not LIKE 'WP%';");
        log.info("新系统学历教育教学计划数量 " + new_teachingPlans_count + " 旧系统学历教育教学计划数量 " + old_teachingPlans_count);

        startYear = 2023;
        endYear = 2015;
        boolean allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_teachingPlans_count1 = courseInformationMapper.selectCount(new LambdaQueryWrapper<CourseInformationPO>().
                    eq(CourseInformationPO::getGrade, "" + i));
            String year_c = i + "";
            year_c = year_c.substring(year_c.length()-2);
            int old_teachingPlans_count1 = (int) scnuxljyDatabase.getValue(
                    "select count(*) from courseDATA where bshi not LIKE 'WP%' and bshi LIKE '" + year_c + "%';");

            if(new_teachingPlans_count1 != old_teachingPlans_count1){
                allEqual = false;
                log.info(i + " 年，新系统学历教育教学计划数量 " + new_teachingPlans_count1 + " 旧系统学历教育教学计划数量 " +
                        old_teachingPlans_count1 + " 两者不同");
            }
        }
        if(allEqual){
            log.info("新旧系统 " + startYear + " 年到 " + endYear + " 年的教学计划完全相等");
        }


        log.info("新旧系统成绩数量对比");
        startYear = 2023;
        endYear = 2020;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                    eq(ScoreInformationPO::getGrade, "" + i));

            int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                    "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");

            if(new_gradeInformation_count != old_gradeInformation_count){
                allEqual = false;
                log.info(i + " 年，新系统学历教育教学计划数量 " + new_gradeInformation_count + " 旧系统学历教育教学计划数量 " +
                        old_gradeInformation_count + " 两者不同");
            }
        }
        if(allEqual){
            log.info("新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
        }
    }
}
