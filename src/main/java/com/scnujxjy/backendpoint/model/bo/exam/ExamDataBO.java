package com.scnujxjy.backendpoint.model.bo.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExamDataBO {
    private Long id;

    private Long examId;

    private String grade;

    private String majorName;

    private String level;

    private String studyForm;

    private String classIdentifier;

    private String courseName;

    private Integer studyHours;

    private String assessmentType;

    private String teachingMethod;

    private String courseType;

    private String credit;

    private String teachingSemester;

    private String courseCode;

    private String courseCover;

    private String className;

    private String mainTeacher;

    private String teacherUsername;

    private String examMethod;

    private String examStatus;

    private String examType;

}
