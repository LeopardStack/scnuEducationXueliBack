package com.scnujxjy.backendpoint.dao.mapper.registration_record_card;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusTeacherFilterRO;
import com.scnujxjy.backendpoint.model.vo.home.StatisticTableForStudentStatus;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.StudentStatusAllVO;
import com.scnujxjy.backendpoint.service.home.StatisticTableForGraduation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学籍信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
public interface StudentStatusMapper extends BaseMapper<StudentStatusPO> {
    /**
     * 根据 学生的身份证号码查找其学籍信息
     * @param idNumber 身份证号码
     * @return 学籍信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM student_status WHERE id_number = #{idNumber}")
    List<StudentStatusVO> selectStudentByidNumber(String idNumber);

    /**
     * 根据 学生的身份证号码和年级查找其学籍信息
     * @param idNumber 身份证号码
     * @param grade 年级
     * @return 学籍信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM student_status WHERE id_number = #{idNumber} and grade = #{grade}")
    StudentStatusVO selectStudentByidNumberGrade(String idNumber, String grade);


    /**
     * 根据 学生身份证号码获取其全部的学生学籍信息
     * @param grade 年级
     * @param college 学院
     * @return 学籍信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM student_status WHERE grade = #{grade} AND college = #{college}")
    List<StudentStatusPO> selectStudentsByGradeCollege(String grade, String college);

    /**
     * 根据 年级获取其全部的教学计划
     * @param grade 年级
     * @return 学籍信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM student_status WHERE grade = #{grade}")
    List<StudentStatusPO> selectStudentsByGrade(String grade);


    List<StudentStatusAllVO> selectByFilterAndPageByManager0(@Param("entity") StudentStatusFilterRO entity, @Param("pageSize") Long pageSize, @Param("l") long l);

    /**
     * 根据筛选条件获取学籍数据总数
     * @param entity
     * @return
     */
    long getCountByFilterAndPageManager0(@Param("entity") StudentStatusFilterRO entity);


    /**
     * 根据筛选条件下载所有学籍数据，不考虑分页
     * @param entity
     * @return
     */
    List<StudentStatusAllVO> downloadStudentStatusDataByManager0(@Param("entity") StudentStatusFilterRO entity);


