package com.scnujxjy.backendpoint.service.InterBase;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
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

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.*;

@Service
@Slf4j
public class OldDataSynchronize {

    public static final int CONSUMER_COUNT = 200;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private ScoreInformationMapper scoreInformationMapper;

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
    public <T> void exportErrorListToExcelAndUploadToMinio(List<T> errorList, Class<T> type, String fileName, String diyBucketName) {
        // Step 1: Write data to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, type).sheet("Sheet1").doWrite(errorList); // 注意，这里直接使用 T.class 是不允许的。我们需要其他方式来传递类的类型。

        // Step 2: Convert ByteArrayOutputStream to ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Step 3: Upload to Minio
        boolean success = minioService.uploadStreamToMinio(inputStream, fileName, diyBucketName);

        if (success) {
            log.info("Successfully uploaded to Minio.");
        } else {
            log.error("Failed to upload to Minio.");
        }

        // Close the streams
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            log.error("Error closing streams: " + e.getMessage());
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
                // 检测新旧系统的成绩数目是否相同，相同则不需要更新
                Integer integer = studentStatusMapper.selectCount(new
                        LambdaQueryWrapper<StudentStatusPO>().eq(StudentStatusPO::getGrade,
                        i));
                log.info(i + "年 新系统中导入的学籍数据记录数 " + integer);
                studentStatusDataImport.updateCountMap.put(i + "年 新系统中导入的学籍数据记录数 ", Long.valueOf(integer));

                scnuxljyDatabase = new SCNUXLJYDatabase();
                Object value = scnuxljyDatabase.getValue("select count(*) from STUDENT_VIEW_WITHPIC where nj='" + i + "'");
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

            if(updateInsert){
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
     * 同步旧系统与新系统的成绩数据
     */
    public void synchronizeGradeInformationData(int startYear, int endYear) throws InterruptedException {

        for(int i = startYear; i >= endYear; i--){
            GradeInfoDataImport gradeInfoDataImport = new GradeInfoDataImport();
            ArrayList<HashMap<String, String>> gradeInfos = getGradeInfos("" + i);
            gradeInfoDataImport.insertLogs.add("旧系统成绩数据总数 " + gradeInfos.size());

            Integer integer = classInformationMapper.selectCount(null);
            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            Object value = scnuxljyDatabase.getValue("select count(*) from RESULT_VIEW_FULL");
            Object value_fwp = scnuxljyDatabase.getValue("select count(*) from RESULT_VIEW_FULL where bshi LIKE 'WP%';");
            gradeInfoDataImport.insertLogs.add("新系统中导入的成绩记录数 " + integer);
            gradeInfoDataImport.insertLogs.add("旧系统中的总成绩记录数 " + value);
            gradeInfoDataImport.insertLogs.add("新系统中的非学历教育成绩记录数 " + value_fwp);


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
            String errorFileName = relativePath + currentDateTime + "_" + i  + "导入成绩结果总览.txt";
            String errorFileName2 = relativePath + currentDateTime + "_" + i  + "导入成绩异常结果.xlsx";
            gradeInfoDataImport.insertLogs.add("文凭班成绩总数为 " + gradeInfoDataImport.getFxl_grade_count());
            gradeInfoDataImport.insertLogs.add("非文凭异常成绩总数为 " + gradeInfoDataImport.getError_grade_count());
            exportListToTxtAndUploadToMinio(gradeInfoDataImport.insertLogs,
                    errorFileName, "datasynchronize");
            exportErrorListToExcelAndUploadToMinio(gradeInfoDataImport.errorList, ErrorGradeData.class,
                    errorFileName2, "datasynchronize");
        }
    }


    /**
     * 统计新旧系统 学籍信息、成绩信息、教学计划、班级信息、成绩信息的异同
     * @return
     */
    public ArrayList<String> calculateStatistics(){
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
                    "' and bshi not like'WP%';");
            if(value_xl != integer){
                allEqual = false;
                formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年旧系统学历教育生数量 " + value_xl + " 新系统学历教育生数量 " +
                        integer + ((int)value_xl == integer ? "  一致" : "  不同"));
                dataCheckLogs.add(formattedMsg);
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
        }


        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统成绩数量对比");
        dataCheckLogs.add(formattedMsg);

        startYear = 2023;
        endYear = 2020;
        allEqual = true;
        for(int i = startYear; i >= endYear; i--){
            Integer new_gradeInformation_count = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().
                    eq(ScoreInformationPO::getGrade, "" + i));

            int old_gradeInformation_count = (int) scnuxljyDatabase.getValue(
                    "select count(*) from RESULT_VIEW_FULL where nj='" + i + "' and bshi not LIKE 'WP%';");
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年，新系统学历教育成绩数量 " + new_gradeInformation_count + " 旧系统学历教育教学计划数量 " +
                    old_gradeInformation_count + (new_gradeInformation_count != old_gradeInformation_count ? " 两者不同" :
                    " 两者相同"));
            dataCheckLogs.add(formattedMsg);
            if(new_gradeInformation_count != old_gradeInformation_count){
                allEqual = false;
            }
        }
        if(allEqual){
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统 " + startYear + " 年到 " + endYear + " 年的成绩数据完全相等");
            dataCheckLogs.add(formattedMsg);
        }

        return dataCheckLogs;
    }


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

        formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, "新旧系统教学计划对比");
        dataCheckLogs.add(formattedMsg);
//        Integer new_teachingPlans_count = courseInformationMapper.selectCount(null);
//        Object old_teachingPlans_count = scnuxljyDatabase.getValue("select count(*) from courseDATA where bshi not LIKE 'WP%';");
//        log.info("新系统学历教育教学计划数量 " + new_teachingPlans_count + " 旧系统学历教育教学计划数量 " + old_teachingPlans_count);

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
            formattedMsg = String.format("[%s] [%s.%s] %s", timeStamp, className, methodName, i + " 年，新系统学历教育教学计划数量 " + new_gradeInformation_count + " 旧系统学历教育教学计划数量 " +
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
                        synchronizeGradeInformationData(i, i);
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

        ArrayList<String> checkLogs = calculateStatistics();
        dataCheckLogs.addAll(checkLogs);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/statistics/";
        String errorFileName = relativePath + currentDateTime + "_" + "新旧系统数据同步总览.txt";
        exportListToTxtAndUploadToMinio(dataCheckLogs, errorFileName, "datasynchronize");
    }
}
