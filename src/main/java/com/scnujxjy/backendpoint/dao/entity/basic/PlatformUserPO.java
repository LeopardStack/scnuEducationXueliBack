package com.scnujxjy.backendpoint.dao.entity.basic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@TableName("platform_user")
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
     * 微信 openId
     */
    private String wechatOpenId;


}
