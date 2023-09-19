package com.scnujxjy.backendpoint.dao.mapper.teaching_process;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;

import java.util.Map;

public class CourseScheduleSqlProvider {

    private String commonConditionSql(PageRO<CourseScheduleRO> ro) {
        StringBuilder conditionSql = new StringBuilder();
        CourseScheduleRO entity = ro.getEntity();

        if (entity.getGrade() != null && !entity.getGrade().isEmpty()) {
            conditionSql.append("AND cs.grade = #{ro.entity.grade} ");
        }
        if (entity.getLevel() != null && !entity.getLevel().isEmpty()) {
            conditionSql.append("AND cs.level = #{ro.entity.level} ");
        }
        if (entity.getMajorName() != null && !entity.getMajorName().isEmpty()) {
            conditionSql.append("AND cs.major_name = #{ro.entity.majorName} ");
        }
        if (entity.getTeachingClass() != null && !entity.getTeachingClass().isEmpty()) {
            conditionSql.append("AND cs.teaching_class = #{ro.entity.teachingClass} ");
        }
        if (entity.getAdminClass() != null && !entity.getAdminClass().isEmpty()) {
            conditionSql.append("AND cs.admin_class = #{ro.entity.adminClass} ");
        }
        if (entity.getExamType() != null && !entity.getExamType().isEmpty()) {
            conditionSql.append("AND cs.exam_type = #{ro.entity.examType} ");
        }
        if (entity.getStudyForm() != null && !entity.getStudyForm().isEmpty()) {
            conditionSql.append("AND cs.study_form = #{ro.entity.studyForm} ");
        }
        if (entity.getCourseName() != null && !entity.getCourseName().isEmpty()) {
            conditionSql.append("AND cs.course_name = #{ro.entity.courseName} ");
        }
        if (entity.getMainTeacherName() != null && !entity.getMainTeacherName().isEmpty()) {
            conditionSql.append("AND cs.main_teacher_name = #{ro.entity.mainTeacherName} ");
        }
        if (entity.getTeachingMethod() != null && !entity.getTeachingMethod().isEmpty()) {
            conditionSql.append("AND cs.teaching_method = #{ro.entity.teachingMethod} ");
        }
        if (entity.getTeachingStartDate() != null) {
            conditionSql.append("AND cs.teaching_date >= #{ro.entity.teachingStartDate} ");
        }
        if (entity.getTeachingEndDate() != null) {
            conditionSql.append("AND cs.teaching_date <= #{ro.entity.teachingEndDate} ");
        }

        return conditionSql.toString();
    }

    public String countCourseSchedulesByConditions(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        PageRO<CourseScheduleRO> ro = (PageRO<CourseScheduleRO>) params.get("ro");
        String collegeName = (String) params.get("collegeName");

        sql.append("SELECT COUNT(*) FROM course_schedule cs ");
        sql.append("JOIN class_information ci ON cs.grade = ci.grade ");
        sql.append("AND cs.level = ci.level ");
        sql.append("AND cs.study_form = ci.study_form ");
        sql.append("AND cs.major_name = ci.major_name ");
        sql.append("AND cs.admin_class = ci.class_name WHERE 1=1 ");
        sql.append(commonConditionSql(ro));
        if (collegeName != null && !collegeName.isEmpty()) {
            sql.append("AND ci.college = #{collegeName} ");
        }

        return sql.toString();
    }

    public String getCourseSchedulesByConditions(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        PageRO<CourseScheduleRO> ro = (PageRO<CourseScheduleRO>) params.get("ro");
        String collegeName = (String) params.get("collegeName");

        sql.append("SELECT cs.* FROM course_schedule cs ");
        sql.append("JOIN class_information ci ON cs.grade = ci.grade ");
        sql.append("AND cs.level = ci.level ");
        sql.append("AND cs.study_form = ci.study_form ");
        sql.append("AND cs.major_name = ci.major_name ");
        sql.append("AND cs.admin_class = ci.class_name WHERE 1=1 ");
        sql.append(commonConditionSql(ro));
        if (collegeName != null && !collegeName.isEmpty()) {
            sql.append("AND ci.college = #{collegeName} ");
        }
        sql.append("ORDER BY cs.teaching_date ASC, cs.teaching_time ASC ");
        sql.append("LIMIT ").append((ro.getPageNumber() - 1) * ro.getPageSize()).append(", ").append(ro.getPageSize());

        return sql.toString();
    }

    private String commonSqlForTeacher(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        PageRO<CourseScheduleRO> ro = (PageRO<CourseScheduleRO>) params.get("ro");
        String teacher_username = (String) params.get("teacher_username");

        sql.append("FROM course_schedule cs ");
        sql.append("WHERE cs.teacher_username = #{teacher_username} ");
        sql.append(commonConditionSql(ro));

        return sql.toString();
    }

    public String getCourseSchedulesByTeacherUserName(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT cs.* ");
        sql.append(commonSqlForTeacher(params));
        sql.append("ORDER BY cs.teaching_date ASC, cs.teaching_time ASC ");
        PageRO<CourseScheduleRO> ro = (PageRO<CourseScheduleRO>) params.get("ro");
        sql.append("LIMIT ").append((ro.getPageNumber() - 1) * ro.getPageSize()).append(", ").append(ro.getPageSize());

        return sql.toString();
    }

    public String countCourseSchedulesByTeacherUserName(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ");
        sql.append(commonSqlForTeacher(params));

        return sql.toString();
    }

    public String countCourseSchedulesByStudentIdNumber(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        PageRO<CourseScheduleRO> ro = (PageRO<CourseScheduleRO>) params.get("ro");
        ClassInformationPO classInfo = (ClassInformationPO) params.get("class_information");

        sql.append("SELECT COUNT(*) FROM course_schedule cs ");
        sql.append("WHERE cs.grade = #{class_information.grade} ");
        sql.append("AND cs.level = #{class_information.level} ");
        sql.append("AND cs.study_form = #{class_information.studyForm} ");
        sql.append("AND cs.major_name = #{class_information.majorName} ");
        sql.append("AND cs.admin_class = #{class_information.className} ");
        sql.append(commonConditionSql(ro));

        return sql.toString();
    }

    public String getCourseSchedulesByStudentIdNumber(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        PageRO<CourseScheduleRO> ro = (PageRO<CourseScheduleRO>) params.get("ro");
        ClassInformationPO classInfo = (ClassInformationPO) params.get("class_information");

        sql.append("SELECT cs.* FROM course_schedule cs ");
        sql.append("WHERE cs.grade = #{class_information.grade} ");
        sql.append("AND cs.level = #{class_information.level} ");
        sql.append("AND cs.study_form = #{class_information.studyForm} ");
        sql.append("AND cs.major_name = #{class_information.majorName} ");
        sql.append("AND cs.admin_class = #{class_information.className} ");
        sql.append(commonConditionSql(ro));
        sql.append("ORDER BY cs.teaching_date ASC, cs.teaching_time ASC ");
        sql.append("LIMIT ").append((ro.getPageNumber() - 1) * ro.getPageSize()).append(", ").append(ro.getPageSize());

        return sql.toString();
    }

}

