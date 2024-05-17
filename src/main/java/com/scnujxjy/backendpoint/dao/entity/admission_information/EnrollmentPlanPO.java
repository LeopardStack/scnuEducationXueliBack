package com.scnujxjy.backendpoint.dao.entity.admission_information;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 招生计划申报表
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("enrollment_plan")
public class EnrollmentPlanPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 招生专业名称
     */
    private String majorName;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 学制
     */
    private String educationLength;

    /**
     * 培养层次
     */
    private String trainingLevel;

    /**
     * 招生人数
     */
    private Integer enrollmentNumber;

    /**
     * 招生对象
     */
    private String targetStudents;

    /**
     * 招生区域
     */
    private String enrollmentRegion;

    /**
     * 具体办学地点
     */
    private String schoolLocation;

    /**
     * 联系电话
     */
    private String contactNumber;

    /**
     * 主管院系
     */
    private String college;

    /**
     * 授课地点
     */
    private String teachingLocation;

    /**
     * 招生科类
     */
    private String enrollmentSubject;

    /**
     * 学费
     */
    private BigDecimal tuition;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 教学点 ID
     */
    private String teachingPointId;

    /**
     * 学院 ID
     */
    private String collegeId;

    /**
     * 审核状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
