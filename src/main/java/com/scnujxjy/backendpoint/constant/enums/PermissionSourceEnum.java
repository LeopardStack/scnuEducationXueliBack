package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermissionSourceEnum {
    APPROVAL_WATCH(1L, "学籍异动.转专业.查看"),
    APPROVAL_APPROVAL(2L, "学籍异动.转专业.审批"),
    APPROVAL_SUSPENSION_WATCH(3L, "学籍异动.休学.查看"),
    APPROVAL_SUSPENSION_APPROVAL(4L, "学籍异动.休学.审批"),;
    private Long value;

    private String permissionSource;

}
