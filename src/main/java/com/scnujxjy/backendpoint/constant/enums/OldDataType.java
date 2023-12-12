package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

/**
 * @author 谢辉龙
 */
@Getter
public enum OldDataType {
    STUDENT_STATUS("学籍数据同步"),
    STUDENT_FEES("缴费数据同步"),
    CLASS_INFO("班级数据同步"),
    TEACHING_PLANS("教学计划同步"),
    STUDENT_STATUS_CHANGE("学籍异动同步"),
    GRADE_INFO("成绩数据同步")
    ;
    private String old_data_type;

    OldDataType(String old_data_type) {
        this.old_data_type = old_data_type;
    }
}
