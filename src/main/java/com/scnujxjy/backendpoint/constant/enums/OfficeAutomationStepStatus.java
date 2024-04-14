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
     * 匹配状态
     *
     * @param status 状态名称
     * @return
     */
    public static OfficeAutomationStepStatus match(String status) {
        if (StrUtil.isBlank(status)) {
            return null;
        }
        for (OfficeAutomationStepStatus value : OfficeAutomationStepStatus.values()) {
            if (status.equals(value.getStatus())) {
                return value;
            }
        }
        return null;
    }
}
