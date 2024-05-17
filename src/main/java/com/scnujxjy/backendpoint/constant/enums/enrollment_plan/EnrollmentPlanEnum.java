package com.scnujxjy.backendpoint.constant.enums.enrollment_plan;

import lombok.Getter;

@Getter
public enum EnrollmentPlanEnum {
    ENROLLMENT_SETUP_KEY("招生计划申报");


    private String key;

    EnrollmentPlanEnum(String key) {
        this.key = key;
    }
}
