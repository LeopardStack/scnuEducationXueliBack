package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MessageStatus {
    DRAFT(1, "草稿"),
    PUBLISH(2, "发布"),

    ROLL_BACK(3, "撤销"),

    DELETED(4, "删除");

    private Integer status;
    private String name;
}
