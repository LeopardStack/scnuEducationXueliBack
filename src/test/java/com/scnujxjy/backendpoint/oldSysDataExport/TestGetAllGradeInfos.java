package com.scnujxjy.backendpoint.oldSysDataExport;

import com.alibaba.excel.EasyExcel;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getGradeInfos;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class ErrorData extends ScoreInformationPO {
    private String errorReason;
}

@SpringBootTest
@Slf4j
public class TestGetAllGradeInfos {
    @Autowired(required = false)
    private ScoreInformationMapper scoreInformationMapper;

    @Autowired(required = false)
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

    @Transactional
    public void insertStudentFeesByGrade(int grade, List<ErrorData> errorList) {
        ArrayList<HashMap<String, String>> studentFees = getGradeInfos("" + grade);

        for (HashMap<String, String> studentData : studentFees) {
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


                scoreInformationMapper.insert(scoreInformationPO);
                success_insert += 1;
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
                errorList.add(errorData);
                failed_insert += 1;
            }
        }
        log.info("成功插入 " + success_insert + " 条数据 " + " 失败插入 " + failed_insert + " 条数据 \n" +
                " 总共从旧系统获取 " + String.valueOf(studentFees.size() + " 条数据"));
        log.error("错误数据如下 \n" + errorList);
    }

    /**
     * 导入学生成绩
     */
    @Test
    public void test1() {
        List<ErrorData> errorList = new ArrayList<>();

        /**
         * 2023 - 2020 的成绩信息已全部导入
         * 2019 年的数据需要单独校验
         */
        StringBuilder allGrades = new StringBuilder();
        for (int i = 2018; i >= 2015; i--) {
            insertStudentFeesByGrade(i, errorList);
            allGrades.append(i).append("_");
        }

        // 调用新方法导出errorList
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/studentGrades/";
        String errorFileName = relativePath + currentDateTime + "_" + allGrades + "导入成绩数据失败的部分数据.xlsx";
        exportErrorListToExcel(errorList, errorFileName);
    }
}
