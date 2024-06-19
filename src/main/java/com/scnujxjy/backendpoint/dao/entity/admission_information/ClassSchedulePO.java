package com.scnujxjy.backendpoint.dao.entity.admission_information;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 开班计划表
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-05-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("class_schedule")
public class ClassSchedulePO implements Serializable {

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
     * 入学时间
     */
    private Date enrollmentDate;

    /**
     * 毕业时间
     */
    private Date graduationDate;

    /**
     * 所属学院
     */
    private String college;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 专业录取代码
     */
    private String admissionCode;

    /**
     * 层次
     */
    private String level;

    /**
     * 学制
     */
    private Integer studyDuration;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 学费标准
     */
    private BigDecimal tuitionFee;

    /**
     * 学号前缀
     */
    private String studentIdPrefix;

    /**
     * 班级标识
     */
    private String classIdentifier;

    /**
     * 班级状态
     */
    private String classStatus;

    /**
     * 班号
     */
    private String classIndex;

    /**
     * 录取人数
     */
    private Integer admissionCount;

    /**
     * 是否开班
     */
    private Boolean isOpenClass;

    /**
     * 备注
     */
    private String remark;

    /**
     * 审核状态，标识谁能操作这条记录
     */
    private String auditStatus;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
