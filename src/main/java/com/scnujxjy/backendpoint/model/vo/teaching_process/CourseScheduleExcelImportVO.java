package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseScheduleExcelImportVO {
    /**
     * 自增主键
     */
    @ExcelProperty(index = 0, value = "自增主键")
    private Long id;

    /**
     * 年级
     */
    @ExcelProperty(index = 1, value = "年级")
    private String grade;

    /**
     * 专业名称
     */
    @ExcelProperty(index = 2, value = "专业名称")
    private String majorName;

    /**
     * 层次
     */
    @ExcelProperty(index = 3, value = "层次")
    private String level;

    /**
     * 学习形式
     */
    @ExcelProperty(index = 4, value = "学习形式")
    private String studyForm;

    /**
     * 行政班别
     */
    @ExcelProperty(index = 5, value = "行政班别")
    private String adminClass;

    /**
     * 教学班别
     */
    @ExcelProperty(index = 6, value = "教学班别")
    private String teachingClass;

    /**
     * 学生人数
     */
    @ExcelProperty(index = 7, value = "学生人数")
    private Integer studentCount;

    /**
     * 课程名称
     */
    @ExcelProperty(index = 8, value = "课程名称")
    private String courseName;

    /**
     * 学时数
     */
    @ExcelProperty(index = 9, value = "学时数")
    private Integer classHours;

    /**
     * 考核类型
     */
    @ExcelProperty(index = 10, value = "考核类型")
    private String examType;

    /**
     * 主讲教师
     */
    @ExcelProperty(index = 11, value = "主讲教师")
    private String mainTeacherName;

    /**
     * 主讲教师工号/学号
     */
    @ExcelProperty(index = 12, value = "主讲教师工号/学号")
    private String mainTeacherId;

    /**
     * 主讲教师身份证号码
     */
    @ExcelProperty(index = 13, value = "主讲教师身份证号码")
    private String mainTeacherIdentity;

    /**
     * 辅导教师
     */
    @ExcelProperty(index = 14, value = "辅导教师")
    private String tutorName;

    /**
     * 辅导教师工号
     */
    @ExcelProperty(index = 15, value = "辅导教师工号")
    private String tutorId;

    /**
     * 辅导教师身份证号码
     */
    @ExcelProperty(index = 16, value = "辅导教师身份证号码")
    private String tutorIdentity;

    /**
     * 授课方式（直播、点播、线下）
     */
    @ExcelProperty(index = 17, value = "授课方式")
    private String teachingMethod;

    /**
     * 上课地点
     */
    @ExcelProperty(index = 18, value = "上课地点")
    private String classLocation;

    /**
     * 在线教学平台及网络课程资源拥有情况
     */
    @ExcelProperty(index = 19, value = "在线教学平台及资源")
    private String onlinePlatform;

    /**
     * 授课日期
     */
    @ExcelProperty(index = 20, value = "授课日期")
    private Date teachingDate;

    /**
     * 授课时间
     */
    @ExcelProperty(index = 21, value = "授课时间")
    private String teachingTime;

    /**
     * 是否覆盖
     */
    @ExcelProperty(index = 22, value = "是否覆盖")
    private String cover;

    /**
     * 教师用户名
     */
    @ExcelIgnore
    private String teacherUsername;

    /**
     * 助教用户名
     */
    @ExcelIgnore
    private String teachingAssistantUsername;
}


