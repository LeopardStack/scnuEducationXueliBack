package com.scnujxjy.backendpoint.dao.mapper.teaching_process;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationCommendation;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationDownloadVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 成绩信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-09-10
 */
public interface ScoreInformationMapper extends BaseMapper<ScoreInformationPO> {

    /**
     * 获取学生的成绩信息 按照年级和课程代码排序
     *
     * @param id_number 学生的身份证号码
     * @return
     */
    @Select("SELECT g.* " +
            "FROM student_status s " +
            "JOIN score_information g ON s.student_number = g.student_id " +
            "WHERE s.id_number = #{id_number} " +
            "ORDER BY s.grade DESC, g.course_code ASC")
    List<ScoreInformationPO> getGradeInfo(String id_number);


    /**
     * 获取除了主键之外其他属性都与指定的一个 ScoreInformationPO 实例变量相等的记录条数
     *
     * @param scoreInformation
     * @return
     */
    @Select({
            "<script>",
            "SELECT COUNT(*) FROM score_information WHERE",
            "((student_id = #{studentId} OR (student_id IS NULL AND #{studentId} IS NULL)) AND",
            "(class_identifier = #{classIdentifier} OR (class_identifier IS NULL AND #{classIdentifier} IS NULL)) AND",
            "(grade = #{grade} OR (grade IS NULL AND #{grade} IS NULL)) AND",
            "(college = #{college} OR (college IS NULL AND #{college} IS NULL)) AND",
            "(major_name = #{majorName} OR (major_name IS NULL AND #{majorName} IS NULL)) AND",
            "(semester = #{semester} OR (semester IS NULL AND #{semester} IS NULL)) AND",
            "(course_name = #{courseName} OR (course_name IS NULL AND #{courseName} IS NULL)) AND",
            "(course_code = #{courseCode} OR (course_code IS NULL AND #{courseCode} IS NULL)) AND",
            "(course_type = #{courseType} OR (course_type IS NULL AND #{courseType} IS NULL)) AND",
            "(assessment_type = #{assessmentType} OR (assessment_type IS NULL AND #{assessmentType} IS NULL)) AND",
            "(final_score = #{finalScore} OR (final_score IS NULL AND #{finalScore} IS NULL)) AND",
            "(makeup_exam1_score = #{makeupExam1Score} OR (makeup_exam1_score IS NULL AND #{makeupExam1Score} IS NULL)) AND",
            "(makeup_exam2_score = #{makeupExam2Score} OR (makeup_exam2_score IS NULL AND #{makeupExam2Score} IS NULL)) AND",
            "(post_graduation_score = #{postGraduationScore} OR (post_graduation_score IS NULL AND #{postGraduationScore} IS NULL)) AND",
            "(remarks = #{remarks} OR (remarks IS NULL AND #{remarks} IS NULL)) AND",
            "(status = #{status} OR (status IS NULL AND #{status} IS NULL)))",
            "</script>"
    })
    int countByAttributesExceptId(ScoreInformationPO scoreInformation);


    /**
     * 获取除了主键、总评成绩、补考1成绩、补考2成绩、结业后补考成绩、备注信息以及状态属性之外，
     * 其他属性都与指定的一个 ScoreInformationPO 实例变量相等的记录条数
     *
     * @param scoreInformation
     * @return
     */
    @Select({
            "<script>",
            "SELECT COUNT(*) FROM score_information WHERE",
            "(student_id = #{studentId} OR (student_id IS NULL AND #{studentId} IS NULL)) AND",
            "(class_identifier = #{classIdentifier} OR (class_identifier IS NULL AND #{classIdentifier} IS NULL)) AND",
            "(grade = #{grade} OR (grade IS NULL AND #{grade} IS NULL)) AND",
            "(college = #{college} OR (college IS NULL AND #{college} IS NULL)) AND",
            "(major_name = #{majorName} OR (major_name IS NULL AND #{majorName} IS NULL)) AND",
            "(semester = #{semester} OR (semester IS NULL AND #{semester} IS NULL)) AND",
            "(course_name = #{courseName} OR (course_name IS NULL AND #{courseName} IS NULL)) AND",
            "(course_code = #{courseCode} OR (course_code IS NULL AND #{courseCode} IS NULL)) AND",
            "(course_type = #{courseType} OR (course_type IS NULL AND #{courseType} IS NULL)) AND",
            "(assessment_type = #{assessmentType} OR (assessment_type IS NULL AND #{assessmentType} IS NULL))",
            "</script>"
    })
    int countBySelectedAttributes(ScoreInformationPO scoreInformation);


