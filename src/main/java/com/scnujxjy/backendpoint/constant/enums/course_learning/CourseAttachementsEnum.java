package com.scnujxjy.backendpoint.constant.enums.course_learning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CourseAttachementsEnum {
    COURSE_ATTACHEMENTS("course_attachment"),
    COURSE_POST_ASSIGNMENTS_ATTACHEMENTS("course_post_assignment"),
    COURSE_ASSIGNMENTS_ATTACHEMENTS("course_assignment");

    String attachmentPrefix;
}
