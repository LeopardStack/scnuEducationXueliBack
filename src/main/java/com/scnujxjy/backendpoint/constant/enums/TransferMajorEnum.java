package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransferMajorEnum {

    NEW_SCHOOL(1, "新生校内"),
    NEW_PROVINCE(2, "新生省内"),
    NEW_COUNTRY(3, "新生国内"),
    OLD_SCHOOL(4, "老生校内");

    private Integer type;

    private String value;

}
