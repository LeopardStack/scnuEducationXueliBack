package com.scnujxjy.backendpoint.dao.mapper.teaching_process;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseCoverChangeRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationManagerZeroVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 课程信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
public interface CourseInformationMapper extends BaseMapper<CourseInformationPO> {
    /**
     * 根据 年级 专业 层次 班级名称 学习形式来获取某一个班级的教学计划
     * @param grade 年级
     * @param major_name 专业名称
     * @param level 层次
     * @param study_form 学习形式
     * @param admin_class 班级标识
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM course_information WHERE grade = #{grade} AND major_name = #{major_name} AND level = #{level}" +
            " AND study_form = #{study_form} AND admin_class = #{admin_class}")
    List<CourseInformationPO> selectCourseInformations1(String grade, String major_name, String level, String study_form,
                                                       String admin_class);

    /**
     * 根据 班级名称 学习形式来获取某一个班级的教学计划
     * @param admin_class 班级标识
     * @param course_code 课程名称编号
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM course_information WHERE admin_class = #{admin_class} AND course_code = #{course_code}")
    List<CourseInformationPO> selectByAdminClassId(String admin_class, String course_code);


    /**
     * 获取学生的教学计划 按照课程代码排序
     * @param id_number 学生的身份证号码
     * @return
     */
    @Select("SELECT g.* " +
            "FROM student_status s " +
            "JOIN course_information g ON s.class_identifier  = g.admin_class " +
            "WHERE s.id_number = #{id_number} " +
            "ORDER BY g.grade DESC, g.course_code ASC")
    List<CourseInformationPO> getStudentTeachingPlans(String id_number);


    /**
     * 根据年级和学院的条件进行联合查询
     * @param grade 年级
     * @return 课程信息集合，类型为 CourseInformationPO
     */
    @Select("SELECT c.* " +
            "FROM course_information c " +
            "JOIN class_information ci ON c.admin_class = ci.class_identifier " +
            "WHERE c.grade = #{grade} AND ci.college = #{college}")
    List<CourseInformationPO> selectByGradeAndCollege(String grade, String college);

    /**
     * 根据学院信息来查询全体学院所有的教学计划
     * @return 课程信息集合，类型为 CourseInformationPO
     */
    @Select("SELECT c.* " +
            "FROM course_information c " +
            "JOIN class_information ci ON c.admin_class = ci.class_identifier " +
            "WHERE ci.college = #{college}")
    List<CourseInformationPO> selectByCollege(String college);

    /**
     * 根据学院信息来查询全体学院所有的教学计划的总数
     * @return 课程信息集合，类型为 CourseInformationPO
     */
    @Select("SELECT count(*) " +
            "FROM course_information c " +
            "JOIN class_information ci ON c.admin_class = ci.class_identifier " +
            "WHERE ci.college = #{college}")
    long getTeachingPlansCount(String college);

    /**
     * 根据年级、专业名称、学习形式、层次、行政班别名称的条件进行联合查询
     * @param grade 年级
     * @return 课程信息集合，类型为 CourseInformationPO
     */
    @Select("SELECT c.* " +
            "FROM course_information c " +
            "JOIN class_information ci ON c.admin_class = ci.class_identifier " +
            "WHERE c.grade = #{grade} AND c.study_form = #{study_form} " +
            "AND c.level = #{level} AND ci.class_name = #{admin_class_name} " +
            "AND c.major_name = #{major_name}")
    List<CourseInformationPO> selectByGradeAndStudyFormAndLevelAndClassAndMajorName(String grade, String study_form,
                                                                        String level, String admin_class_name,
                                                                        String major_name);

    /**
     * 根据年级、专业名称、学习形式、层次、行政班别名称、课程名称的条件进行联合查询
     * @param grade 年级
     * @return 课程信息集合，类型为 CourseInformationPO
     */
    @Select("SELECT c.* " +
            "FROM course_information c " +
            "JOIN class_information ci ON c.admin_class = ci.class_identifier " +
            "WHERE c.grade = #{grade} AND c.study_form = #{study_form} " +
            "AND c.level = #{level} AND ci.class_name = #{admin_class_name} " +
            "AND c.major_name = #{major_name} AND c.course_name = #{course_name}")
    List<CourseInformationPO> selectByGradeAndStudyFormAndLevelAndClassAndMajorNameAndCourseName(
            String grade, String study_form,
            String level, String admin_class_name,
            String major_name, String course_name);


    /**
     * 查询某一个学院的教学计划
     * @param filter
     * @return
     */
    List<CourseInformationVO> selectByFilterAndPage(@Param("filter") CourseInformationRO filter,
                                                    @Param("pageSize") Long pageSize,
                                                    @Param("offset") Long offset);

    /**
     * 查询教学计划中的某一门课程
     * @param filter
     * @return
     */
    CourseInformationVO selectSingleCourse(@Param("filter") CourseCoverChangeRO filter);



    /**
     * 查询某一个学院的教学计划总数
     * @param filter
     * @return
     */
    long getCountByFilterAndPage(@Param("filter") CourseInformationRO filter);

