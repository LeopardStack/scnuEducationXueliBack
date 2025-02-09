package com.scnujxjy.backendpoint.model.ro.teaching_process;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseScheduleRO {
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
     * 教学班别
     */
    private String teachingClass;

    /**
     * 学生人数
     */
    private Integer studentCount;

    /**
     * 学期
     */
    private String semester;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 学时数
     */
    private Integer classHours;

    /**
     * 考核类型
     */
    private String examType;

    /**
     * 主讲教师
     */
    private String mainTeacherName;

    /**
     * 主讲教师工号/学号
     */
    private String mainTeacherId;

    /**
     * 主讲教师身份证号码
     */
    private String mainTeacherIdentity;

    /**
     * 辅导教师
     */
    private String tutorName;

    /**
     * 辅导教师工号
     */
    private String tutorId;

    /**
     * 辅导教师身份证号码
     */
    private String tutorIdentity;

    /**
     * 授课方式（直播、点播、线下）
     */
    private String teachingMethod;

    /**
     * 上课地点
     */
    private String classLocation;

    /**
     * 在线教学平台及网络课程资源拥有情况
     */
    private String onlinePlatform;

    /**
     * 授课日期
     */
    private Date teachingDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone="Asia/Shanghai")
    private Date teachingStartDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone="Asia/Shanghai")
    private Date teachingEndDate;



    /**
     * 授课时间
     */
    private String teachingTime;

    /**
     * 主讲教师平台用户名
     */
    private String teacherUsername;

    /**
     * 辅导教师平台用户名
     */
    private String teachingAssistantUsername;

    /**
     * 批次 id
     */
    private Long batchIndex;
}
