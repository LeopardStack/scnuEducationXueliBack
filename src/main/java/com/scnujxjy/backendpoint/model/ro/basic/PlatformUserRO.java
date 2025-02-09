package com.scnujxjy.backendpoint.model.ro.basic;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PlatformUserRO {
    /**
     * 用户id，自增
     */
    private Long userId;
    /**
     * 角色id
     */
    private Long roleId;

    private List<Long> roleIds;

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
     * 用户 ID 集合
     */
    private List<Long> userIdList;


    /**
     * 微信 openId
     */
    private String wechatOpenId;

    /**
     * 补充角色 id 集合
     */
    private List<Long> supplementaryRoleIdSet;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlatformUserRO that = (PlatformUserRO) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(roleId, that.roleId) &&
                Objects.equals(avatarImagePath, that.avatarImagePath) &&
                Objects.equals(password, that.password) &&
                Objects.equals(username, that.username) &&
                Objects.equals(name, that.name) &&
                Objects.equals(wechatOpenId, that.wechatOpenId) &&
                CollUtil.isEqualList(supplementaryRoleIdSet, that.supplementaryRoleIdSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId, avatarImagePath, password, username, name, wechatOpenId, supplementaryRoleIdSet);
    }
}
