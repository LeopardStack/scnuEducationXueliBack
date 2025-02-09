package com.scnujxjy.backendpoint.model.vo.video_stream;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AttendanceVO {
    //学号、姓名、行政班别、是否出勤、观看时长
    private static final String describe = "考勤数据";
    @ExcelProperty(value = {describe,"层次"})
    private String section;
    @ExcelProperty(value = {describe,"学号"})
    private String code;

    @ExcelProperty(value = {describe,"姓名"})
    private String name;

    @ExcelProperty(value = {describe,"课程名称"})
    private String courseName;

    @ExcelProperty(value = {describe,"主讲老师"})
    private String teacherName;

    @ExcelProperty(value = {describe,"行政班别"})
    private String className;

    @ExcelProperty(value = {describe,"是否出勤"})
    private String Attendance;

//    @ExcelProperty(value = {describe,"直播观看时长,单位秒"})
//    private String playDuration;

//    @ExcelProperty(value = {describe,"回放观看时长,单位秒"})
//    private String vodDuration;

    @ExcelProperty(value = {describe,"是否有观看回放"})
    private String isVodDuration;

}
