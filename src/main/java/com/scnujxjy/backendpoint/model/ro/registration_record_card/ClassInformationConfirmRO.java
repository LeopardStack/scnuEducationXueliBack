package com.scnujxjy.backendpoint.model.ro.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassInformationConfirmRO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 层次
     */
    private String level;

    /**
     * 学院
     */
    private String college;

    /**
     * 招生代码
     */
    private String majorCode;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 教学点
     */
    private String teachingPointName;

    /**
     * 教学点简称
     */
    private String teachingPointNameAlias;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 实录人数
     */
    private String admissionNumber;

    /**
     * 开班
     */
    private String classCreate;

    /**
     * 不开班
     */
    private String classNotCreate;

    /**
     * 备注
     */
    private String remark;


}
