package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author hp
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TeacherCoursesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private  Long id;

    private String grade;

    private String majorName;

    private String level;

    private String studyForm;

    /**
     * 行政班别  即 class_information 里面的 class_name
     */
    private String adminClass;

    private String teachingClass;


    private String courseName;

    private String classIdentifier;

    private String courseCover;
}
