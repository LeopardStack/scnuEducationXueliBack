package com.scnujxjy.backendpoint.exportAttendanceData;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AttendanceTestVO {
    //学号、姓名、行政班别、是否出勤、观看时长
    private static final String describe = "考勤数据";

    @ExcelProperty(value = {describe,"年级"})
    private String grade;

    @ExcelProperty(value = {describe,"学院"})
    private String college;

    @ExcelProperty(value = {describe,"专业名称"})
    private String majorName;

    @ExcelProperty(value = {describe,"学习形式"})
    private String studyForm;

    @ExcelProperty(value = {describe,"层次"})
    private String level;

    @ExcelProperty(value = {describe,"课程名称"})
    private String courseName;

    @ExcelProperty(value = {describe,"主讲老师"})
    private String mainTeacherName;

    @ExcelProperty(value = {describe, "上课日期"})
    @DateTimeFormat("yyyy/MM/dd")  // 指定日期格式
    private Date teachingDate;

    @ExcelProperty(value = {describe,"上课时间"})
    private String teachingTime;

    @ExcelProperty(value = {describe,"学号"})
    private String code;

    @ExcelProperty(value = {describe,"姓名"})
    private String name;

    @ExcelProperty(value = {describe,"行政班别"})
    private String className;

    @ExcelProperty(value = {describe,"是否出勤"})
    private String Attendance;

    @ExcelProperty(value = {describe,"观看时长"})
    private String playDuration;

    @ExcelIgnore
    private String totalSeconds;

    @ExcelProperty(value = {describe,"进入时间"})
    private String startTime;
}
