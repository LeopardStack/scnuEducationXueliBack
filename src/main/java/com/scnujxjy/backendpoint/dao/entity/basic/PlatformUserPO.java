package com.scnujxjy.backendpoint.dao.entity.basic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.scnujxjy.backendpoint.handler.type_handler.LongTypeHandler;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName(value = "platform_user", autoResultMap = true)
public class PlatformUserPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id，自增
     */
    @TableId(type = IdType.AUTO)
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
     * 补充角色id集合
     * <p>create、insert的typeHandler生效</p>
     * <p>注意update时typeHandler会失效，参考：</p>
     *
     * @see com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper#updateUser(PlatformUserPO)
     */
    @TableField(typeHandler = LongTypeHandler.class)
    private List<Long> supplementaryRoleIdSet;

    /**
     * 是否允许登录 -1 表示禁止登录
     */
    private String login_on;
}
