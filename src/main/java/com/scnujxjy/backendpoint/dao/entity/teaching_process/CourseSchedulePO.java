package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.alibaba.excel.annotation.ExcelProperty;
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
    @ExcelProperty(index = 1)
    private String grade;

    /**
     * 专业名称
     */
    @ExcelProperty(index = 2)
    private String majorName;

    /**
     * 层次
     */
    @ExcelProperty(index = 3)
    private String level;

    /**
     * 学习形式
     */
    @ExcelProperty(index = 4)
    private String studyForm;

    /**
     * 行政班别
     */
    @ExcelProperty(index = 5)
    private String adminClass;

    /**
     * 教学班别
     */
    @ExcelProperty(index = 6)
    private String teachingClass;

    /**
     * 学生人数
     */
    @ExcelProperty(index = 7)
    private Integer studentCount;

    /**
     * 课程名称
     */
    @ExcelProperty(index = 8)
    private String courseName;

    /**
     * 学时数
     */
    @ExcelProperty(index = 9)
    private Integer classHours;

    /**
     * 考核类型
     */
    @ExcelProperty(index = 10)
    private String examType;

    /**
     * 主讲教师
     */
    @ExcelProperty(index = 11)
    private String mainTeacherName;

    /**
     * 主讲教师工号/学号
     */
    @ExcelProperty(index = 12)
    private String mainTeacherId;

    /**
     * 主讲教师身份证号码
     */
    @ExcelProperty(index = 13)
    private String mainTeacherIdentity;

    /**
     * 辅导教师
     */
    @ExcelProperty(index = 14)
    private String tutorName;

    /**
     * 辅导教师工号
     */
    @ExcelProperty(index = 15)
    private String tutorId;

    /**
     * 辅导教师身份证号码
     */
    @ExcelProperty(index = 16)
    private String tutorIdentity;

    /**
     * 授课方式（直播、点播、线下）
     */
    @ExcelProperty(index = 17)
    private String teachingMethod;

    /**
     * 上课地点
     */
    @ExcelProperty(index = 18)
    private String classLocation;

    /**
     * 在线教学平台及网络课程资源拥有情况
     */
    @ExcelProperty(index = 19)
    private String onlinePlatform;

    /**
     * 授课日期
     */
    @ExcelProperty(index = 20)
    private Date teachingDate;

    /**
     * 授课时间
     */
    @ExcelProperty(index = 21)
    private String teachingTime;

    /**
     * 教师平台用户名
     */
    private String teacherUsername;


}
