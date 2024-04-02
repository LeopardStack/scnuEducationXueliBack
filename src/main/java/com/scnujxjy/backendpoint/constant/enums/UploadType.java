package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum UploadType {
    COURSE_SCHEDULE_LIST("学历教育排课表"),
    COURSE_TEACHER_LIST("学历教育师资表")
    ;
    private String upload_type;

    UploadType(String upload_type) {
        this.upload_type = upload_type;
    }
}