    /**
     * 指定一个 ScoreInformationPO 实例变量，把与它除了主键、总评成绩、补考1成绩、补考2成绩、结业后补考成绩、
     * 备注信息以及状态属性之外其他属性都相等的记录全更新
     *
     * @param scoreInformation
     * @return
     */
    @Update({
            "<script>",
            "UPDATE score_information SET",
            "final_score = #{finalScore},",
            "makeup_exam1_score = #{makeupExam1Score},",
            "makeup_exam2_score = #{makeupExam2Score},",
            "post_graduation_score = #{postGraduationScore},",
            "remarks = #{remarks},",
            "status = #{status}",
            "WHERE",
            "(student_id = #{studentId} OR (student_id IS NULL AND #{studentId} IS NULL)) AND",
            "(class_identifier = #{classIdentifier} OR (class_identifier IS NULL AND #{classIdentifier} IS NULL)) AND",
            "(grade = #{grade} OR (grade IS NULL AND #{grade} IS NULL)) AND",
            "(college = #{college} OR (college IS NULL AND #{college} IS NULL)) AND",
            "(major_name = #{majorName} OR (major_name IS NULL AND #{majorName} IS NULL)) AND",
            "(semester = #{semester} OR (semester IS NULL AND #{semester} IS NULL)) AND",
            "(course_name = #{courseName} OR (course_name IS NULL AND #{courseName} IS NULL)) AND",
            "(course_code = #{courseCode} OR (course_code IS NULL AND #{courseCode} IS NULL)) AND",
            "(course_type = #{courseType} OR (course_type IS NULL AND #{courseType} IS NULL)) AND",
            "(assessment_type = #{assessmentType} OR (assessment_type IS NULL AND #{assessmentType} IS NULL))",
            "</script>"
    })
    int updateBySelectedAttributes(ScoreInformationPO scoreInformation);


    /**
     * 获取数据库中指定年级是否拥有除了主键、总评成绩、补考1成绩、补考2成绩、结业后补考成绩及备注信息与状态以外，其他字段都一样的数据
     *
     * @return
     */
    @Select({
            "<script>",
            "SELECT student_id, class_identifier, college, major_name, semester, course_name, course_code, course_type, assessment_type ",
            "FROM score_information ",
            "WHERE grade = #{grade} ",
            "GROUP BY student_id, class_identifier, college, major_name, semester, course_name, course_code, course_type, assessment_type ",
            "HAVING COUNT(*) > 1",
            "</script>"
    })
    List<ScoreInformationPO> selectDuplicateRecordsByGrade(String grade);

    /**
     * 获取成绩数据
     *
     * @param entity
     * @param pageSize
     * @return
     */
    List<ScoreInformationVO> getStudentGradeInfoByFilter(@Param("entity") ScoreInformationFilterRO entity,
                                                         @Param("pageSize") Long pageSize, @Param("l") long l);

    /**
     * 获取成绩数据
     *
     * @param entity
     * @param pageSize
     * @return
     */
    List<ScoreInformationVO> getTeachingPointStudentGradeInfoByFilter(@Param("entity") ScoreInformationFilterRO entity,
                                                                      @Param("pageSize") Long pageSize, @Param("l") long l);

    /**
     * 获取成绩数据
     *
     * @param entity
     * @param pageSize
     * @return
     */
    Long getTeachingPointStudentGradeInfoByFilterCount(@Param("entity") ScoreInformationFilterRO entity);

    /**
     * 获取成绩数据计数
     *
     * @param entity
     * @return
     */
    long getCountStudentGradeInfoByFilter(@Param("entity") ScoreInformationFilterRO entity);