    // 获取年级的筛选参数
    @Select({
            "<script>",
            "SELECT DISTINCT c.grade",
            "FROM course_information c",
            "JOIN class_information ci ON c.admin_class = ci.class_identifier",
            "WHERE 1=1",
            "<if test='college != null and college != \"\"'>",
            "AND ci.college = #{college}",
            "</if>",
            "ORDER BY c.grade DESC", // 从高到低排序
            "</script>"
    })
    List<String> selectDistinctGrades(String college);

    // 获取专业名称的筛选参数
    @Select({
            "<script>",
            "SELECT DISTINCT c.major_name",
            "FROM course_information c",
            "JOIN class_information ci ON c.admin_class = ci.class_identifier",
            "WHERE 1=1",
            "<if test='college != null and college != \"\"'>",
            "AND ci.college = #{college}",
            "</if>",
            "</script>"
    })
    List<String> selectDistinctMajorNames(String college);

    // 获取层次的筛选参数
    @Select({
            "<script>",
            "SELECT DISTINCT c.level",
            "FROM course_information c",
            "JOIN class_information ci ON c.admin_class = ci.class_identifier",
            "WHERE 1=1",
            "<if test='college != null and college != \"\"'>",
            "AND ci.college = #{college}",
            "</if>",
            "</script>"
    })
    List<String> selectDistinctLevels(String college);

    // 获取课程名称的筛选参数
    @Select({
            "<script>",
            "SELECT DISTINCT c.course_name",
            "FROM course_information c",
            "JOIN class_information ci ON c.admin_class = ci.class_identifier",
            "WHERE 1=1",
            "<if test='college != null and college != \"\"'>",
            "AND ci.college = #{college}",
            "</if>",
            "</script>"
    })
    List<String> selectDistinctCourseNames(String college);

    // 获取学习形式的筛选参数
    @Select({
            "<script>",
            "SELECT DISTINCT c.study_form",
            "FROM course_information c",
            "JOIN class_information ci ON c.admin_class = ci.class_identifier",
            "WHERE 1=1",
            "<if test='college != null and college != \"\"'>",
            "AND ci.college = #{college}",
            "</if>",
            "</script>"
    })
    List<String> selectDistinctStudyForms(String college);

    // 获取班级名称的筛选参数
    @Select({
            "<script>",
            "SELECT DISTINCT ci.class_name",
            "FROM course_information c",
            "JOIN class_information ci ON c.admin_class = ci.class_identifier",
            "WHERE 1=1",
            "<if test='college != null and college != \"\"'>",
            "AND ci.college = #{college}",
            "</if>",
            "</script>"
    })
    List<String> selectDistinctClassNames(String college);

    // 获取学院名称的筛选参数
    @Select({
            "<script>",
            "SELECT DISTINCT ci.college",
            "FROM course_information c",
            "JOIN class_information ci ON c.admin_class = ci.class_identifier",
            "</script>"
    })
    List<String> selectDistinctCollegeNames();


    /**
     * 学历教育部查询教学计划
     * @param filter
     * @return
     */
    @Select({
            "<script>",
            "SELECT c.*, ci.class_name, ci.college",
            "FROM course_information c",
            "JOIN class_information ci ON c.admin_class = ci.class_identifier",
            "WHERE 1=1",
            "<if test='filter.college != null and filter.college != \"\"'>",
            "AND ci.college = #{filter.college}",
            "</if>",
            "<if test='filter.grade != null and filter.grade != \"\"'>",
            "AND c.grade = #{filter.grade}",
            "</if>",
            "<if test='filter.majorName != null and filter.majorName != \"\"'>",
            "AND c.major_name = #{filter.majorName}",
            "</if>",
            "<if test='filter.level != null and filter.level != \"\"'>",
            "AND c.level = #{filter.level}",
            "</if>",
            "<if test='filter.studyForm != null and filter.studyForm != \"\"'>",
            "AND c.studyForm = #{filter.studyForm}",
            "</if>",
            "<if test='filter.adminClass != null and filter.adminClass != \"\"'>",
            "AND c.adminClass = #{filter.adminClass}",
            "</if>",
            "<if test='filter.courseName != null and filter.courseName != \"\"'>",
            "AND c.courseName = #{filter.courseName}",
            "</if>",
            "<if test='filter.studyHours != null'>",
            "AND c.studyHours = #{filter.studyHours}",
            "</if>",
            "<if test='filter.assessmentType != null and filter.assessmentType != \"\"'>",
            "AND c.assessmentType = #{filter.assessmentType}",
            "</if>",
            "<if test='filter.teachingMethod != null and filter.teachingMethod != \"\"'>",
            "AND c.teachingMethod = #{filter.teachingMethod}",
            "</if>",
            "<if test='filter.courseType != null and filter.courseType != \"\"'>",
            "AND c.courseType = #{filter.courseType}",
            "</if>",
            "<if test='filter.credit != null'>",
            "AND c.credit = #{filter.credit}",
            "</if>",
            "<if test='filter.teachingSemester != null and filter.teachingSemester != \"\"'>",
            "AND c.teachingSemester = #{filter.teachingSemester}",
            "</if>",
            "<if test='filter.courseCode != null and filter.courseCode != \"\"'>",
            "AND c.course_code = #{filter.courseCode}",
            "</if>",
            "<if test='filter.className != null and filter.className != \"\"'>",
            "AND ci.class_name = #{filter.className}",
            "</if>",
            "ORDER BY c.grade DESC, c.major_name, c.level, c.study_form, c.course_code",
            "<if test='pageSize != null and offset != null'>",
            "LIMIT #{offset}, #{pageSize}",
            "</if>",
            "</script>"
    })
    List<CourseInformationManagerZeroVO> selectByFilterAndPageByManager0(@Param("filter") CourseInformationRO filter, @Param("pageSize") long pageSize,
                                                                         @Param("offset") long offset);