    /**
     * 根据筛选条件获取筛选条件年级
     * 如果是二级学院教务员登录，则加一个学院的字段限制
     * 如果是教学点教务员登录，则加一个教学点，即它所管辖的班别，来进行限制
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.grade " +
            "FROM student_status ss " +
            "LEFT JOIN graduation_info gi ON ss.student_number = gi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND ss.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND ss.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.grade != null'>AND ss.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND ss.college = #{entity.college} </if>" +
            "<if test='entity.teachingPoint != null'>AND ss.teaching_point = #{entity.teachingPoint} </if>" +
            "<if test='entity.majorName != null'>AND ss.major_name = #{entity.majorName} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyDuration != null'>AND ss.study_duration = #{entity.studyDuration} </if>" +
            "<if test='entity.admissionNumber != null'>AND ss.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.academicStatus != null'>AND ss.academic_status = #{entity.academicStatus} </if>" +
            "<if test='entity.enrollmentDate != null'>AND ss.enrollment_date = #{entity.enrollmentDate} </if>" +
            "<if test='entity.idNumber != null'>AND ss.id_number = #{entity.idNumber} </if>" +
            "<if test='entity.classIdentifier != null'>AND ss.class_identifier = #{entity.classIdentifier} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "<if test='entity.graduationDate != null'>AND gi.graduation_date = #{entity.graduationDate} </if>" +
            "ORDER BY ss.grade DESC" +
            "</script>")
    List<String> getDistinctGrades(@Param("entity") StudentStatusFilterRO entity);

    /**
     * 根据筛选条件获取筛选条件层次
     * 如果是二级学院教务员登录，则加一个学院的字段限制
     * 如果是教学点教务员登录，则加一个教学点，即它所管辖的班别，来进行限制
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.level " +
            "FROM student_status ss " +
            "LEFT JOIN graduation_info gi ON ss.student_number = gi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND ss.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND ss.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.grade != null'>AND ss.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND ss.college = #{entity.college} </if>" +
            "<if test='entity.teachingPoint != null'>AND ss.teaching_point = #{entity.teachingPoint} </if>" +
            "<if test='entity.majorName != null'>AND ss.major_name = #{entity.majorName} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyDuration != null'>AND ss.study_duration = #{entity.studyDuration} </if>" +
            "<if test='entity.admissionNumber != null'>AND ss.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.academicStatus != null'>AND ss.academic_status = #{entity.academicStatus} </if>" +
            "<if test='entity.enrollmentDate != null'>AND ss.enrollment_date = #{entity.enrollmentDate} </if>" +
            "<if test='entity.idNumber != null'>AND ss.id_number = #{entity.idNumber} </if>" +
            "<if test='entity.classIdentifier != null'>AND ss.class_identifier = #{entity.classIdentifier} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "<if test='entity.graduationDate != null'>AND gi.graduation_date = #{entity.graduationDate} </if>" +
            "ORDER BY ss.level" +
            "</script>")
    List<String> getDistinctLevels(@Param("entity") StudentStatusFilterRO entity);

    /**
     * 根据筛选条件获取筛选条件学院
     * 如果是二级学院教务员登录，则加一个学院的字段限制
     * 如果是教学点教务员登录，则加一个教学点，即它所管辖的班别，来进行限制
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.college " +
            "FROM student_status ss " +
            "LEFT JOIN graduation_info gi ON ss.student_number = gi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND ss.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND ss.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.grade != null'>AND ss.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND ss.college = #{entity.college} </if>" +
            "<if test='entity.teachingPoint != null'>AND ss.teaching_point = #{entity.teachingPoint} </if>" +
            "<if test='entity.majorName != null'>AND ss.major_name = #{entity.majorName} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyDuration != null'>AND ss.study_duration = #{entity.studyDuration} </if>" +
            "<if test='entity.admissionNumber != null'>AND ss.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.academicStatus != null'>AND ss.academic_status = #{entity.academicStatus} </if>" +
            "<if test='entity.enrollmentDate != null'>AND ss.enrollment_date = #{entity.enrollmentDate} </if>" +
            "<if test='entity.idNumber != null'>AND ss.id_number = #{entity.idNumber} </if>" +
            "<if test='entity.classIdentifier != null'>AND ss.class_identifier = #{entity.classIdentifier} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "<if test='entity.graduationDate != null'>AND gi.graduation_date = #{entity.graduationDate} </if>" +
            "ORDER BY ss.college" +
            "</script>")
    List<String> getDistinctColleges(@Param("entity") StudentStatusFilterRO entity);

    /**
     * 根据筛选条件获取筛选条件专业名称
     * 如果是二级学院教务员登录，则加一个学院的字段限制
     * 如果是教学点教务员登录，则加一个教学点，即它所管辖的班别，来进行限制
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.major_name " +
            "FROM student_status ss " +
            "LEFT JOIN graduation_info gi ON ss.student_number = gi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND ss.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND ss.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.grade != null'>AND ss.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND ss.college = #{entity.college} </if>" +
            "<if test='entity.teachingPoint != null'>AND ss.teaching_point = #{entity.teachingPoint} </if>" +
            "<if test='entity.majorName != null'>AND ss.major_name = #{entity.majorName} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyDuration != null'>AND ss.study_duration = #{entity.studyDuration} </if>" +
            "<if test='entity.admissionNumber != null'>AND ss.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.academicStatus != null'>AND ss.academic_status = #{entity.academicStatus} </if>" +
            "<if test='entity.enrollmentDate != null'>AND ss.enrollment_date = #{entity.enrollmentDate} </if>" +
            "<if test='entity.idNumber != null'>AND ss.id_number = #{entity.idNumber} </if>" +
            "<if test='entity.classIdentifier != null'>AND ss.class_identifier = #{entity.classIdentifier} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "<if test='entity.graduationDate != null'>AND gi.graduation_date = #{entity.graduationDate} </if>" +
            "ORDER BY ss.major_name" +
            "</script>")
    List<String> getDistinctMajorNames(@Param("entity") StudentStatusFilterRO entity);

    /**
     * 根据筛选条件获取筛选条件学习形式
     * 如果是二级学院教务员登录，则加一个学院的字段限制
     * 如果是教学点教务员登录，则加一个教学点，即它所管辖的班别，来进行限制
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.study_form " +
            "FROM student_status ss " +
            "LEFT JOIN graduation_info gi ON ss.student_number = gi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND ss.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND ss.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.grade != null'>AND ss.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND ss.college = #{entity.college} </if>" +
            "<if test='entity.teachingPoint != null'>AND ss.teaching_point = #{entity.teachingPoint} </if>" +
            "<if test='entity.majorName != null'>AND ss.major_name = #{entity.majorName} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyDuration != null'>AND ss.study_duration = #{entity.studyDuration} </if>" +
            "<if test='entity.admissionNumber != null'>AND ss.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.academicStatus != null'>AND ss.academic_status = #{entity.academicStatus} </if>" +
            "<if test='entity.enrollmentDate != null'>AND ss.enrollment_date = #{entity.enrollmentDate} </if>" +
            "<if test='entity.idNumber != null'>AND ss.id_number = #{entity.idNumber} </if>" +
            "<if test='entity.classIdentifier != null'>AND ss.class_identifier = #{entity.classIdentifier} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "<if test='entity.graduationDate != null'>AND gi.graduation_date = #{entity.graduationDate} </if>" +
            "ORDER BY ss.study_form" +
            "</script>")
    List<String> getDistinctStudyForms(@Param("entity") StudentStatusFilterRO entity);

    /**
     * 根据筛选条件获取筛选条件行政班别
     * 如果是二级学院教务员登录，则加一个学院的字段限制
     * 如果是教学点教务员登录，则加一个教学点，即它所管辖的班别，来进行限制
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ci.class_name " +
            "FROM student_status ss " +
            "LEFT JOIN graduation_info gi ON ss.student_number = gi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND ss.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND ss.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.grade != null'>AND ss.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND ss.college = #{entity.college} </if>" +
            "<if test='entity.teachingPoint != null'>AND ss.teaching_point = #{entity.teachingPoint} </if>" +
            "<if test='entity.majorName != null'>AND ss.major_name = #{entity.majorName} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyDuration != null'>AND ss.study_duration = #{entity.studyDuration} </if>" +
            "<if test='entity.admissionNumber != null'>AND ss.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.academicStatus != null'>AND ss.academic_status = #{entity.academicStatus} </if>" +
            "<if test='entity.enrollmentDate != null'>AND ss.enrollment_date = #{entity.enrollmentDate} </if>" +
            "<if test='entity.idNumber != null'>AND ss.id_number = #{entity.idNumber} </if>" +
            "<if test='entity.classIdentifier != null'>AND ss.class_identifier = #{entity.classIdentifier} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "<if test='entity.graduationDate != null'>AND gi.graduation_date = #{entity.graduationDate} </if>" +
            "ORDER BY ci.class_name" +
            "</script>")
    List<String> getDistinctClassNames(@Param("entity") StudentStatusFilterRO entity);

    /**
     * 根据筛选条件获取筛选条件学制
     * 如果是二级学院教务员登录，则加一个学院的字段限制
     * 如果是教学点教务员登录，则加一个教学点，即它所管辖的班别，来进行限制
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.study_duration " +
            "FROM student_status ss " +
            "LEFT JOIN graduation_info gi ON ss.student_number = gi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND ss.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND ss.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.grade != null'>AND ss.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND ss.college = #{entity.college} </if>" +
            "<if test='entity.teachingPoint != null'>AND ss.teaching_point = #{entity.teachingPoint} </if>" +
            "<if test='entity.majorName != null'>AND ss.major_name = #{entity.majorName} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyDuration != null'>AND ss.study_duration = #{entity.studyDuration} </if>" +
            "<if test='entity.admissionNumber != null'>AND ss.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.academicStatus != null'>AND ss.academic_status = #{entity.academicStatus} </if>" +
            "<if test='entity.enrollmentDate != null'>AND ss.enrollment_date = #{entity.enrollmentDate} </if>" +
            "<if test='entity.idNumber != null'>AND ss.id_number = #{entity.idNumber} </if>" +
            "<if test='entity.classIdentifier != null'>AND ss.class_identifier = #{entity.classIdentifier} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "<if test='entity.graduationDate != null'>AND gi.graduation_date = #{entity.graduationDate} </if>" +
            "ORDER BY ss.study_duration" +
            "</script>")
    List<String> getDistinctStudyDurations(@Param("entity") StudentStatusFilterRO entity);

    /**
     * 根据筛选条件获取筛选条件学籍状态
     * 如果是二级学院教务员登录，则加一个学院的字段限制
     * 如果是教学点教务员登录，则加一个教学点，即它所管辖的班别，来进行限制
     * @param entity
     * @return
     */
    @Select("<script>" +
            "SELECT DISTINCT ss.academic_status " +
            "FROM student_status ss " +
            "LEFT JOIN graduation_info gi ON ss.student_number = gi.student_number " +
            "LEFT JOIN class_information ci ON ss.class_identifier = ci.class_identifier " +
            "LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade " +
            "WHERE 1=1 " +
            "<if test='entity.id != null'>AND ss.id = #{entity.id} </if>" +
            "<if test='entity.studentNumber != null'>AND ss.student_number = #{entity.studentNumber} </if>" +
            "<if test='entity.grade != null'>AND ss.grade = #{entity.grade} </if>" +
            "<if test='entity.college != null'>AND ss.college = #{entity.college} </if>" +
            "<if test='entity.teachingPoint != null'>AND ss.teaching_point = #{entity.teachingPoint} </if>" +
            "<if test='entity.majorName != null'>AND ss.major_name = #{entity.majorName} </if>" +
            "<if test='entity.studyForm != null'>AND ss.study_form = #{entity.studyForm} </if>" +
            "<if test='entity.level != null'>AND ss.level = #{entity.level} </if>" +
            "<if test='entity.studyDuration != null'>AND ss.study_duration = #{entity.studyDuration} </if>" +
            "<if test='entity.admissionNumber != null'>AND ss.admission_number = #{entity.admissionNumber} </if>" +
            "<if test='entity.academicStatus != null'>AND ss.academic_status = #{entity.academicStatus} </if>" +
            "<if test='entity.enrollmentDate != null'>AND ss.enrollment_date = #{entity.enrollmentDate} </if>" +
            "<if test='entity.idNumber != null'>AND ss.id_number = #{entity.idNumber} </if>" +
            "<if test='entity.classIdentifier != null'>AND ss.class_identifier = #{entity.classIdentifier} </if>" +
            "<if test='entity.className != null'>AND ci.class_name = #{entity.className} </if>" +
            "<if test='entity.graduationDate != null'>AND gi.graduation_date = #{entity.graduationDate} </if>" +
            "ORDER BY ss.academic_status" +
            "</script>")
    List<String> getDistinctAcademicStatuss(@Param("entity") StudentStatusFilterRO entity);



