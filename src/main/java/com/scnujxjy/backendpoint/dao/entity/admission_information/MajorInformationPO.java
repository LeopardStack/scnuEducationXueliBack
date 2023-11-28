package com.scnujxjy.backendpoint.dao.entity.admission_information;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 专业信息表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("major_information")
public class MajorInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年级
     */
    private String grade;

    /**
     * 层次
     */
    private String level;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 学费
     */
    private BigDecimal tuition;

    /**
     * 录取专业代码
     */
    private String admissionMajorCode;

    /**
     * 招生科类
     */
    private String admissionType;

    /**
     * 学信网专业代码
     */
    private String xuexinwangMajorCode;

    /**
     * 学信网专业名称
     */
    private String xuexinwangMajorName;

    /**
     * 学院id
     */
    private String collegeId;

    /**
     * 教学点id
     */
    private String teachingPointId;

    /**
     * 人才培养方案
     */
    private String personnelCultivatingProgramUrl;


}
