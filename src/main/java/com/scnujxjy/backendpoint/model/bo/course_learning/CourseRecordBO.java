package com.scnujxjy.backendpoint.model.bo.course_learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


/**
 * 存储 课程连表结果
 *
 * courses_learning cl
 * LEFT JOIN
 * sections ss ON cl.id = ss.course_id
 * LEFT JOIN
 * courses_class_mapping ccm ON cl.id = ccm.course_id
 * LEFT JOIN
 * class_information ci ON ccm.class_identifier = ci.class_identifier
 * LEFT JOIN
 * teacher_information ti ON cl.default_main_teacher_username = ti.teacher_username
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseRecordBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String grade;
    private String courseName;
    private String courseType;
    private String courseDescription;
    private String courseCoverUrl;
    private String defaultMainTeacherUsername;
    private String courseIdentifier;
    private String valid;
    private Date createdTime;
    private Date updatedTime;
    private Integer parentSectionId;
    private String sectionName;
    private Integer sequence;
    private String contentType;
    private String mainTeacherUsername;
    private Date deadline;
    private Date startTime;
    private String sectionValid;
    private String classIdentifier;
    private String classGrade;
    private String className;
    private String studyForm;
    private String level;
    private String studyPeriod;
    private String college;
    private String majorName;
    private String majorCode;
    private Double tuition;
    private String name; // teacherName
}
