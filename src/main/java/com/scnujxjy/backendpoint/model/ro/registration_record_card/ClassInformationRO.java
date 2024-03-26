package com.scnujxjy.backendpoint.model.ro.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ClassInformationRO {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 班级标识
     */
    private String classIdentifier;

    /**
     * 班级标识
     */
    private Set<String> classIdentifiers;

    /**
     * 年级
     */
    private String grade;

    /**
     * 班级学号前缀
     */
    private String classStudentPrefix;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 层次
     */
    private String level;

    /**
     * 学制
     */
    private String studyPeriod;

    /**
     * 学院
     */
    private String college;

    /**
     * 女性人数
     */
    private Integer femaleCount;

    /**
     * 总人数
     */
    private Integer totalCount;

    /**
     * 毕业总人数
     */
    private Integer graduateTotalCount;

    /**
     * 毕业女性人数
     */
    private Integer graduateFemaleCount;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 入学开始日期
     */
    private Date admissionStartDate;

    /**
     * 入学日期
     */
    private Date admissionDate;

    /**
     * 入学结束日期
     */
    private Date admissionEndDate;

    /**
     * 毕业开始日期
     */
    private Date graduationStartDate;

    /**
     * 毕业日期
     */
    private Date graduationDate;

    /**
     * 毕业结束日期
     */
    private Date graduationEndDate;

    /**
     * 学籍状态
     */
    private String studentStatus;

    /**
     * 录取专业代码
     */
    private String majorCode;

    /**
     * 学费
     */
    private BigDecimal tuition;

    /**
     * 是否为师范生，1是，0否
     */
    private Boolean isTeacherStudent;

    /**
     * course_schedule中的批次 id
     */
    private Long batchIndex;

    private List<String> classNames;
}
