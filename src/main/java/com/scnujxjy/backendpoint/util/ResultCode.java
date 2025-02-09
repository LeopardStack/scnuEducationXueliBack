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
    UPDATE_COURSE_FAI12(21018, "课程公告发布失败，根据课程ID 找不到课程信息"),
    UPDATE_COURSE_FAI13(21019, "课程公告发布失败，上传公告附件到 Minio 失败"),
    UPDATE_COURSE_FAI14(21020, "课程公告发布失败，公告信息插入数据库失败"),
    UPDATE_COURSE_FAI15(21021, "课程公告发布失败，公告信息更新附件列表失败"),
    UPDATE_COURSE_FAI16(21022, "获取课程公告详情信息失败，根据 公告 ID 查询不到信息"),
    UPDATE_COURSE_FAI17(21023, "删除课程公告信息时，删除附件信息失败"),
    UPDATE_COURSE_FAI18(21024, "删除课程公告信息失败"),
    UPDATE_COURSE_FAI19(21025, "课程公告更新失败，根据课程ID 找不到课程信息"),
    UPDATE_COURSE_FAI20(21026, "课程公告更新失败，根据公告ID 找不到原始课程公告信息"),
    UPDATE_COURSE_FAI21(21027, "课程公告更新失败，插入到数据库出现失败"),
    UPDATE_COURSE_FAI22(21028, "课程公告更新失败，公告信息更新附件列表失败"),
    UPDATE_COURSE_FAI23(21029, "课程公告更新失败，更新公告附件到 Minio 失败"),


    UPDATE_COURSE_FAI24(21030, "课程作业创建失败，课程 ID 不能为空"),
    UPDATE_COURSE_FAI25(21031, "课程作业创建失败，根据课程 ID 找不到课程信息"),
    UPDATE_COURSE_FAI26(21032, "课程作业创建失败，课程作业名称不能为空"),
    UPDATE_COURSE_FAI27(21033, "课程作业创建失败，截止时间不能小于当下时间"),
    UPDATE_COURSE_FAI28(21034, "课程作业创建失败，插入数据库失败"),

    UPDATE_COURSE_FAI29(21035, "课程作业删除失败，删除作业附件信息失败"),
    UPDATE_COURSE_FAI30(21036, "课程作业删除失败，删除作业记录失败"),

    UPDATE_COURSE_FAI31(21037, "课程作业编辑失败，课程 ID 不能为空"),
    UPDATE_COURSE_FAI32(21038, "课程作业编辑失败，根据课程 ID 找不到课程信息"),
    UPDATE_COURSE_FAI33(21039, "课程作业编辑失败，课程作业名称不能为空"),
    UPDATE_COURSE_FAI34(21040, "课程作业编辑失败，截止日期不能小于当前时间"),
    UPDATE_COURSE_FAI35(21041, "课程作业编辑失败，课程 ID 不能为空"),
    UPDATE_COURSE_FAI36(21042, "课程作业编辑失败，课程更新信息插入数据库失败"),
    UPDATE_COURSE_FAI37(21043, "课程作业编辑失败，更新附件到 Minio 出现错误"),
    UPDATE_COURSE_FAI38(21044, "课程作业编辑失败，更新附件信息到数据库出现错误"),
    UPDATE_COURSE_FAI39(21045, "课程作业编辑失败，更新附件信息出现错误"),
    UPDATE_COURSE_FAI40(21046, "课程作业编辑失败，更新课程作业信息出现错误"),
    UPDATE_COURSE_FAI41(21047, "课程作业编辑失败，根据作业作业 ID 找不到原作业信息"),
    UPDATE_COURSE_FAI42(21048, "课程作业编辑失败，删除原作业的附件信息失败"),

    UPDATE_COURSE_FAI43(21049, "课程作业查询失败，查询课程作业信息 课程 ID 不能为空"),
    UPDATE_COURSE_FAI44(21050, "课程作业查询失败，根据 课程 ID 查询不到课程信息"),


    COURSE_POST_ASSIGNMENT_FAI1(25001,
            "课程作业提交失败，课程 ID 不能为空"),
    COURSE_POST_ASSIGNMENT_FAI2(25002,
            "课程作业提交失败，根据课程 ID 找不到课程信息"),
    COURSE_POST_ASSIGNMENT_FAI3(25003,
            "课程作业提交失败，作业 ID 不能为空"),
    COURSE_POST_ASSIGNMENT_FAI4(25004,
            "课程作业提交失败，根据作业 ID 找不到作业信息"),
    COURSE_POST_ASSIGNMENT_FAI5(25005,
            "课程作业提交失败，不能提交空作业"),
    COURSE_POST_ASSIGNMENT_FAI6(25006,
            "课程作业提交失败，插入作业信息到数据库失败"),
    COURSE_POST_ASSIGNMENT_FAI7(25007,
            "课程作业提交失败，作业附件上传到 Minio 失败"),
    COURSE_POST_ASSIGNMENT_FAI8(25008,
            "课程作业提交失败，更新作业附件信息到数据库失败"),
    COURSE_POST_ASSIGNMENT_FAI9(25009,
            "课程作业提交失败，已超过作业提交的截止时间"),
    COURSE_POST_ASSIGNMENT_FAI10(25010,
            "课程作业打分失败，作业 ID 不能为空"),
    COURSE_POST_ASSIGNMENT_FAI11(25011,
            "课程作业打分失败，根据作业 ID 找不到学生的提交作业"),
    COURSE_POST_ASSIGNMENT_FAI12(25012,
            "课程作业打分失败，分数提交到数据库失败"),
    COURSE_POST_ASSIGNMENT_FAI13(25013,
            "课程作业打分失败，获取学生作业失败"),
    COURSE_POST_ASSIGNMENT_FAI14(25014,
            "该作业您没提交"),
    COURSE_POST_ASSIGNMENT_FAI15(25015,
            "课程作业提交失败， 删除历史提交作业附件失败"),
    COURSE_POST_ASSIGNMENT_FAI16(25016,
            "课程作业提交失败， 删除历史提交作业记录失败"),
    COURSE_POST_ASSIGNMENT_FAI17(25017,
            "课程作业提交失败， 老师已对作业打分"),

    COURSE_POST_MATERIALS_FAI1(25201,"添加课程资料失败，课程 ID 不能为空"),
    COURSE_POST_MATERIALS_FAI2(25202,"添加课程资料失败，根据课程 ID 找不到课程信息"),
    COURSE_POST_MATERIALS_FAI3(25203,"添加课程资料失败，课程资料不能为空"),
    COURSE_POST_MATERIALS_FAI4(25204,"添加课程资料失败，上传课程资料文件到 Minio 失败"),
    COURSE_POST_MATERIALS_FAI5(25205,"添加课程资料失败，插入课程资料信息到数据库失败"),
    COURSE_POST_MATERIALS_FAI6(25206,"查询课程资料失败，根据课程 ID 找不到课程信息"),
    COURSE_POST_MATERIALS_FAI7(25207,"删除课程资料失败，数据库删除失败"),

    ANNOUNCEMENT_MSG_FAIL1(25301,"创建公告消息失败，公告标题不能为空"),
    ANNOUNCEMENT_MSG_FAIL2(25302,"创建公告消息失败，附件不能超过3个 大小不能超过 100M"),
    ANNOUNCEMENT_MSG_FAIL3(25303,"创建公告消息失败，公告消息插入数据库失败"),
    ANNOUNCEMENT_MSG_FAIL4(25304,"创建公告消息失败，解析筛选实体失败"),
    ANNOUNCEMENT_MSG_FAIL5(25305,"创建公告消息失败，发布公告的时间不能在当下时间之前"),
    ANNOUNCEMENT_MSG_FAIL6(25306,"创建公告消息失败，处理文件上传到 Minio 时出错"),
    ANNOUNCEMENT_MSG_FAIL7(25307,"删除公告消息失败，无法删除公告消息的附件记录"),
    ANNOUNCEMENT_MSG_FAIL8(25308,"删除公告消息失败，无法删除公告消息数据库记录"),
    ANNOUNCEMENT_MSG_FAIL9(25309,"获取公告消息失败，找不到登录用户信息"),
    ANNOUNCEMENT_MSG_FAIL10(25310,"更新公告消息失败，公告标题不能为空"),
    ANNOUNCEMENT_MSG_FAIL11(25311,"更新公告消息失败，附件不能超过3个 大小不能超过 100M"),
    ANNOUNCEMENT_MSG_FAIL12(25312,"更新公告消息失败，解析筛选实体失败"),
    ANNOUNCEMENT_MSG_FAIL13(25313,"更新公告消息失败，发布公告的时间不能在当下时间之前"),
    ANNOUNCEMENT_MSG_FAIL14(25314,"创建公告消息失败，发布公告的发布状态不合法"),
    ANNOUNCEMENT_MSG_FAIL15(25315,"根据公告 ID 查询不到该公告"),
    ANNOUNCEMENT_MSG_FAIL16(25316,"获取不同用户群体的筛选项时，userType 不能为空"),
    ANNOUNCEMENT_MSG_FAIL17(25317,"获取用户发布的公告消息所涉及的用户群体时，公告 ID 不能为空"),
    ANNOUNCEMENT_MSG_FAIL18(25318,"获取用户发布的公告消息所涉及的用户群体时，根据公告 ID 无法查询到公告消息"),
    ANNOUNCEMENT_MSG_FAIL19(25319,"获取用户发布的公告消息所涉及的用户群体时，参数错误，不能为空"),
    ANNOUNCEMENT_MSG_FAIL20(25320,"获取用户发布的公告消息所涉及的用户群体时，由于用户群体类型不合法，结果为空"),

    ENROLLMENT_PLAN_FAIL1(25401,"招生计划设置失败，招生计划设置标志不能为空"),
    ENROLLMENT_PLAN_FAIL2(25402,"招生计划设置失败，招生计划涉及的校外教学点 ID 集合不能为空"),
    ENROLLMENT_PLAN_FAIL3(25403,"招生计划设置失败，存在教学点 ID 不合法"),
    ENROLLMENT_PLAN_FAIL4(25404,"招生计划申报失败，年份不能为空"),
    ENROLLMENT_PLAN_FAIL5(25405,"招生计划申报失败，招生专业名称不能为空"),
    ENROLLMENT_PLAN_FAIL6(25406,"招生计划申报失败，学习形式不能为空"),
    ENROLLMENT_PLAN_FAIL7(25407,"招生计划申报失败，学制不能为空"),
    ENROLLMENT_PLAN_FAIL8(25408,"招生计划申报失败，培养层次不能为空"),
    ENROLLMENT_PLAN_FAIL9(25409,"招生计划申报失败，招生人数不能为空"),
    ENROLLMENT_PLAN_FAIL10(254010,"招生计划申报失败，招生对象不能为空"),
    ENROLLMENT_PLAN_FAIL11(254011,"招生计划申报失败，招生区域不能为空"),
    ENROLLMENT_PLAN_FAIL12(254012,"招生计划申报失败，具体办学地点不能为空"),
    ENROLLMENT_PLAN_FAIL13(254013,"招生计划申报失败，联系电话不能为空"),
    ENROLLMENT_PLAN_FAIL14(254014,"招生计划申报失败，主管院系不能为空"),
    ENROLLMENT_PLAN_FAIL15(254015,"招生计划申报失败，插入数据库记录时失败"),
    ENROLLMENT_PLAN_FAIL16(254016,"招生计划申报失败，学院信息不能为空"),
    ENROLLMENT_PLAN_FAIL17(254017,"招生计划申报失败，教学点信息不能为空"),
    ENROLLMENT_PLAN_FAIL18(254018,"招生计划申报失败，不合法的申报用户"),
    ENROLLMENT_PLAN_FAIL19(254019,"审核招生计划时，招生计划 ID 不能为空"),
    ENROLLMENT_PLAN_FAIL20(254020,"审核招生计划时，根据招生计划 ID 找不到该招生计划信息"),
    ENROLLMENT_PLAN_FAIL21(254021,"打回招生计划时，打回对象只能是教学点管理员或者二级学院管理员"),
    ENROLLMENT_PLAN_FAIL22(254022,"编辑招生计划时，招生计划 ID 不能为空"),
    ENROLLMENT_PLAN_FAIL23(254023,"编辑招生计划时，根据招生计划 ID 找不到该招生计划信息"),
    ENROLLMENT_PLAN_FAIL24(254024,"招生计划编辑失败，年份不能为空"),
    ENROLLMENT_PLAN_FAIL25(254025,"招生计划编辑失败，招生专业名称不能为空"),
    ENROLLMENT_PLAN_FAIL26(254026,"招生计划编辑失败，学习形式不能为空"),
    ENROLLMENT_PLAN_FAIL27(254027,"招生计划编辑失败，学制不能为空"),
    ENROLLMENT_PLAN_FAIL28(254028,"招生计划编辑失败，培养层次不能为空"),
    ENROLLMENT_PLAN_FAIL29(254029,"招生计划编辑失败，招生人数不能为空"),
    ENROLLMENT_PLAN_FAIL30(254030,"招生计划编辑失败，招生对象不能为空"),
    ENROLLMENT_PLAN_FAIL31(254031,"招生计划编辑失败，招生区域不能为空"),
    ENROLLMENT_PLAN_FAIL32(254032,"招生计划编辑失败，具体办学地点不能为空"),
    ENROLLMENT_PLAN_FAIL33(254033,"招生计划编辑失败，联系电话不能为空"),
    ENROLLMENT_PLAN_FAIL34(254034,"招生计划编辑失败，主管院系不能为空"),
    ENROLLMENT_PLAN_FAIL35(254035,"招生计划编辑失败，数据库更新失败"),
    ENROLLMENT_PLAN_FAIL36(254036,"招生计划编辑失败，权限问题"),
    ENROLLMENT_PLAN_FAIL37(254037,"招生计划审批失败，数据库更新失败"),
    ENROLLMENT_PLAN_FAIL38(254038,"招生计划审批失败，权限问题"),
    ENROLLMENT_PLAN_FAIL39(254039,"招生计划打回失败，数据库更新失败"),
    ENROLLMENT_PLAN_FAIL40(254040,"招生计划打回失败，二级学院管理员打回只能给教学点管理员"),
    ENROLLMENT_PLAN_FAIL41(254041,"招生计划打回失败，权限问题"),
    ENROLLMENT_PLAN_FAIL42(254042,"批量提交招生计划失败，筛选参数不能为空"),
    ENROLLMENT_PLAN_FAIL43(254043,"批量提交招生计划失败，权限问题"),
    ENROLLMENT_PLAN_FAIL44(254044,"批量提交招生计划失败，数据库更新失败"),
    ENROLLMENT_PLAN_FAIL45(254045,"批量打回招生计划失败，筛选参数不能为空"),
    ENROLLMENT_PLAN_FAIL46(254046,"批量打回招生计划失败，打回角色不能为空"),
    ENROLLMENT_PLAN_FAIL47(254047,"批量打回招生计划失败，打回角色只能为教学点管理员或者二级学院管理员"),
    ENROLLMENT_PLAN_FAIL48(254048,"批量打回招生计划失败，二级学院管理员打回只能给教学点管理员"),
    ENROLLMENT_PLAN_FAIL49(254049,"批量打回招生计划失败，权限问题"),
    ENROLLMENT_PLAN_FAIL50(254050,"招生计划申报失败，不符合教学点账号信息的教学点管理员申报其他教学点"),
    ENROLLMENT_PLAN_FAIL51(254051,"招生计划提交失败，教学点只能提交本教学点的招生计划"),
    ENROLLMENT_PLAN_FAIL52(254052,"招生计划提交失败，二级学院只能提交本二级学院的招生计划"),
    ENROLLMENT_PLAN_FAIL53(254053,"招生计划打回失败，该招生计划不处于当前角色状态"),
    ENROLLMENT_PLAN_FAIL54(254054,"招生计划删除失败，ID 不能为空"),
    ENROLLMENT_PLAN_FAIL55(254055,"招生计划删除失败，数据库删除失败"),
    ENROLLMENT_PLAN_FAIL56(254056,"招生计划申报失败，学费找不到匹配项"),
    ENROLLMENT_PLAN_FAIL57(254057,"打回招生计划时，备注不能为空"),

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

