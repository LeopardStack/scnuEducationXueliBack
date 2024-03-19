package com.scnujxjy.backendpoint.model.vo.course_learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseClassInfoVO {
    private String grade;

    private String className;

    private String college;


    private String level;


    private String studyForm;

    private String majorName;

    private String classIdentifier;
}
