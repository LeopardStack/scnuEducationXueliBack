package com.scnujxjy.backendpoint.util;


import cn.dev33.satoken.util.SaResult;
import lombok.Getter;

@Getter
public enum ResultCode {
    // 成功状态码
    SUCCESS(200, "成功"),
    FAIL(500,"失败"),
    ROLE_INFO_FAIL1(500,"获取用户角色信息为空"),

    PARTIALSUCCESS(100, "操作部分名单成功"),

    // 参数错误：10001-19999
    PARAM_IS_INVALID(10001, "参数无效"),
    PARAM_IS_NULL(10002, "参数不能为空"),
    DATABASE_INSERT_ERROR(10003, "数据库插入数据失败"),
    DATABASE_DELETE_ERROR(10004, "已删除，无需再删除"),
    DATABASE_DELETE_ERROR2(10005, "数据库删除失败"),

    // 用户错误：20001-29999
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_ERROR(20002, "用户登录失败，账号/密码错误"),
    USER_ID_GET_FAIL(20003, "用户ID获取失败"),
    USER_LOGIN_FAIL(20004, "用户角色信息缺失，登录失败"),
    USER_LOGIN_FAIL1(20005, "用户角色信息存在多个，登录失败"),
    USER_LOGIN_FAIL2(20006, "用户角色信息缺失，登录失败"),
    TEACHING_POINT_FAIL1(20007, "创建新的教学点，教学点名称不能为空"),
    TEACHING_POINT_FAIL2(20008, "创建新的教学点，教学点简称不能为空"),
    TEACHING_POINT_FAIL3(20009, "创建新的教学点，教学点名称重复"),
    TEACHING_POINT_FAIL4(20010, "根据教学点ID查询不到教学点信息"),
    USER_LOGIN_FAIL3(20011, "修改密码时入参不能为空"),
    USER_LOGIN_FAIL4(20012, "密码不符合格式规范"),
    USER_LOGIN_FAIL5(20013, "修改密码时插入数据库失败"),
    COLLEGE_FAIL1(20014, "没有找到合适的二级学院 ID 来创建新的二级学院"),
    COLLEGE_FAIL2(20015, "二级学院已经存在了"),
    COLLEGE_FAIL3(20016, "二级学院插入数据库失败"),
    COLLEGE_FAIL4(20017, "非法的二级学院 ID"),
    TEACHER_INFORMATION_FAIL1(20018, "添加老师的姓名不能为空"),
    TEACHER_INFORMATION_FAIL2(20019, "教师类型 主讲/辅导教师 不能为空"),
    TEACHER_INFORMATION_FAIL3(20020, "教师的工号/学号、身份证号码不能同时为空"),
    TEACHER_INFORMATION_FAIL4(20021, "创建教师平台账户时，教师的工号/学号、身份证号码不合理"),
    TEACHER_INFORMATION_FAIL5(20022, "创建教师平台账户时，该教师账户已存在"),
    TEACHER_INFORMATION_FAIL6(20023, "创建教师平台账户时，账号长度不能小于 6"),
    TEACHER_INFORMATION_FAIL7(20024, "修改教师信息时，根据 user_id 找不到该教师"),
    TEACHER_INFORMATION_FAIL8(20025, "修改教师信息时，更新数据库失败"),
    TEACHER_INFORMATION_FAIL9(20026, "创建教师时，插入数据库失败"),
    TEACHER_INFORMATION_FAIL10(20027, "删除教师失败"),
    TEACHER_INFORMATION_FAIL11(20028, "批量导入师资表失败"),


    COLLEGE_INFORMATION_FAIL1(22001, "新增学院教务员，名字不能为空"),
    COLLEGE_INFORMATION_FAIL2(22002, "新增学院教务员，工号或者身份证号码不能同时为空"),
    COLLEGE_INFORMATION_FAIL3(22003, "新增学院教务员，学院ID 不能为空"),
    COLLEGE_INFORMATION_FAIL4(22004, "新增学院教务员，根据学院ID 找不到该学院信息"),
    COLLEGE_INFORMATION_FAIL5(22005, "更新学院教务员，名字不能为空"),
    COLLEGE_INFORMATION_FAIL6(22006, "更新学院教务员，工号或者身份证号码不能同时为空"),
    COLLEGE_INFORMATION_FAIL7(22007, "更新学院教务员，学院ID 不能为空"),
    COLLEGE_INFORMATION_FAIL8(22008, "更新学院教务员，根据学院ID 找不到该学院信息"),


    TEACHINGPOINT_INFORMATION_FAIL1(23001, "新增教学点管理人员信息，教学点 ID 不能为空"),
    TEACHINGPOINT_INFORMATION_FAIL2(23002, "新增教学点管理人员信息，根据教学点 ID 找不到对应的教学点"),
    TEACHINGPOINT_INFORMATION_FAIL3(23003, "新增教学点管理人员信息，管理人员必须提供身份证号码或者手机号码"),
    TEACHINGPOINT_INFORMATION_FAIL4(23004, "新增教学点管理人员信息，管理人员的身份证号码或者手机号码长度必须大于5"),
    TEACHINGPOINT_INFORMATION_FAIL5(23005, "修改教学点管理人员信息，通过 userId 找不到原教学点管理人员信息记录"),

    VIDEO_INFORMATION_FAIL1(24001, "没有找到学生的网梯登录信息"),

    // 课程学习错误码
    UPDATE_COURSE_FAIL1(21007, "课程ID 不存在"),
    UPDATE_COURSE_FAIL2(21008, "课程ID 找不到对应的课程信息"),
    UPDATE_COURSE_FAIL3(21009, "创建课程节点时 节点内容不合法"),
    UPDATE_COURSE_FAIL4(21010, "学生获取观看链接失败"),
    UPDATE_COURSE_FAIL5(21011, "通过课程 ID 获取不到课程信息"),
    UPDATE_COURSE_FAIL6(21012, "删除课程失败"),
    UPDATE_COURSE_FAI7(21013, "添加重修名单时，课程主键 ID 找不到"),
    UPDATE_COURSE_FAI8(21014, "添加重修名单时，该学生已经在正常的班级里"),
    UPDATE_COURSE_FAI9(21015, "添加重修名单时，该学生已经在重修库中"),
    UPDATE_COURSE_FAI10(21016, "添加白名单失败"),
    UPDATE_COURSE_FAI11(21017, "添加重修学生失败"),

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

    //直播课程相关错误码
    SELECT_SECTION_FAIL(50008, "不存在该门节点课程，请联系管理员"),
    SELECT_VIDEO_FAIL(50009, "不存在该门直播间，请联系管理员"),
    SELECT_COURSE_FAIL(50010, "不存在该门直播间，请联系管理员"),


    // 接口错误：60001-69999
    INTERFACE_INNER_INVOKE_ERROR(60001, "内部系统接口调用异常"),
    INTERFACE_OUTTER_INVOKE_ERROR(60002, "外部系统接口调用异常"),
    INTERFACE_FORBID_VISIT(60003, "该接口已停止访问"),

    // 通用操作错误
    COMMON_OPERATION_ERROR(500, "通用操作错误"),

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

    public SaResult generateErrorResultInfo(){
        return SaResult.error(message).setCode(code);
    }

    public static SaResult generateOKInfo(String msg){
        return SaResult.ok(msg);
    }
}

