package com.scnujxjy.backendpoint.model.vo.video_stream;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AttendanceVO {
    //学号、姓名、行政班别、是否出勤、观看时长
    private static final String describe = "考勤数据";
    @ExcelProperty(value = {describe,"年级"})
    private String grade;
    @ExcelProperty(value = {describe,"专业"})
    private String majorName;
    @ExcelProperty(value = {describe,"层次"})
    private String level;
    @ExcelProperty(value = {describe,"学习形式"})
    private String studyForm;
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

    @ExcelProperty(value = {describe,"观看时长,单位秒"})
    private String playDuration;

}
