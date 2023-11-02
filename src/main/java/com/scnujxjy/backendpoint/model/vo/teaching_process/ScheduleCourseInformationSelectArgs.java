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

    /**
     * 年级的筛选项
     */
    List<String> grades;

    /**
     * 学院的筛选项
     */
    List<String> collegeNames;

    /**
     * 专业名称的筛选项
     */
    List<String> majorNames;

    /**
     * 行政班别的筛选项
     */
    List<String> adminClassNames;

    /**
     * 教学班级的筛选
     */
    List<String> teachingClasses;

    /**
     * 专业名称的筛选
     */
    List<String> courseNames;

    /**
     * 学期的筛选
     */
    List<String> semesters;

    /**
     * 层次
     */
    private List<String> levels;

    /**
     * 学习形式
     */
    private List<String> studyForms;
}
