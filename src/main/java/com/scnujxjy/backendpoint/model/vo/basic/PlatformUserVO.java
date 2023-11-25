package com.scnujxjy.backendpoint.model.vo.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PlatformUserVO {
    /**
     * 用户id，自增
     */
    private Long userId;
    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 用户头像地址
     */
    private String avatarImagePath;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户名，用于登录
     */
    private String username;

    /**
     * 用户姓名
     */
    private String name;


    /**
     * 微信 openId
     */
    private String wechatOpenId;
    /**
     * 补充角色 id集合
     */
    private List<Long> supplementaryRoleIdSet;

}
