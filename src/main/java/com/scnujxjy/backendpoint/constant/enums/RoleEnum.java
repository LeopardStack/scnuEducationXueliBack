package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {

    STUDENT(1, "学生"),
    TEACHER(2, "教师"),
    SECOND_COLLEGE_ADMIN(6, "二级学院管理员"),
    CAIWUBU_ADMIN(5, "财务部管理员"),
    TEACHINGPOINT_ADMIN(7, "教学点管理员"),
    XUELIJIAOYUBU_ADMIN(3, "学历教育部管理员");

    private Integer roleId;

    private String roleName;

    RoleEnum(int roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }
}
