package com.scnujxjy.backendpoint.model.vo.course_learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseLearningVO {
    /**
     * 课程的主键 ID
     */
    private Long id;


    /**
     * 课程所在 年级
     */
    private String grade;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型 直播、点播、线下、 混合
     */
    private String courseType;

    /**
     * 课程描述
     */
    private String courseDescription;

    /**
     * 课程封面 URL
     */
    private String courseCoverUrl;

    /**
     * 默认主讲老师
     */
    private String defaultMainTeacherUsername;

    /**
     * 主讲老师姓名
     */
    private String defaultMainTeacherName;

    /**
     * 课程标识符 用来在未来建立好 全局统一的课程标识符后
     * 直接通过课程标识符来匹配 某个年级教学计划中这门课的所有班
     */
    private String courseIdentifier;

    /**
     * 班级名称
     */
    private String classNames;

    /**
     * 课程是否有效 Y/N
     */
    private String valid;

    /**
     * 近期排课 如果为空 说明未设置 但是直播课都会设置
     */
    private Date recentCourseScheduleTime;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
