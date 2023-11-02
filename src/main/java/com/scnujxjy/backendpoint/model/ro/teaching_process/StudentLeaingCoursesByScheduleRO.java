package com.scnujxjy.backendpoint.model.ro.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class StudentLeaingCoursesByScheduleRO {
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
     * 学习形式
     */
    private String studyForm;


    /**
     * 层次
     */
    private String level;

    /**
     * 行政班别
     */
    private String adminClass;


    /**
     * 教学班别
     */
    private String teachingClass;

    /**
     * 学时
     */
    private String classHours;

    /**
     * 考察类型
     */
    private String examType;

    /**
     * 主讲老师学号/工号
     */
    private String mainTeacherId;

    /**
     * 主讲老师身份证号码
     */
    private String mainTeacherIdentity;

    /**
     * 主讲老师姓名
     */
    private String mainTeacherName;

    /**
     * 辅导教师工号/学号
     */
    private String tutorId;

    /**
     * 辅导教师身份证号码
     */
    private String tutorIdentity;

    /**
     * 辅导教师姓名
     */
    private String tutorName;

    /**
     * 辅导教师姓名
     */
    private String courseName;

    /**
     * 辅导教师姓名
     */
    private String teacherUsername;

    /**
     * 课程封面图片 URL
     */
    private String courseCover;


}
