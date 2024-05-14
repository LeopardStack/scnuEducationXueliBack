package com.scnujxjy.backendpoint.model.bo.platform_message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ManagerInfoBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 姓名
     */
    private String name;

    /**
     * 基本角色
     */
    private String roleName;

    /**
     * 工号
     */
    private String workNumber;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 部门
     */
    private String department;

    /**
     * 学院
     */
    private String collegeName;

    /**
     * 教学点名称
     */
    private String teachingPointName;


    /**
     * 已读未读
     */
    private boolean isRead;

}