    /**
     * 学历教育部管理查询教学计划总数
     * @param filter
     * @return
     */
    @Select({
            "<script>",
            "SELECT count(*)",
            "FROM course_information c",
            "JOIN class_information ci ON c.admin_class = ci.class_identifier",
            "WHERE 1=1",
            "<if test='filter.college != null and filter.college != \"\"'>",
            "AND ci.college = #{filter.college}",
            "</if>",
            "<if test='filter.grade != null and filter.grade != \"\"'>",
            "AND c.grade = #{filter.grade}",
            "</if>",
            "<if test='filter.majorName != null and filter.majorName != \"\"'>",
            "AND c.major_name = #{filter.majorName}",
            "</if>",
            "<if test='filter.level != null and filter.level != \"\"'>",
            "AND c.level = #{filter.level}",
            "</if>",
            "<if test='filter.studyForm != null and filter.studyForm != \"\"'>",
            "AND c.studyForm = #{filter.studyForm}",
            "</if>",
            "<if test='filter.adminClass != null and filter.adminClass != \"\"'>",
            "AND c.adminClass = #{filter.adminClass}",
            "</if>",
            "<if test='filter.courseName != null and filter.courseName != \"\"'>",
            "AND c.courseName = #{filter.courseName}",
            "</if>",
            "<if test='filter.studyHours != null'>",
            "AND c.studyHours = #{filter.studyHours}",
            "</if>",
            "<if test='filter.assessmentType != null and filter.assessmentType != \"\"'>",
            "AND c.assessmentType = #{filter.assessmentType}",
            "</if>",
            "<if test='filter.teachingMethod != null and filter.teachingMethod != \"\"'>",
            "AND c.teachingMethod = #{filter.teachingMethod}",
            "</if>",
            "<if test='filter.courseType != null and filter.courseType != \"\"'>",
            "AND c.courseType = #{filter.courseType}",
            "</if>",
            "<if test='filter.credit != null'>",
            "AND c.credit = #{filter.credit}",
            "</if>",
            "<if test='filter.teachingSemester != null and filter.teachingSemester != \"\"'>",
            "AND c.teachingSemester = #{filter.teachingSemester}",
            "</if>",
            "<if test='filter.courseCode != null and filter.courseCode != \"\"'>",
            "AND c.course_code = #{filter.courseCode}",
            "</if>",
            "<if test='filter.className != null and filter.className != \"\"'>",
            "AND ci.class_name = #{filter.className}",
            "</if>",
            "</script>"
    })
    long getCountByFilterAndPageManager0(@Param("filter") CourseInformationRO filter);


    /**
     * 清空教学计划表
     */
    @Delete("TRUNCATE TABLE course_information")
    void truncateTable();

    /**
     * 获取指定条件下的年龄（去除重复项）
     * @param filter
     * @return
     */
    List<String> getDistinctGrades(@Param("filter" ) CourseScheduleFilterRO filter);

    /**
     * 获取指定条件下的学院（去除重复项）
     * @param filter
     * @return
     */
    List<String> getDistinctCollegeNames(@Param("filter" ) CourseScheduleFilterRO filter);


    /**
     * 获取指定条件下的学习形式（去除重复项）
     * @param filter
     * @return
     */
    List<String> getDistinctStudyForms(@Param("filter" ) CourseScheduleFilterRO filter);


    /**
     * 获取指定条件下的班级名称（去除重复项）
     * @param filter
     * @return
     */
    List<String> getDistinctClassNames(@Param("filter" ) CourseScheduleFilterRO filter);


    /**
     * 获取指定条件下的层次（去除重复项）
     * @param filter
     * @return
     */
    List<String> getDistinctLevels(@Param("filter" ) CourseScheduleFilterRO filter);

    /**
     * 获取指定条件下的年龄（去除重复项）
     * @param filter
     * @return
     */
    List<String> getDistinctMajorNames(@Param("filter" ) CourseScheduleFilterRO filter);

    /**
     * 获取指定条件下的学期（去除重复项）
     * @param filter
     * @return
     */
    List<String> getDistinctSemasters(@Param("filter" ) CourseScheduleFilterRO filter);

    /**
     * 获取指定条件下的课程名称（去除重复项）
     * @param filter
     * @return
     */
    List<String> getDistinctCourseNames(@Param("filter" ) CourseScheduleFilterRO filter);
}
