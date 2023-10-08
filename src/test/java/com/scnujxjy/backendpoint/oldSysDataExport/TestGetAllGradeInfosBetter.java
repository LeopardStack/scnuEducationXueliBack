package com.scnujxjy.backendpoint.oldSysDataExport;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.util.SCNUXLJYDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getGradeInfos;

@SpringBootTest
@Slf4j
public class TestGetAllGradeInfosBetter {
    @Resource
    private ScoreInformationMapper scoreInformationMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;


    private int success_insert = 0;
    private int failed_insert = 0;

    public void exportErrorListToExcel(List<ErrorData> errorList, String outputPath) {
        EasyExcel.write(outputPath, ErrorData.class).sheet("Error Data").doWrite(errorList);
    }

    private int isDigit(String score) {
        if (score == null || score.trim().length() == 0 || score.trim().equals("NULL")) {
            return -1;
        } else {
            Pattern pattern = Pattern.compile("^\\d*(\\.\\d+)?$");
            Matcher matcher = pattern.matcher(score.trim());
            if(matcher.matches()){
                return 1;
            }else{
                return 0;
            }
        }
    }

    private static final int CONSUMER_COUNT = 200;
    private ExecutorService executorService;

    private BlockingQueue<HashMap<String, String>> queue = new LinkedBlockingQueue<>();  // Unbounded queue

    private CountDownLatch latch;

    List<ErrorData> errorList = new ArrayList<>();

    @Transactional
    int insertData(HashMap<String, String> studentData){
        ScoreInformationPO scoreInformationPO = new ScoreInformationPO();
        ErrorData errorData = new ErrorData();

        try {
            // 请根据实际的字段名和数据类型调整以下代码
            String class_identifier = studentData.get("BSHI");
            String course_id = studentData.get("KCHH");
            if(class_identifier == null){
                throw new RuntimeException("班级标识为空 " + studentData.toString());
            }
            if(course_id == null){
                throw new RuntimeException("课程编号为空 " + studentData.toString());
            }

            if(class_identifier.startsWith("WP")){
                throw new RuntimeException("非学历的成绩记录");
            }

            scoreInformationPO.setStudentId(studentData.get("XHAO"));
            scoreInformationPO.setClassIdentifier(class_identifier);
            scoreInformationPO.setGrade(studentData.get("NJ"));
            scoreInformationPO.setCollege(studentData.get("XI"));
            scoreInformationPO.setMajorName(studentData.get("ZHY"));
            scoreInformationPO.setSemester(studentData.get("XQI"));
            scoreInformationPO.setCourseName(studentData.get("KCHM"));
            scoreInformationPO.setCourseCode(course_id);
            // 选修必须要从教学计划中获得
            List<CourseInformationPO> courseInformationPOS = courseInformationMapper.selectByAdminClassId(class_identifier, course_id);
            String courseType = null;
            if(courseInformationPOS.size() != 1){
                if(courseInformationPOS.size() == 0){
                    throw new RuntimeException("找不到对应的课程 " + studentData.toString());
                }else{
                    throw new RuntimeException("课程代码和班级标识找到了多份课程 " + studentData.toString() + "\n" +
                            courseInformationPOS.toString());
                }

            }else{
                CourseInformationPO courseInformationPO = courseInformationPOS.get(0);
                courseType = courseInformationPO.getCourseType();
            }
            scoreInformationPO.setCourseType(courseType);
            scoreInformationPO.setAssessmentType(studentData.get("FSHI"));
            String zp = studentData.get("ZP");
            if (isDigit(zp) == 1) {
                scoreInformationPO.setFinalScore(zp.trim());
            }else if(isDigit(zp) == -1){
                // 成绩为空
            }else if(isDigit(zp) == 0){
                // 存在成绩字符串，但是属于特殊状态
                scoreInformationPO.setStatus(zp.trim());
            }else{
                throw new RuntimeException("异常的成绩数据 " + zp);
            }

//                scoreInformationPO.setMakeupExam1Score(convertStringToDouble(studentData.get("BK")));

            String bk = studentData.get("BK");
            if (isDigit(bk) == 1) {
                scoreInformationPO.setMakeupExam1Score(bk.trim());
            }else if(isDigit(bk) == -1){
                // 成绩为空
            }else if(isDigit(bk) == 0){
                // 存在成绩字符串，但是属于特殊状态
                scoreInformationPO.setStatus(bk.trim());
            }else{
                throw new RuntimeException("异常的成绩数据 " + bk);
            }

//                scoreInformationPO.setMakeupExam2Score(convertStringToDouble(studentData.get("BK2")));

            String bk2 = studentData.get("BK");
            if (isDigit(bk2) == 1) {
                scoreInformationPO.setMakeupExam2Score(bk2.trim());
            }else if(isDigit(bk2) == -1){
                // 成绩为空
            }else if(isDigit(bk2) == 0){
                // 存在成绩字符串，但是属于特殊状态
                scoreInformationPO.setStatus(bk2.trim());
            }else{
                throw new RuntimeException("异常的成绩数据 " + bk2);
            }

//                scoreInformationPO.setPostGraduationScore(convertStringToDouble(studentData.get("JBK")));
            String jbk = studentData.get("JBK");
            if (isDigit(jbk) == 1) {
                scoreInformationPO.setPostGraduationScore(jbk.trim());
            }else if(isDigit(jbk) == -1){
                // 成绩为空
            }else if(isDigit(jbk) == 0){
                // 存在成绩字符串，但是属于特殊状态
                scoreInformationPO.setStatus(jbk.trim());
            }else{
                throw new RuntimeException("异常的成绩数据 " + jbk);
            }

            String bz = studentData.get("BZ");
            if(bz == null || bz.trim().length() == 0 || bz.equals("NULL")){

            }else{
                scoreInformationPO.setRemarks(studentData.get("BZ"));
            }

            synchronized(this) {
                // 插入之前检查目前的这个实例变量是否已经和数据库中那一条记录是否是摸一样  如果是则不需要插入
                int ident1 = scoreInformationMapper.countByAttributesExceptId(scoreInformationPO);
                if(ident1 == 1){
                    return 0;
                }else if(ident1 == 0){
                    // 没有相同的，查看除了成绩和备注备份是否相同，如果相同则覆盖，不相同则添加一条成绩记录
                    int ident2 = scoreInformationMapper.countBySelectedAttributes(scoreInformationPO);
                    if(ident2 == 1){
                        int i = scoreInformationMapper.updateBySelectedAttributes(scoreInformationPO);
                        if(i == 1){
                            return 0;
                        }else if(i == 0){
                            throw new RuntimeException("发现了一条除成绩部分其他都相同的记录，但是更新失败了 ");
                        }
                        else{
                            throw new RuntimeException("更新成绩失败，更新了多条（大于1） ");
                        }
                    }else if(ident2 == 0){
                        synchronized(this) {
                            scoreInformationMapper.insert(scoreInformationPO);
                            success_insert += 1;
                        }
                    }else{
                        throw new RuntimeException("数据库中存在多条一模一样的成绩记录，除成绩和备注备份其他都一模一样 ");
                    }
                }else{
                    throw new RuntimeException("数据库中存在多条一模一样的成绩记录 ");
                }
            }
            return 0;
        } catch (Exception e) {
            log.error(e.toString());
            errorData.setId((long) failed_insert);
            errorData.setStudentId(studentData.get("XHAO"));
            errorData.setClassIdentifier(studentData.get("BSHI"));
            errorData.setGrade(studentData.get("NJ"));
            errorData.setCollege(studentData.get("XI"));
            errorData.setMajorName(studentData.get("ZHY"));
            errorData.setSemester(studentData.get("XQI"));
            errorData.setCourseName(studentData.get("KCHM"));
            errorData.setCourseCode(studentData.get("KCHH"));
//                errorData.setCourseType(studentData.get("KCHM"));
            errorData.setAssessmentType(studentData.get("FSHI"));
            errorData.setFinalScore(studentData.get("ZP"));
            errorData.setMakeupExam1Score(studentData.get("BK"));
            errorData.setMakeupExam2Score(studentData.get("BK2"));
            errorData.setPostGraduationScore(studentData.get("JBK"));
            errorData.setRemarks(studentData.get("BZ"));
            errorData.setErrorReason(e.toString());
            synchronized(this) {
                errorList.add(errorData);
                failed_insert += 1;
            }
        }
        return -1;
    }

