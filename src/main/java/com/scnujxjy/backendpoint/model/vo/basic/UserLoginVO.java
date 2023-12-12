package com.scnujxjy.backendpoint.model.vo.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author leopard
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginVO {
    /**
     * SaToken 信息，包含了 token 值、token 名称还有 token 剩余有效时间
     */
    private Object tokenInfo;
    /**
     * 用户权限信息，从 SaToken 框架中获取
     */
    private List<String> permissionList;

    /**
     * 用户角色信息，用于登录返回，判断用户属于教师、管理员还是学生
     */
    private String roleName;

    /**
     * 用户详细角色信息，标识用户具体身份
     */
    private String detailRoleName;

    /**
     * 用户登录ID，也就是平台用户表中的自增 id
     */
    private String userID;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户的微信 openId
     */
    private String openId;

    /**
     * 用户所有角色
     */
    private List<String> allRoleNameList;
}

