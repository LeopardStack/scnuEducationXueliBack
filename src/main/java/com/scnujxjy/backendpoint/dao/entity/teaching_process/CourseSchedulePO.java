package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 排课表
 * </p>
 *
 * @author leopard
 * @since 2023-08-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("course_schedule")
public class CourseSchedulePO implements Serializable {

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
     * 教学班别
     */
    private String teachingClass;

    /**
     * 学生人数
     */
    private Integer studentCount;

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

    /**
     * 授课时间
     */
    private String teachingTime;


}
