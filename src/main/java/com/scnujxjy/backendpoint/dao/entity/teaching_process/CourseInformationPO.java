package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 课程信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course_information")
public class CourseInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年级
     */
    private String grade;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 层次
     */
    private String level;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 行政班别
     */
    private String adminClass;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 学时数
     */
    private Integer studyHours;

    /**
     * 考核类型
     */
    private String assessmentType;

    /**
     * 授课方式
     */
    private String teachingMethod;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 学分
     */
    private Integer credit;

    /**
     * 授课学期
     */
    private String teachingSemester;


}
