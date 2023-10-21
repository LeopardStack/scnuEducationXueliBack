package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 排课表课程筛选参数
 */
@Data
public class ScheduleCourseInformationSelectArgs implements Serializable {
    private static final long serialVersionUID = 1L;

    List<String> grades;

    List<String> collegeNames;

    List<String> majorNames;

    List<String> adminClassNames;

    List<String> teachingClasses;
    List<String> courseNames;

    /**
     * 层次
     */
    private List<String> levels;

    /**
     * 学习形式
     */
    private List<String> studyForms;
}
