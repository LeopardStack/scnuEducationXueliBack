package com.scnujxjy.backendpoint.dao.mapper.teaching_process;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 排课表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-18
 */
@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedulePO> {

    /**
     * 根据 学院来获取所有的教学计划
     *
     * @param collegeName 学院
     * @return 排课表集合，类型为 CourseSchedulePO
     */
    @Select("SELECT cs.* " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<CourseSchedulePO> detailByCollegeName(String collegeName);


    /**
     * 根据 主讲教师姓名来获取所有的教学计划
     *
     * @param main_teacher_name 主讲教师姓名
     * @return 排课表集合，类型为 CourseSchedulePO
     */
    @Select("SELECT * FROM course_schedule WHERE main_teacher_name = #{main_teacher_name}")
    List<CourseSchedulePO> detailByMainTeacherName(String main_teacher_name);

    /**
     * 根据 主讲教师姓名、工号(学号)来获取所有的教学计划
     *
     * @param main_teacher_name 主讲教师姓名
     * @param main_teacher_id 主讲教师工号（学号）
     * @return 排课表集合，类型为 CourseSchedulePO
     */
    @Select("SELECT * FROM course_schedule WHERE main_teacher_name = #{main_teacher_name} AND main_teacher_id = #{main_teacher_id}")
    List<CourseSchedulePO> detailByMainTeacherNameMainTeacherId(String main_teacher_name, String main_teacher_id);

    /**
     * 根据 主讲教师姓名、身份证号码来获取所有的教学计划
     *
     * @param main_teacher_name 主讲教师姓名
     * @param main_teacher_identity 主讲教师身份证号码
     * @return 排课表集合，类型为 CourseSchedulePO
     */
    @Select("SELECT * FROM course_schedule WHERE main_teacher_name = #{main_teacher_name} AND main_teacher_identity = #{main_teacher_identity}")
    List<CourseSchedulePO> detailByMainTeacherName(String main_teacher_name, String main_teacher_identity);

    /**
     * 根据主讲教师姓名和工号（或身份证号码）来更新排课表中的平台账号字段
     *
     * @param main_teacher_name 主讲教师姓名
     * @param main_teacher_id 主讲教师工号（或身份证号码）
     * @param platform_account 教师的平台账号
     */
    @Update("<script>"
            + "UPDATE course_schedule SET teacher_username = #{platform_account} "
            + "WHERE main_teacher_name = #{main_teacher_name} "
            + "<choose>"
            + "<when test='main_teacher_id != null and main_teacher_identity != null'>"
            + "(main_teacher_id = #{main_teacher_id} OR main_teacher_identity = #{main_teacher_identity})"
            + "</when>"
            + "<when test='main_teacher_id != null'>"
            + "AND main_teacher_id = #{main_teacher_id}"
            + "</when>"
            + "<when test='main_teacher_identity != null'>"
            + "AND main_teacher_identity = #{main_teacher_identity}"
            + "</when>"
            + "</choose>"
            + "</script>")
    int updateTeacherPlatformAccount(String main_teacher_name, String main_teacher_id, String main_teacher_identity, String platform_account);

    @Select("SELECT DISTINCT grade " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctGradesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT level " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.level = ci.level " +
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctLevelsByCollegeName(String collegeName);

    @Select("SELECT DISTINCT majorName " +
            "FROM course_schedule cs " +
            "WHERE cs.majorName IS NOT NULL AND ci.college = #{collegeName};")
    List<String> getDistinctMajorsByCollegeName(String collegeName);

    @Select("SELECT DISTINCT teachingClass " +
            "FROM course_schedule cs " +
            "WHERE cs.teachingClass IS NOT NULL AND ci.college = #{collegeName};")
    List<String> getDistinctTeachingClassesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT adminClass " +
            "FROM course_schedule cs " +
            "WHERE cs.adminClass IS NOT NULL AND ci.college = #{collegeName};")
    List<String> getDistinctAdminClassesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT examType " +
            "FROM course_schedule cs " +
            "WHERE cs.examType IS NOT NULL AND ci.college = #{collegeName};")
    List<String> getDistinctExamTypesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT studyForm " +
            "FROM course_schedule cs " +
            "WHERE cs.studyForm IS NOT NULL AND ci.college = #{collegeName};")
    List<String> getDistinctStudyFormsByCollegeName(String collegeName);

    @Select("SELECT DISTINCT courseName " +
            "FROM course_schedule cs " +
            "WHERE cs.courseName IS NOT NULL AND ci.college = #{collegeName};")
    List<String> getDistinctCourseNamesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT mainTeacherName " +
            "FROM course_schedule cs " +
            "WHERE cs.mainTeacherName IS NOT NULL AND ci.college = #{collegeName};")
    List<String> getDistinctMainTeachersByCollegeName(String collegeName);

    @Select("SELECT DISTINCT teachingMethod " +
            "FROM course_schedule cs " +
            "WHERE cs.teachingMethod IS NOT NULL AND ci.college = #{collegeName};")
    List<String> getDistinctTeachingMethodsByCollegeName(String collegeName);


    /**
     * 获取所有行政班别数据（不重复）
     * @return
     */
    @Select("SELECT DISTINCT admin_class " +
            "FROM course_schedule cs " +
            "WHERE cs.admin_class IS NOT NULL;")
    List<String> getAllDistinctAdminClasses();

    /**
     * 获取所有不重复的教学班别数据。
     *
     * @return 一个包含所有不重复教学班别的列表。
     */
    @Select("SELECT DISTINCT teaching_class " +
            "FROM course_schedule cs " +
            "WHERE cs.teaching_class IS NOT NULL;")
    List<String> getAllDistinctTeachingClasses();

    /**
     * 通过 年级、专业名称、层次、学习形式、行政班别、教学班别、课程名称、主讲教师、授课日期、授课时间 来查看是否存在重复的排课
     * @param courseSchedulePO 排课表记录
     * @return
     */
    @Select("SELECT COUNT(*) FROM course_schedule " +
            "WHERE grade = #{grade} " +
            "AND major_name = #{majorName} " +
            "AND level = #{level} " +
            "AND study_form = #{studyForm} " +
            "AND admin_class = #{adminClass} " +
            "AND teaching_class = #{teachingClass} " +
            "AND course_name = #{courseName} " +
            "AND main_teacher_name = #{mainTeacherName} " +
            "AND teaching_date = #{teachingDate} " +
            "AND teaching_time = #{teachingTime}")
    int checkDuplicate(CourseSchedulePO courseSchedulePO);

    /**
     * 通过 年级、专业名称、层次、学习形式、行政班别、教学班别、课程名称、主讲教师、授课日期、授课时间 来获取重复的排课
     * @param courseSchedulePO
     * @return
     */
    @Select("SELECT * FROM course_schedule " +
            "WHERE grade = #{grade} " +
            "AND major_name = #{majorName} " +
            "AND level = #{level} " +
            "AND study_form = #{studyForm} " +
            "AND admin_class = #{adminClass} " +
            "AND teaching_class = #{teachingClass} " +
            "AND course_name = #{courseName} " +
            "AND main_teacher_name = #{mainTeacherName} " +
            "AND teaching_date = #{teachingDate} " +
            "AND teaching_time = #{teachingTime}")
    List<CourseSchedulePO> findDuplicateRecords(CourseSchedulePO courseSchedulePO);


}