    @Select("<script>" +
            "SELECT ss.grade, " +
            "       COUNT(DISTINCT ss.student_number) AS student_count, " +
            "       COUNT(DISTINCT gi.student_number) AS graduation_count, " +
            "       COUNT(DISTINCT di.student_number) AS degree_count " +
            "FROM student_status ss " +
            "LEFT JOIN graduation_info gi ON ss.student_number = gi.student_number AND gi.graduation_number IS NOT NULL AND gi.graduation_date IS NOT NULL " +
            "LEFT JOIN degree_info di ON ss.student_number = di.student_number " +
            "WHERE ss.grade BETWEEN #{startYear} AND #{endYear} " +
            "GROUP BY ss.grade " +
            "ORDER BY ss.grade ASC" +
            "</script>")
    List<Map<String, StatisticTableForStudentStatus>> getCountOfStudentStatus(@Param("startYear") String startYear, @Param("endYear") String endYear);


    @Select("<script>" +
            "SELECT EXTRACT(YEAR FROM gi.graduation_date) AS graduation_year, " +
            "       COUNT(DISTINCT CASE WHEN MONTH(gi.graduation_date) = 7 THEN gi.student_number END) AS july_graduation_count, " +
            "       COUNT(DISTINCT CASE WHEN MONTH(gi.graduation_date) = 1 THEN gi.student_number END) AS january_graduation_count, " +
            "       COUNT(DISTINCT gi.student_number) AS annual_graduation_count " +
            "FROM graduation_info gi " +
            "WHERE EXTRACT(YEAR FROM gi.graduation_date) BETWEEN #{startYear} AND #{endYear} " +
            "GROUP BY EXTRACT(YEAR FROM gi.graduation_date) " +
            "ORDER BY EXTRACT(YEAR FROM gi.graduation_date) ASC" +
            "</script>")
    List<Map<String, StatisticTableForGraduation>> getCountOfGraduation(@Param("startYear") int startYear, @Param("endYear") int endYear);


