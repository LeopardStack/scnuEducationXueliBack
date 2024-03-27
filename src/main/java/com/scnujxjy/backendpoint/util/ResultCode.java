package com.scnujxjy.backendpoint.util;


public enum ResultCode {
    // 成功状态码
    SUCCESS(200, "成功"),
    FAIL(0,"失败"),

    PARTIALSUCCESS(100, "操作部分名单成功"),

    // 参数错误：10001-19999
    PARAM_IS_INVALID(10001, "参数无效"),

    // 用户错误：20001-29999
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_ERROR(20002, "用户登录失败，账号/密码错误"),
    USER_ID_GET_FAIL(20003, "用户ID获取失败"),
    USER_LOGIN_FAIL(20004, "用户角色信息缺失，登录失败"),
    USER_LOGIN_FAIL1(20005, "用户角色信息存在多个，登录失败"),
    USER_LOGIN_FAIL2(20006, "用户角色信息缺失，登录失败"),

    // 课程学习错误码
    UPDATE_COURSE_FAIL1(20007, "课程ID 不存在"),
    UPDATE_COURSE_FAIL2(20008, "课程ID 找不到对应的课程信息"),
    UPDATE_COURSE_FAIL3(20009, "创建课程节点时 节点内容不合法"),
    UPDATE_COURSE_FAIL4(20010, "学生获取观看链接失败"),

    // 获取所有角色信息失败
    ROLE_ALL_INFO_GET_FAIL(30001, "获取全部角色信息失败"),
    ADD_NEW_ROLE_FAIL(30002, "添加新的角色信息失败"),
    ADD_NEW_ROLE_FAIL_2(30003, "添加重复的角色信息"),
    GET_TUTOR_FAIL(2000, "该频道助教数不足，请联系管理员添加"),

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
    CREATE_PROJECT_FAIL3(50003, "删除项目失败"),
    PROJECT_MANAGE_BRIEF_GET(50004, "成功获取所有项目信息"),
    PROJECT_MANAGE_BRIEF_GET2(50005, "成功获取所有项目信息，最新的项目并未上传任何文件"),
    PROJECT_MANAGE_BRIEF_GET_FAIL(50006, "获取所有项目文件信息失败"),
    PROJECT_MANAGE_BRIEF_FILE_DELETE_FAIL(50007, "删除文件失败"),


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
        this.code =  code;
        this.message = message;
    }

    public int getCode() {
        return  code;
    }

    public String getMessage() {
        return message;
    }
}