    @PostConstruct
    public void init() {

        latch = new CountDownLatch(CONSUMER_COUNT);
        // 创建消费者线程
        executorService = Executors.newFixedThreadPool(CONSUMER_COUNT);

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            executorService.execute(() -> {
                try {
                    while (true) {
                        HashMap<String, String> hashMap = queue.take();
                        if(hashMap.containsKey("END")){
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

    @PreDestroy
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Test
    public void test1() throws InterruptedException {
        /**
         * 2023 - 2020 的成绩信息已全部导入
         * 2019 年的数据需要单独校验
         */
        StringBuilder allGrades = new StringBuilder();
        for (int i = 2022; i >= 2021; i--) {
            // 检测新旧系统的成绩数目是否相同，相同则不需要更新
            Integer integer = scoreInformationMapper.selectCount(new LambdaQueryWrapper<ScoreInformationPO>().eq(ScoreInformationPO::getGrade,
                    i));
            log.info("新系统中导入的成绩记录数 " + integer);

            SCNUXLJYDatabase scnuxljyDatabase = new SCNUXLJYDatabase();
            Object value = scnuxljyDatabase.getValue("select count(*) from RESULT_VIEW_FULL where nj='" + i + "'");
            Object value_fwp = scnuxljyDatabase.getValue("select count(*) from RESULT_VIEW_FULL where nj='" + i +
                    "' and bshi LIKE 'WP%';");
            boolean updateInsert = true;
            try{
                int oldAll = (int) value;
                int oldFwpAll = (int) value_fwp;
                if(oldFwpAll + oldAll == integer){
                    updateInsert = false;
                }

            }catch (Exception e){
                log.error("获取新旧系统成绩数据失败 " + i + "\n" + e.toString());
            }

            if(updateInsert){
                ArrayList<HashMap<String, String>> gradeInfos = getGradeInfos("" + i);
                for (HashMap<String, String> hashMap : gradeInfos) {

                    queue.put(hashMap); // Put the object in the queue
                }
                allGrades.append(i).append("_");
            }
        }

        // 传递毒药对象
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("END", "TRUE");
            queue.put(hashMap);
        }

        latch.await();

        // 调用新方法导出errorList
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/studentGrades/";
        String errorFileName = relativePath + currentDateTime + "_" + allGrades + "导入成绩数据失败的部分数据.xlsx";
        exportErrorListToExcel(errorList, errorFileName);
    }


}
