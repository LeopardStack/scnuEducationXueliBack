package com.scnujxjy.backendpoint.model.ro.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseInformationRO {


    /**
     * 自增主键
     */
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

    /**
     * 课程编号
     */
    private String courseCode;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学院名称
     */
    private String college;


}