  //    @Select("<script>" +
//            " SELECT  pi.name,pi.id_number FROM course_schedule cs" +
//            " LEFT JOIN class_information coi" +
//            " ON cs.grade = coi.grade and cs.major_name=coi.major_name and cs.study_form=coi.study_form" +
//            " and cs.admin_class=coi.class_name and cs.level=coi.level" +
//            " LEFT JOIN student_status ss ON coi.class_identifier = ss.class_identifier" +
//            " LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade" +
//            " WHERE pi.id_number is not null  " +
//            "<if test='entity.grade != null'>AND cs.grade = #{entity.grade} </if>" +
//            "<if test='entity.majorName != null'>AND cs.major_name = #{entity.majorName} </if>" +
//            "<if test='entity.studyForm != null'>AND cs.study_form = #{entity.studyForm} </if>" +
//            "<if test='entity.courseName != null'>AND cs.course_name = #{entity.courseName} </if>" +
//            "<if test='entity.level != null'>AND cs.level = #{entity.level} </if>"+
//            "</script>")
    @Select("<script>" +
            " SELECT DISTINCT pi.name,pi.id_number FROM course_schedule cs" +
            " LEFT JOIN class_information coi" +
            " ON cs.admin_class=coi.class_name and  cs.major_name=coi.major_name and cs.grade = coi.grade and cs.study_form=coi.study_form" +
            " and cs.level=coi.level" +
            " LEFT JOIN student_status ss ON coi.class_identifier = ss.class_identifier" +
            " LEFT JOIN personal_info pi ON ss.id_number = pi.id_number AND ss.grade = pi.grade" +
            " WHERE pi.id_number is not null  " +
            "<if test='entity.adminClass != null'>AND cs.admin_class = #{entity.adminClass} </if>" +
            "<if test='entity.grade != null'>AND cs.grade = #{entity.grade} </if>" +
            "<if test='entity.majorName != null'>AND cs.major_name = #{entity.majorName} </if>" +
            "<if test='entity.studyForm != null'>AND cs.study_form = #{entity.studyForm} </if>" +
            "<if test='entity.level != null'>AND cs.level = #{entity.level} </if>"+
            "</script>")
    List<Map<String,String>> getScheduleClassStudent(@Param("entity") CourseSchedulePO courseSchedulePO);

    List<StudentStatusAllVO> getStudentStatusInfoByTeacher(@Param("entity")StudentStatusTeacherFilterRO entity,
                                                           @Param("pageNumber")Long pageNumber, @Param("pageSize")long pageSize);

    long getStudentStatusInfoByTeacherCount(@Param("entity") StudentStatusTeacherFilterRO entity);

    /**
     * 获取教学点的所有学生信息
     * @param entity
     * @param pageSize
     * @param l
     * @return
     */
    List<StudentStatusAllVO> selectByFilterAndPageByTeachingPoint(@Param("entity") StudentStatusFilterRO entity, @Param("pageSize") Long pageSize, @Param("l") long l);

    long selectByFilterAndPageByTeachingPointCount(@Param("entity") StudentStatusFilterRO entity);
}

