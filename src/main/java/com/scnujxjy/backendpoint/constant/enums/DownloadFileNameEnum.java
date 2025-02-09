package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum DownloadFileNameEnum {
    STUDENT_STATUS_EXPORT_FILE("学籍数据导出"),
    STUDENT_SCORE_INFORMATION_EXPORT_FILE("成绩数据导出"),
    CLASS_INFORMATION_EXPORT_FILE("班级数据导出"),
    EXAM_TEACHERS_EXPORT_FILE("考试信息导出"),
    EXAM_STUDENTS_EXPORT_FILE("机考名单信息导出"),
    ADMISSION_STUDENTS_EXPORT_FILE("新生录取信息导出"),
    ADMISSION_STUDENTS_PAY_EXPORT_FILE("新生缴费信息导出"),
    ADMISSION_STUDENTS_NOT_PAY_EXPORT_FILE("新生未缴费信息导出"),
    ATTENDANCE_STUDENTS_EXPORT_FILE("考勤表信息导出"),
    STUDENT_FEES_EXPORT_FILE("缴费信息导出");


    private String filename;

    DownloadFileNameEnum(String filename) {
        this.filename = filename;
    }
}
