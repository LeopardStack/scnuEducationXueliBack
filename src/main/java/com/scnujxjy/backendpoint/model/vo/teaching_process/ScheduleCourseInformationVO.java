package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ScheduleCourseInformationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学院
     */
    private String college;

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
     * 教学班别
     */
    private String teachingClass;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 主讲教师姓名
     */
    private String mainTeacherName;

    /**
     * 主讲教师学号/工号
     */
    private String mainTeacherId;

    /**
     * 主讲教师身份证号码
     */
    private String mainTeacherIdentity;

    /**
     * 助教老师姓名
     */
    private String tutorName;

    /**
     * 助教老师学号/工号
     */
    private String tutorId;

    /**
     * 助教老师身份证号码
     */
    private String tutorIdentity;

    private String teachingMethod;

    private String teacherUsername;

    /**
     * 课程学习时长
     */
    private String classHours;

    /**
     * 课程类型
     */
    private String examType;
}
