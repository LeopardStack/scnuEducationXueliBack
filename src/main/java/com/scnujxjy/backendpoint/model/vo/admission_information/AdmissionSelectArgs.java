package com.scnujxjy.backendpoint.model.vo.admission_information;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AdmissionSelectArgs implements Serializable {

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
     * 教学点
     */
    private List<String> teachingPoints;

}
