package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemMessageStatus {
    SUCCESS("SUCCESS", "成功"),
    WAITING("WAITING", "等待中"),
    FAILED("FAILED", "失败");
    private String name;

    private String description;
}
