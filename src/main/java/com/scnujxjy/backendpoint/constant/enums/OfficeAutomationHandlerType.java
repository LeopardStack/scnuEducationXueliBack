package com.scnujxjy.backendpoint.constant.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum OfficeAutomationHandlerType {


    STUDENT_TRANSFER_MAJOR("student-transfer-major", "学生转专业");
    String type;

    String name;

    public static OfficeAutomationHandlerType match(String type) {
        if (StrUtil.isBlank(type)) {
            return null;
        }
        for (OfficeAutomationHandlerType value : OfficeAutomationHandlerType.values()) {
            if (type.equals(value.getType())) {
                return value;
            }
        }
        return null;
    }
}
