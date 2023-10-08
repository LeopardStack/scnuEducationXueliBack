package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.Data;

import java.util.List;

@Data
public class CourseInformationSelectArgs {
    /**
     * 年级
     */
    private List<String> grades;

    /**
     * 专业名称
     */
    private List<String> majorNames;

    /**
     * 层次
     */
    private List<String> levels;

    /**
     * 学习形式
     */
    private List<String> studyForms;

    /**
     * 班级名称
     */
    private List<String> classNames;

    /**
     * 课程名称
     */
    private List<String> courseNames;
}
