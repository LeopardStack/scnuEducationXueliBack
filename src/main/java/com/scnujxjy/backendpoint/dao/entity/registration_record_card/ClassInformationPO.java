package com.scnujxjy.backendpoint.dao.entity.registration_record_card;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 班级信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("class_information")
public class ClassInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 班级标识
     */
    private String classIdentifier;

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
     * 入学日期
     */
    private Date admissionDate;

    /**
     * 毕业日期
     */
    private Date graduationDate;

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


}
