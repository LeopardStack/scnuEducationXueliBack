package com.scnujxjy.backendpoint.model.ro.core_data;

import lombok.Data;

@Data
public class TeacherInformationRequest {
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 身份证号
     */
    private String idNumber;
    /**
     * 性别
     */
    private String gender;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 学院
     */
    private String college;
    /**
     * 教师类型
     */
    private String teacherType;

    private Integer page;

    private Integer pageSize;

    private Integer offset;

}
