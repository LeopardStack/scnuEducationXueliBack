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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private Double convertStringToDouble(String score) {
        if (score == null || score.trim().length() == 0 || score.trim().equals("NULL")) {
            return null;
        } else {
            return Double.parseDouble(score);
        }
    }

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
                    throw new RuntimeException("找不到对应的课程或者 课程代码和班级标识找到了多份课程 " + studentData.toString());
                }else{
                    CourseInformationPO courseInformationPO = courseInformationPOS.get(0);
                    courseType = courseInformationPO.getCourseType();
                }
                scoreInformationPO.setCourseType(courseType);
                scoreInformationPO.setAssessmentType(studentData.get("FSHI"));
                scoreInformationPO.setFinalScore(convertStringToDouble(studentData.get("ZP")));
                scoreInformationPO.setMakeupExam1Score(convertStringToDouble(studentData.get("BK")));
                scoreInformationPO.setMakeupExam2Score(convertStringToDouble(studentData.get("BK2")));
                scoreInformationPO.setPostGraduationScore(convertStringToDouble(studentData.get("JBK")));
                scoreInformationPO.setRemarks(studentData.get("BZ"));


                scoreInformationMapper.insert(scoreInformationPO);
                success_insert += 1;
            } catch (Exception e) {
                log.error(e.toString());
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

        for (int i = 2023; i > 2022; i--) {
            insertStudentFeesByGrade(i, errorList);
        }

        // 调用新方法导出errorList
        exportErrorListToExcel(errorList, "data_import_error_excel/studentGrades/20230910导入成绩数据失败的部分数据.xlsx");
    }
}
