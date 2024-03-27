package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum StudentCoursesStatusEnum {
    COMPLETED_COURSE("已修"),
    ENROLLED_COURSE("在修");
    private final String courseStatus;

    StudentCoursesStatusEnum(String courseStatus) {
        this.courseStatus = courseStatus;
    }

}
