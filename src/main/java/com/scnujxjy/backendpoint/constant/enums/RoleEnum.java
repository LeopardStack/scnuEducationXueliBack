package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {

    STUDENT(1, "学生"),
    TEACHER(2, "教师"),
    XUELIJIAOYUBU_ADMIN(3, "学历教育部管理员"),
    ADMISSIONS_DEPARTMENT_ADMINISTRATOR(4, "招生部管理员"),
    CAIWUBU_ADMIN(5, "财务部管理员"),
    SECOND_COLLEGE_ADMIN(6, "二级学院管理员"),
    TEACHING_POINT_ADMIN(7, "教学点管理员"),
    SUPER_ADMIN(8, "超级管理员"),
    INSTRUCTIONAL_SUPERVISION(9, "教学督导"),
    STUDENT_STATUS_CHANGE_INSPECTOR(10, "学籍异动查看员"),
    COURSE_LEARNING_CUR(11, "课程学习的增加修改查询"),
    STUDENT_STATUS_CHANGE_AUDITOR(12, "学籍异动审核员");

    private Long roleId;

    private String roleName;

    RoleEnum(long roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }
}
