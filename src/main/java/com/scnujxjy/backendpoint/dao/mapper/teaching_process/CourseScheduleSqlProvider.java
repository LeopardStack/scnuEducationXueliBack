package com.scnujxjy.backendpoint.dao.mapper.teaching_process;

import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;

import java.util.Map;

public class CourseScheduleSqlProvider {

    public String countCourseSchedulesByConditions(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        PageRO<CourseScheduleRO> ro = (PageRO<CourseScheduleRO>) params.get("ro");
        CourseScheduleRO entity = ro.getEntity();
        String collegeName = (String) params.get("collegeName");

        sql.append("SELECT COUNT(*) FROM course_schedule cs ");
        sql.append("JOIN class_information ci ON cs.grade = ci.grade ");
        sql.append("AND cs.level = ci.level ");
        sql.append("AND cs.study_form = ci.study_form ");
        sql.append("AND cs.major_name = ci.major_name ");
        sql.append("AND cs.admin_class = ci.class_name WHERE 1=1 ");

        if (entity.getGrade() != null && !entity.getGrade().isEmpty()) {
            sql.append("AND cs.grade = #{ro.entity.grade} ");
        }
        if (entity.getLevel() != null && !entity.getLevel().isEmpty()) {
            sql.append("AND cs.level = #{ro.entity.level} ");
        }
        if (entity.getMajorName() != null && !entity.getMajorName().isEmpty()) {
            sql.append("AND cs.major_name = #{ro.entity.majorName} ");
        }
        if (entity.getTeachingClass() != null && !entity.getTeachingClass().isEmpty()) {
            sql.append("AND cs.teaching_class = #{ro.entity.teachingClass} ");
        }
        if (entity.getAdminClass() != null && !entity.getAdminClass().isEmpty()) {
            sql.append("AND cs.admin_class = #{ro.entity.adminClass} ");
        }
        if (entity.getExamType() != null && !entity.getExamType().isEmpty()) {
            sql.append("AND cs.exam_type = #{ro.entity.examType} ");
        }
        if (entity.getStudyForm() != null && !entity.getStudyForm().isEmpty()) {
            sql.append("AND cs.study_form = #{ro.entity.studyForm} ");
        }
        if (entity.getCourseName() != null && !entity.getCourseName().isEmpty()) {
            sql.append("AND cs.course_name = #{ro.entity.courseName} ");
        }
        if (entity.getMainTeacherName() != null && !entity.getMainTeacherName().isEmpty()) {
            sql.append("AND cs.main_teacher_name = #{ro.entity.mainTeacherName} ");
        }
        if (entity.getTeachingMethod() != null && !entity.getTeachingMethod().isEmpty()) {
            sql.append("AND cs.teaching_method = #{ro.entity.teachingMethod} ");
        }
        if (entity.getTeachingStartDate() != null) {
            sql.append("AND cs.teaching_date >= #{ro.entity.teachingStartDate} ");
        }
        if (entity.getTeachingEndDate() != null) {
            sql.append("AND cs.teaching_date <= #{ro.entity.teachingEndDate} ");
        }
        if (collegeName != null && !collegeName.isEmpty()) {
            sql.append("AND ci.college = #{collegeName} ");
        }

        return sql.toString();
    }

    public String getCourseSchedulesByConditions(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder();
        PageRO<CourseScheduleRO> ro = (PageRO<CourseScheduleRO>) params.get("ro");
        CourseScheduleRO entity = ro.getEntity();
        String collegeName = (String) params.get("collegeName");

        sql.append("SELECT cs.* FROM course_schedule cs ");
        sql.append("JOIN class_information ci ON cs.grade = ci.grade ");
        sql.append("AND cs.level = ci.level ");
        sql.append("AND cs.study_form = ci.study_form ");
        sql.append("AND cs.major_name = ci.major_name ");
        sql.append("AND cs.admin_class = ci.class_name WHERE 1=1 ");

        if (entity.getGrade() != null && !entity.getGrade().isEmpty()) {
            sql.append("AND cs.grade = #{ro.entity.grade} ");
        }
        if (entity.getLevel() != null && !entity.getLevel().isEmpty()) {
            sql.append("AND cs.level = #{ro.entity.level} ");
        }
        if (entity.getMajorName() != null && !entity.getMajorName().isEmpty()) {
            sql.append("AND cs.major_name = #{ro.entity.majorName} ");
        }
        if (entity.getTeachingClass() != null && !entity.getTeachingClass().isEmpty()) {
            sql.append("AND cs.teaching_class = #{ro.entity.teachingClass} ");
        }
        if (entity.getAdminClass() != null && !entity.getAdminClass().isEmpty()) {
            sql.append("AND cs.admin_class = #{ro.entity.adminClass} ");
        }
        if (entity.getExamType() != null && !entity.getExamType().isEmpty()) {
            sql.append("AND cs.exam_type = #{ro.entity.examType} ");
        }
        if (entity.getStudyForm() != null && !entity.getStudyForm().isEmpty()) {
            sql.append("AND cs.study_form = #{ro.entity.studyForm} ");
        }
        if (entity.getCourseName() != null && !entity.getCourseName().isEmpty()) {
            sql.append("AND cs.course_name = #{ro.entity.courseName} ");
        }
        if (entity.getMainTeacherName() != null && !entity.getMainTeacherName().isEmpty()) {
            sql.append("AND cs.main_teacher_name = #{ro.entity.mainTeacherName} ");
        }
        if (entity.getTeachingMethod() != null && !entity.getTeachingMethod().isEmpty()) {
            sql.append("AND cs.teaching_method = #{ro.entity.teachingMethod} ");
        }
        if (entity.getTeachingStartDate() != null) {
            sql.append("AND cs.teaching_date >= #{ro.entity.teachingStartDate} ");
        }
        if (entity.getTeachingEndDate() != null) {
            sql.append("AND cs.teaching_date <= #{ro.entity.teachingEndDate} ");
        }
        if (collegeName != null && !collegeName.isEmpty()) {
            sql.append("AND ci.college = #{collegeName} ");
        }
        sql.append("ORDER BY cs.id ASC ");
        sql.append("LIMIT ").append((ro.getPageNumber() - 1) * ro.getPageSize()).append(", ").append(ro.getPageSize());

        return sql.toString();
    }
}
