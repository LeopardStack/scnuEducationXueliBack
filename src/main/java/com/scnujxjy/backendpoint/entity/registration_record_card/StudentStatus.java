package com.scnujxjy.backendpoint.entity.registration_record_card;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 学籍信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StudentStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学院
     */
    private String college;

    /**
     * 教学点
     */
    private String teachingPoint;

    /**
     * 专业名称
     */
    private String majorName;

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
    private String studyDuration;

    /**
     * 考生号
     */
    private String admissionNumber;

    /**
     * 学籍状态
     */
    private String academicStatus;

    /**
     * 入学日期，一般为入学年份 + 03
     */
    private Date enrollmentDate;


}
