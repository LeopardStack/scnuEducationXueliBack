package com.scnujxjy.backendpoint.constant.enums.office_automation;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author lth
 * @version 1.0
 * @description TODO
 * @date 2024/4/26 15:40
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum SystemMessageType2Enum {

    SCHOOL_IN_TRANSFER_MAJOR("校内转专业"),
    SCHOOL_OUT_TRANSFER_MAJOR("校外转专业"),
    STUDENT_SUSPENSION_OF_STUDY("学生休学"),
    COMMON("默认审批流程");

    /**
     * 此处的typeName要与{@link OfficeAutomationHandlerType#getName()}相同
     */
    String typeName;

    public static SystemMessageType2Enum match(String typeName) {
        if (StrUtil.isBlank(typeName)) {
            return null;
        }
        for (SystemMessageType2Enum systemMessageType2Enum : SystemMessageType2Enum.values()) {
            if (systemMessageType2Enum.getTypeName().equals(typeName)) {
                return systemMessageType2Enum;
            }
        }
        return null;
    }

    public static SystemMessageType2Enum match(OfficeAutomationHandlerType type) {
        if (Objects.isNull(type)) {
            return COMMON;
        }
        for (SystemMessageType2Enum systemMessageType2Enum : SystemMessageType2Enum.values()) {
            if (systemMessageType2Enum.getTypeName().equals(type.getName())) {
                return systemMessageType2Enum;
            }
        }
        return COMMON;
    }
}
