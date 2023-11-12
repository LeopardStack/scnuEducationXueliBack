package com.scnujxjy.backendpoint.model.ro.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ClassInformationFilterRO {
    /**
     * 班级表主键
     */
    private Long id;

    /**
     * 年级
     */
    private String grade;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学院
     */
    private String college;

    /**
     * 层次
     */
    private String level;

    /**
     * 学制
     */
    private String studyPeriod;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 班级名称
     */
    private List<String>  classNames;
}
