package com.scnujxjy.backendpoint.dao.mapper.teaching_process;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.bo.teaching_process.CourseScheduleStudentExcelBO;
import com.scnujxjy.backendpoint.model.bo.teaching_process.ScheduleCoursesInformationBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import org.apache.ibatis.annotations.*;

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
            "AND cs.major_name = ci.major_name " +
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
     * @param main_teacher_id   主讲教师工号（学号）
     * @return 排课表集合，类型为 CourseSchedulePO
     */
    @Select("SELECT * FROM course_schedule WHERE main_teacher_name = #{main_teacher_name} AND main_teacher_id = #{main_teacher_id}")
    List<CourseSchedulePO> detailByMainTeacherNameMainTeacherId(String main_teacher_name, String main_teacher_id);

    /**
     * 根据 主讲教师姓名、身份证号码来获取所有的教学计划
     *
     * @param main_teacher_name     主讲教师姓名
     * @param main_teacher_identity 主讲教师身份证号码
     * @return 排课表集合，类型为 CourseSchedulePO
     */
    @Select("SELECT * FROM course_schedule WHERE main_teacher_name = #{main_teacher_name} AND main_teacher_identity = #{main_teacher_identity}")
    List<CourseSchedulePO> detailByMainTeacherNameIdNumber(String main_teacher_name, String main_teacher_identity);

    /**
     * 根据主讲教师姓名和工号（或身份证号码）来更新排课表中的平台账号字段
     *
     * @param main_teacher_name 主讲教师姓名
     * @param main_teacher_id   主讲教师工号（或身份证号码）
     * @param platform_account  教师的平台账号
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

    /**
     * 获取指定学院排课表中的年级数（不重复）
     *
     * @param collegeName
     * @return
     */
    @Select("SELECT DISTINCT cs.grade " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctGradesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT cs.level " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctLevelsByCollegeName(String collegeName);

    @Select("SELECT DISTINCT cs.major_name " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctMajorsByCollegeName(String collegeName);

    @Select("SELECT DISTINCT cs.teaching_class " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctTeachingClassesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT cs.admin_class " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctAdminClassesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT cs.exam_type " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctExamTypesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT cs.study_form " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctStudyFormsByCollegeName(String collegeName);

    @Select("SELECT DISTINCT cs.course_name " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctCourseNamesByCollegeName(String collegeName);

    @Select("SELECT DISTINCT cs.main_teacher_name " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctMainTeachersByCollegeName(String collegeName);

    @Select("SELECT DISTINCT cs.teaching_method " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<String> getDistinctTeachingMethodsByCollegeName(String collegeName);

    /**
     * 获取指定学院的排课表记录
     *
     * @param collegeName
     * @return
     */
    @Select("SELECT cs.* " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.major_name = ci.major_name " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<CourseScheduleRO> getDistinctCourseSchedulesByCollegeName(String collegeName);


    /**
     * 动态条件查询指定学院的排课表记录
     *
     * @param collegeName
     * @param ro
     * @return
     */

    @SelectProvider(type = CourseScheduleSqlProvider.class, method = "getCourseSchedulesByConditions")
    List<TeacherCourseScheduleVO> getCourseSchedulesByConditions(@Param("collegeName") String collegeName, @Param("ro") PageRO<CourseScheduleRO> ro);

    /**
     * 动态条件查询指定学院的排课表记录总数目
     *
     * @param collegeName
     * @param ro
     * @return
     */

    @SelectProvider(type = CourseScheduleSqlProvider.class, method = "countCourseSchedulesByConditions")
    long countCourseSchedulesByConditions(@Param("collegeName") String collegeName, @Param("ro") PageRO<CourseScheduleRO> ro);


    /**
     * 动态条件选择 教师的排课表
     *
     * @param teacher_username
     * @param ro
     * @return
     */
//    @SelectProvider(type = CourseScheduleSqlProvider.class, method = "getCourseSchedulesByTeacherUserName")
    List<TeacherCourseScheduleVO> getCourseSchedulesByTeacherUserName(@Param("teacher_username") String teacher_username,
                                                                      @Param("entity") CourseScheduleRO entity,
                                                                      @Param("pageNumber") Long pageNumber,
                                                                      @Param("pageSize") Long pageSize);


    /**
     * 选择最近的排课记录
     *
     * @param teacher_username
     * @return
     */
    List<TeacherCourseScheduleVO> getCourseSchedulesByTeacherUserNameRecent(@Param("teacher_username") String teacher_username,
                                                                            @Param("pageNumber") Long pageNumber,
                                                                            @Param("pageSize") Long pageSize);

    List<TeacherCourseScheduleVO> getCourseSchedulesByTeacherUserNameRecentBetter(@Param("teacher_username") String teacher_username);

    /**
     * 获取助教的所有排课表记录
     *
     * @param username
     * @return
     */
    List<TeacherCourseScheduleVO> getCourseSchedulesByTutor(@Param("username") String username);

    long getCourseSchedulesByTeacherUserNameRecentCount(@Param("teacher_username") String teacher_username);

    /**
     * 动态条件查询指定学生的排课表记录总数目
     *
     * @param class_information 班级信息
     * @param ro                筛选条件
     * @return
     */

    @SelectProvider(type = CourseScheduleSqlProvider.class, method = "countCourseSchedulesByStudentIdNumber")
    long countCourseSchedulesByStudentIdNumber(@Param("class_information") ClassInformationPO class_information, @Param("ro") PageRO<CourseScheduleRO> ro);

    /**
     * 动态条件查询指定学生的排课表记录
     *
     * @param class_information 班级信息
     * @param ro                筛选条件
     * @return
     */
    @SelectProvider(type = CourseScheduleSqlProvider.class, method = "getCourseSchedulesByStudentIdNumber")
    List<TeacherCourseScheduleVO> getCourseSchedulesByStudentIdNumber(@Param("class_information") ClassInformationPO class_information,
                                                                      @Param("ro") PageRO<CourseScheduleRO> ro);

    /**
     * 动态条件查询指定老师的排课表记录总数目
     *
     * @param teacher_username
     * @param ro
     * @return
     */

//    @SelectProvider(type = CourseScheduleSqlProvider.class, method = "countCourseSchedulesByTeacherUserName")
    long countCourseSchedulesByTeacherUserName(@Param("teacher_username") String teacher_username, @Param("entity") CourseScheduleRO entity);

    /**
     * 根据 年级、专业名称、层次、学习形式、行政班别、主讲教师名字、授课日期、授课时间是否在数据库中
     * 存在一模一样的记录，存在则说明排课重复，需要选择是否覆盖
     *
     * @param courseSchedulePO 导入的排课表记录
     * @return
     */
    @Update("UPDATE course_schedule " +
            "SET grade = #{grade}, " +
            "major_name = #{majorName}, " +
            "level = #{level}, " +
            "study_form = #{studyForm}, " +
            "admin_class = #{adminClass}, " +
            "teaching_class = #{teachingClass}, " +
            "student_count = #{studentCount}, " +
            "course_name = #{courseName}, " +
            "class_hours = #{classHours}, " +
            "exam_type = #{examType}, " +
            "main_teacher_name = #{mainTeacherName}, " +
            "main_teacher_id = #{mainTeacherId}, " +
            "main_teacher_identity = #{mainTeacherIdentity}, " +
            "tutor_name = #{tutorName}, " +
            "tutor_id = #{tutorId}, " +
            "tutor_identity = #{tutorIdentity}, " +
            "teaching_method = #{teachingMethod}, " +
            "class_location = #{classLocation}, " +
            "online_platform = #{onlinePlatform}, " +
            "teaching_date = #{teachingDate}, " +
            "teaching_time = #{teachingTime}, " +
            "teacher_username = #{teacherUsername} " +
            "WHERE grade = #{grade} " +
            "AND major_name = #{majorName} " +
            "AND level = #{level} " +
            "AND study_form = #{studyForm} " +
            "AND admin_class = #{adminClass} " +
            "AND main_teacher_name = #{mainTeacherName} " +
            "AND teaching_date = #{teachingDate} " +
            "AND teaching_time = #{teachingTime}")
    int updateCourseScheduleByConditions(CourseSchedulePO courseSchedulePO);


    /**
     * 获取所有行政班别数据（不重复）
     *
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
     * 通过 年级、专业名称、层次、学习形式、行政班别、课程名称、主讲教师、授课日期、授课时间 来查看是否存在重复的排课
     *
     * @param courseSchedulePO 排课表记录
     * @return
     */
    @Select("SELECT COUNT(*) FROM course_schedule " +
            "WHERE grade = #{grade} " +
            "AND major_name = #{majorName} " +
            "AND level = #{level} " +
            "AND study_form = #{studyForm} " +
            "AND admin_class = #{adminClass} " +
            "AND course_name = #{courseName} " +
            "AND main_teacher_name = #{mainTeacherName} " +
            "AND teaching_date = #{teachingDate} " +
            "AND teaching_time = #{teachingTime}")
    int checkDuplicate(CourseSchedulePO courseSchedulePO);

    /**
     * 通过 年级、专业名称、层次、学习形式、行政班别、教学班别、课程名称、主讲教师、授课日期、授课时间 来获取重复的排课
     *
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


    @Select("SELECT * " +
            "FROM course_schedule " +
            "WHERE teaching_date >= CURRENT_DATE AND teaching_date < DATE_ADD(CURRENT_DATE, INTERVAL #{ daysAhead } DAY);")
    List<CourseSchedulePO> findRecentRecords(int daysAhead);

    @Select("SELECT * " +
            "FROM course_schedule " +
            "WHERE " +
            "TIMESTAMP(teaching_date, SUBSTRING_INDEX(teaching_time, '-', 1)) BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL #{hour} HOUR) " +
            "OR " +
            "TIMESTAMP(teaching_date, SUBSTRING_INDEX(teaching_time, '-', -1)) BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL #{hour} HOUR);")
    List<CourseSchedulePO> findRecordsWithinCertainHour(@Param("hour") int hour);


    @Select("SELECT * " +
            "FROM course_schedule " +
            "WHERE teaching_date = (SELECT MIN(teaching_date) " +
            "FROM course_schedule " +
            "WHERE teaching_date >= CURRENT_DATE);")
    List<CourseSchedulePO> findminRecords();


    /**
     * 获取排课表课程信息
     *
     * @param courseScheduleFilterROPageRO
     * @param pageSize
     * @param pageNumber
     * @return
     */
    List<ScheduleCourseInformationVO> selectCoursesInformation(@Param("courseScheduleFilterROPageRO")
                                                               CourseScheduleFilterRO courseScheduleFilterROPageRO,
                                                               @Param("pageSize") long pageSize, @Param("pageNumber") long pageNumber);

    /**
     * 获取排课表信息 不需要提供分页信息
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    List<ScheduleCourseInformationVO> selectCoursesInformationWithoutPage(@Param("courseScheduleFilterROPageRO")
                                                                          CourseScheduleFilterRO courseScheduleFilterROPageRO);


    /**
     * 根据教学计划获取排课表中相对应的课程信息
     *
     * @param courseScheduleFilterROPageRO
     * @param pageSize
     * @param pageNumber
     * @return
     */
    List<ScheduleCourseInformationVO> selectCoursesInformationSchedule(@Param("courseScheduleFilterROPageRO")
                                                                       CourseScheduleFilterRO courseScheduleFilterROPageRO,
                                                                       @Param("pageSize") long pageSize, @Param("pageNumber") long pageNumber);

    long countCoursesInformation(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);

    long selectCoursesInformationCount(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);


    /**
     * 获取排课表里面的年级筛选参数
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    List<String> getDistinctGrades(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);

    /**
     * 获取排课表里面的学院筛选参数
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */

    List<String> getDistinctCollegeNames(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);

    /**
     * 获取排课表里面的学习形式筛选参数
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    List<String> getDistinctStudyForms(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);

    /**
     * 获取排课表里的班级名称的筛选参数
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    List<String> getDistinctClassNames(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);

    /**
     * 获取排课表里的专业名称筛选参数
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    List<String> getDistinctMajorNames(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);

    /**
     * 获取排课表里面的层次筛选参数
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    List<String> getDistinctLevels(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);


    /**
     * 获取排课表的教学班别筛选参数
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    List<String> getDistinctTeachingClasses(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);

    /**
     * 获取排课表中的课程筛选参数
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    List<String> getDistinctCourseNames(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO courseScheduleFilterROPageRO);


    /**
     * 获取排课表详细信息
     *
     * @param courseScheduleFilterROPageRO
     * @param pageSize
     * @param pageNumber
     * @return
     */
    List<SchedulesVO> selectSchedulesInformation(@Param("courseScheduleFilterROPageRO")
                                                 CourseScheduleFilterRO courseScheduleFilterROPageRO,
                                                 @Param("pageSize") long pageSize, @Param("pageNumber") long pageNumber);

    List<SchedulesVO> selectTeachingPointSchedulesInformation(@Param("courseScheduleFilterROPageRO")
                                                              CourseScheduleFilterRO courseScheduleFilterROPageRO,
                                                              @Param("pageSize") long pageSize, @Param("pageNumber") long pageNumber);

    Long selectTeachingPointSchedulesInformationCount(@Param("courseScheduleFilterROPageRO")
                                                      CourseScheduleFilterRO courseScheduleFilterROPageRO);

    /**
     * 根据学生身份证号码获取所在班级的全部课程
     * 问题：学生存在多身份证号码一起读 因此优先获取最新年份的
     *
     * @param studentIdNumber 学生身份证号码
     * @return
     */
    List<CourseInformationPO> selectStudentAllCourses(@Param("studentIdNumber") String studentIdNumber);

    /**
     * 获取指定教师所有的排课信息
     *
     * @param teacherUsername 教师用户名
     * @return
     */
    List<CourseSchedulePO> selectTeacherAllCourses(@Param("teacherUsername") String teacherUsername);

    /**
     * 获取指定教师指定条件下的所有的课程信息
     *
     * @param entity
     * @param l
     * @param pageSize
     * @return
     */
    List<TeacherCoursesVO> selectTeacherCoursesWithoutDate(@Param("entity") CourseScheduleRO entity,
                                                           @Param("pageNumber") long l, @Param("pageSize") Long pageSize);

    /**
     * 获取指定教师指定条件下的课程数量
     *
     * @param entity
     * @return
     */
    long selectTeacherCoursesWithoutDateCount(@Param("entity") CourseScheduleRO entity);

    /**
     * 将 onlinePlatform 字段更新为 null
     *
     * @param id 要更新的记录的ID
     * @return 更新的记录数
     */
    Long updateOnlinePlatformToNull(Long id);

    List<TeacherCourseScheduleVO> getCourseSchedulesByStudentInfoRecent(@Param("entity") CourseScheduleRO entity,
                                                                        @Param("pageNumber") Long pageNumber,
                                                                        @Param("pageSize") Long pageSize);

    long getCourseSchedulesByStudentInfoRecentCount(@Param("entity") CourseScheduleRO entity);

    List<TeacherCourseScheduleVO> getCourseSchedulesByStudentInfo(@Param("entity") CourseScheduleRO entity,
                                                                  @Param("pageNumber") Long pageNumber,
                                                                  @Param("pageSize") Long pageSize);

    long countCourseSchedulesByStudentInfo(@Param("entity") CourseScheduleRO entity);

    /**
     * 获取排课表课程管理信息
     *
     * @param entity
     * @return
     */
    List<ScheduleCoursesInformationBO> getScheduleCoursesInformation(@Param("courseScheduleFilterROPageRO") CourseScheduleFilterRO entity);

    /**
     * 根据批次id获取学生信息
     *
     * @param batchIndex 批次id
     * @return
     */
    List<CourseScheduleStudentExcelBO> getStudentInformationBatchIndex(@Param("batch_index") Long batchIndex);

    @Select("SELECT MAX(batch_index) FROM course_schedule")
    long selectMaxBitch();

    @Select("SELECT distinct online_platform FROM course_schedule WHERE main_teacher_name = #{mainTeacherName} and main_teacher_id = #{mainTeacherId} ")
    List<String> selectByNameAndWorkNumber(String mainTeacherName, String mainTeacherId);

    List<ScheduleCourseInformationVO> selectAllCoursesInformationWithoutPage(@Param("courseScheduleFilterROPageRO")
                                                                             CourseScheduleFilterRO courseScheduleFilterROPageRO);

    List<CourseScheduleDetailVO> selectAllCourseScheduleInformationWithoutPage(@Param("courseScheduleFilterROPageRO")
                                                                               CourseScheduleFilterRO courseScheduleFilterROPageRO);
}