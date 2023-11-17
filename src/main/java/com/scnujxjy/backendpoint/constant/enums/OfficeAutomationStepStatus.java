package com.scnujxjy.backendpoint.constant.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@Getter
public enum OfficeAutomationStepStatus {
    WAITING("waiting", "审批中", false),
    SUCCESS("success", "成功", true),
    FAILED("failed", "失败", false),
    TRANSFER("transfer", "流转", false);

    String status;
    String name;
    Boolean isSuccess;

    OfficeAutomationStepStatus(String status, String name, Boolean isSuccess) {
        this.status = status;
        this.name = name;
        this.isSuccess = isSuccess;
    }

    /**
     * 根据状态名称匹配状态
     *
     * @param name 状态名称
     * @return
     */
    public static OfficeAutomationStepStatus match(String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        for (OfficeAutomationStepStatus value : OfficeAutomationStepStatus.values()) {
            if (name.equals(value.getName())) {
                return value;
            }
        }
        return null;
    }
}
