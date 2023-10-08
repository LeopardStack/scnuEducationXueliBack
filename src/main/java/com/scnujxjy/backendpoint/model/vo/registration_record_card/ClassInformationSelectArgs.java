package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 班级筛选参数
 */
@Data
public class ClassInformationSelectArgs implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 学院名称
     */
    private List<String> collegeNames;

    /**
     * 班级名称
     */
    private List<String> classNames;

    /**
     * 教学点名称
     */
    private List<String> teachingPoints;

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
     * 年级
     */
    private List<String> grades;

    /**
     * 学制
     */
    private List<String> studyDurations;

    /**
     * 学籍状态
     */
    private List<String> academicStatus;
}
