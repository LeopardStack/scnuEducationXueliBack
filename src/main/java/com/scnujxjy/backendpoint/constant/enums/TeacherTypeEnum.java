package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum TeacherTypeEnum {
    MAIN_TEACHER("主讲教师"),
    ASSISTANT_TEACHER("辅导教师");
    private final String type;

    TeacherTypeEnum(String type) {
        this.type = type;
    }

}
