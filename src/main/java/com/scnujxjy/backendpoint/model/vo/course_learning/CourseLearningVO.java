package com.scnujxjy.backendpoint.model.vo.course_learning;

import com.scnujxjy.backendpoint.model.bo.course_learning.TeacherInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    private String grades;

    /**
     * 年份
     */
    private String year;

    /**
     * 课程所在 学院
     */
    private String colleges;

    /**
     * 课程所在 专业名称
     */
    private String majorNames;

    /**
     * 班级名称
     */
    private String classNames;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 层次
     */
    private String levels;

    /**
     * 学习形式
     */
    private String studyForms;

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
     * 助教信息
     */
    private List<TeacherInfoVO> assistants;

    /**
     * 课程标识符 用来在未来建立好 全局统一的课程标识符后
     * 直接通过课程标识符来匹配 某个年级教学计划中这门课的所有班
     */
    private String courseIdentifier;


    /**
     * 课程是否有效 Y/N
     */
    private String valid;

    /**
     * 教学点名称
     */
    private String teachingPointName;

    /**
     * 近期排课 如果为空 说明未设置 但是直播课都会设置
     */
    private Date recentCourseScheduleTime;

    /**
     * 下次排课 如果为空 说明未设置 但是直播课都会设置
     */
    private Date nextCourseScheduleTime;

    /**
     * 频道 ID 如果是直播课的话
     */
    private String channelId;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseLearningVO that = (CourseLearningVO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
