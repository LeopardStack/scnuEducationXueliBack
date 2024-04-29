package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemMessageStatus {
    SUCCESS("SUCCESS"),
    FAILED("FAILED");
    private String name;
}
