package com.scnujxjy.backendpoint.util;


public enum ResultCode {
    // 成功状态码
    SUCCESS(200, "成功"),

    // 参数错误：10001-19999
    PARAM_IS_INVALID(10001, "参数无效"),

    // 用户错误：20001-29999
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_ERROR(20002, "用户登录失败"),
    USER_ID_GET_FAIL(20003, "用户ID获取失败"),

    // 获取所有角色信息失败
    ROLE_ALL_INFO_GET_FAIL(30001, "获取全部角色信息失败"),
    ADD_NEW_ROLE_FAIL(30002, "添加新的角色信息失败"),
    ADD_NEW_ROLE_FAIL_2(30003, "添加重复的角色信息"),

    // 添加新权限失败
    ADD_NEW_PERMISSION_FAIL(40001, "添加新的权限信息失败"),
    ADD_NEW_PERMISSION_FAIL2(40002, "不允许添加重复的权限"),
    PERMISSION_NOT_FOUND(40003, "更新权限失败，无该ID的权限"),
    UPDATE_PERMISSION_FAIL2(40004, "重复更新权限"),
    UPDATE_PERMISSION_FAIL(40005, "更新权限失败"),
    DELETE_PERMISSION_FAIL(40006, "删除权限失败"),

    // 项目错误
    CREATE_PROJECT_FAIL(50001, "创建项目失败"),
    CREATE_PROJECT_FAIL2(50002, "获取所有项目信息失败"),


    // 接口错误：60001-69999
    INTERFACE_INNER_INVOKE_ERROR(60001, "内部系统接口调用异常"),
    INTERFACE_OUTTER_INVOKE_ERROR(60002, "外部系统接口调用异常"),
    INTERFACE_FORBID_VISIT(60003, "该接口已停止访问"),

    // 权限错误：70001-79999
    PERMISSION_NO_ACCESS(70001, "无访问权限");

    // 操作代码
    int code;

    // 提示信息
    String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

