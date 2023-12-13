package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum DownloadFileNameEnum {
    STUDENT_STATUS_EXPORT_FILE("学籍数据导出"),
    STUDENT_SCORE_INFORMATION_EXPORT_FILE("成绩数据导出"),
    CLASS_INFORMATION_EXPORT_FILE("班级数据导出"),
    EXAM_TEACHERS_EXPORT_FILE("考试信息导出"),
    EXAM_STUDENTS_EXPORT_FILE("机考名单信息导出"),
    STUDENT_FEES_EXPORT_FILE("缴费信息导出");


    private String filename;

    DownloadFileNameEnum(String filename) {
        this.filename = filename;
    }
}
