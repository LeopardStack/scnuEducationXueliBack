package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 学籍筛选参数列表
 */
@Data
public class StudentStatusSelectArgs implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 年级
     */
    private List<String> grades;

    /**
     * 学院名称
     */
    private List<String> collegeNames;

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
     * 行政班别
     */
    private List<String> classNames;

    /**
     * 学制
     */
    private List<String> studyDurations;

    /**
     * 学籍状态
     */
    private List<String> academicStatus;
}