    /**
     * 返回年级的筛选参数列表
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT pi.grade " +
            "FROM score_information pi " +
            "LEFT JOIN class_information ci ON pi.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentId != null'>AND pi.studentId = #{entity.studentId} </if>" +
            "<if test='entity.grade != null'>AND pi.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND pi.college = #{entity.college} </if>" +
            "<if test='entity.majorName != null'>AND pi.major_name = #{entity.majorName} </if>" +

            "<if test='entity.semester != null'>AND pi.semester = #{entity.semester} </if>" +
            "<if test='entity.courseName != null'>AND pi.course_name = #{entity.courseName} </if>" +
            "<if test='entity.courseCode != null'>AND pi.course_code = #{entity.courseCode} </if>" +
            "<if test='entity.className != null'>AND ci.class_name LIKE CONCAT(#{entity.className}, '%') </if>" +

            "<if test='entity.courseType != null'>AND pi.course_type = #{entity.courseType} </if>" +
            "<if test='entity.assessmentType != null'>AND pi.assessment_type = #{entity.assessmentType} </if>" +
            "</script>")
    List<String> getDistinctGrades(@Param("entity") ScoreInformationFilterRO entity);

    /**
     * 返回学院的筛选参数列表
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ci.college " +
            "FROM score_information pi " +
            "LEFT JOIN class_information ci ON pi.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentId != null'>AND pi.studentId = #{entity.studentId} </if>" +
            "<if test='entity.grade != null'>AND pi.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND pi.college = #{entity.college} </if>" +
            "<if test='entity.majorName != null'>AND pi.major_name = #{entity.majorName} </if>" +

            "<if test='entity.semester != null'>AND pi.semester = #{entity.semester} </if>" +
            "<if test='entity.courseName != null'>AND pi.course_name = #{entity.courseName} </if>" +
            "<if test='entity.courseCode != null'>AND pi.course_code = #{entity.courseCode} </if>" +
            "<if test='entity.className != null'>AND ci.class_name LIKE CONCAT(#{entity.className}, '%') </if>" +

            "<if test='entity.courseType != null'>AND pi.course_type = #{entity.courseType} </if>" +
            "<if test='entity.assessmentType != null'>AND pi.assessment_type = #{entity.assessmentType} </if>" +
            "</script>")
    List<String> getDistinctCollegeNames(@Param("entity") ScoreInformationFilterRO entity);

    /**
     * 返回层次的筛选参数列表
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ci.level " +
            "FROM score_information pi " +
            "LEFT JOIN class_information ci ON pi.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentId != null'>AND pi.studentId = #{entity.studentId} </if>" +
            "<if test='entity.grade != null'>AND pi.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND pi.college = #{entity.college} </if>" +
            "<if test='entity.majorName != null'>AND pi.major_name = #{entity.majorName} </if>" +

            "<if test='entity.semester != null'>AND pi.semester = #{entity.semester} </if>" +
            "<if test='entity.courseName != null'>AND pi.course_name = #{entity.courseName} </if>" +
            "<if test='entity.courseCode != null'>AND pi.course_code = #{entity.courseCode} </if>" +
            "<if test='entity.className != null'>AND ci.class_name LIKE CONCAT(#{entity.className}, '%') </if>" +

            "<if test='entity.courseType != null'>AND pi.course_type = #{entity.courseType} </if>" +
            "<if test='entity.assessmentType != null'>AND pi.assessment_type = #{entity.assessmentType} </if>" +
            "</script>")
    List<String> getDistinctLevels(@Param("entity") ScoreInformationFilterRO entity);

    /**
     * 返回学习形式的筛选参数列表
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ci.study_form " +
            "FROM score_information pi " +
            "LEFT JOIN class_information ci ON pi.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentId != null'>AND pi.studentId = #{entity.studentId} </if>" +
            "<if test='entity.grade != null'>AND pi.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND pi.college = #{entity.college} </if>" +
            "<if test='entity.majorName != null'>AND pi.major_name = #{entity.majorName} </if>" +

            "<if test='entity.semester != null'>AND pi.semester = #{entity.semester} </if>" +
            "<if test='entity.courseName != null'>AND pi.course_name = #{entity.courseName} </if>" +
            "<if test='entity.courseCode != null'>AND pi.course_code = #{entity.courseCode} </if>" +
            "<if test='entity.className != null'>AND ci.class_name LIKE CONCAT(#{entity.className}, '%') </if>" +

            "<if test='entity.courseType != null'>AND pi.course_type = #{entity.courseType} </if>" +
            "<if test='entity.assessmentType != null'>AND pi.assessment_type = #{entity.assessmentType} </if>" +
            "</script>")
    List<String> getDistinctStudyForms(@Param("entity") ScoreInformationFilterRO entity);

    /**
     * 返回班级名称的筛选参数列表
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ci.class_name " +
            "FROM score_information pi " +
            "LEFT JOIN class_information ci ON pi.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentId != null'>AND pi.studentId = #{entity.studentId} </if>" +
            "<if test='entity.grade != null'>AND pi.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND pi.college = #{entity.college} </if>" +
            "<if test='entity.majorName != null'>AND pi.major_name = #{entity.majorName} </if>" +

            "<if test='entity.semester != null'>AND pi.semester = #{entity.semester} </if>" +
            "<if test='entity.courseName != null'>AND pi.course_name = #{entity.courseName} </if>" +
            "<if test='entity.courseCode != null'>AND pi.course_code = #{entity.courseCode} </if>" +
            "<if test='entity.className != null'>AND ci.class_name LIKE CONCAT(#{entity.className}, '%') </if>" +

            "<if test='entity.courseType != null'>AND pi.course_type = #{entity.courseType} </if>" +
            "<if test='entity.assessmentType != null'>AND pi.assessment_type = #{entity.assessmentType} </if>" +
            "</script>")
    List<String> getDistinctClassNames(@Param("entity") ScoreInformationFilterRO entity);

    /**
     * 返回课程名称的筛选参数列表
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT pi.course_name " +
            "FROM score_information pi " +
            "LEFT JOIN class_information ci ON pi.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentId != null'>AND pi.studentId = #{entity.studentId} </if>" +
            "<if test='entity.grade != null'>AND pi.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND pi.college = #{entity.college} </if>" +
            "<if test='entity.majorName != null'>AND pi.major_name = #{entity.majorName} </if>" +

            "<if test='entity.semester != null'>AND pi.semester = #{entity.semester} </if>" +
            "<if test='entity.courseName != null'>AND pi.course_name = #{entity.courseName} </if>" +
            "<if test='entity.courseCode != null'>AND pi.course_code = #{entity.courseCode} </if>" +
            "<if test='entity.className != null'>AND ci.class_name LIKE CONCAT(#{entity.className}, '%') </if>" +

            "<if test='entity.courseType != null'>AND pi.course_type = #{entity.courseType} </if>" +
            "<if test='entity.assessmentType != null'>AND pi.assessment_type = #{entity.assessmentType} </if>" +
            "</script>")
    List<String> getDistinctCourseNames(@Param("entity") ScoreInformationFilterRO entity);

    /**
     * 返回专业名称的筛选参数列表
     *
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ci.major_name " +
            "FROM score_information pi " +
            "LEFT JOIN class_information ci ON pi.class_identifier = ci.class_identifier " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND pi.id = #{entity.id} </if>" +
            "<if test='entity.studentId != null'>AND pi.studentId = #{entity.studentId} </if>" +
            "<if test='entity.grade != null'>AND pi.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND pi.college = #{entity.college} </if>" +
            "<if test='entity.majorName != null'>AND pi.major_name = #{entity.majorName} </if>" +

            "<if test='entity.semester != null'>AND pi.semester = #{entity.semester} </if>" +
            "<if test='entity.courseName != null'>AND pi.course_name = #{entity.courseName} </if>" +
            "<if test='entity.courseCode != null'>AND pi.course_code = #{entity.courseCode} </if>" +
            "<if test='entity.className != null'>AND ci.class_name LIKE CONCAT(#{entity.className}, '%') </if>" +

            "<if test='entity.courseType != null'>AND pi.course_type = #{entity.courseType} </if>" +
            "<if test='entity.assessmentType != null'>AND pi.assessment_type = #{entity.assessmentType} </if>" +
            "</script>")
    List<String> getDistinctMajorNames(@Param("entity") ScoreInformationFilterRO entity);

    /**
     * 返回专业名称的筛选参数列表
     *
     * @param entity
     * @return
     */

    List<String> getDistinctStatus(@Param("entity") ScoreInformationFilterRO entity);


    /**
     * 获取筛选条件的成绩下载数据
     *
     * @param entity
     * @return
     */
    List<ScoreInformationDownloadVO> downloadScoreInformationDataByManager0(@Param("entity") ScoreInformationFilterRO entity);


    List<ScoreInformationCommendation> scoreInformationAward(@Param("student_id") String studentId);
}
