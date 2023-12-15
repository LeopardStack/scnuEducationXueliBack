package com.scnujxjy.backendpoint.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PermissionEnum {
    VIEW_NEW_STUDENT_INFORMATION("新生信息.查询");

    String permission;
}
