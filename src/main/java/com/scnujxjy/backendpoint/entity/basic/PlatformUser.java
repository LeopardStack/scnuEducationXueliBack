package com.scnujxjy.backendpoint.entity.basic;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author leopard
 * @since 2023-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("PlatformUser")
public class PlatformUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "UserID")
    private Long UserID;

    @TableField("RoleID")
    private Long RoleID;

    @TableField("AvatarImagePath")
    private String AvatarImagePath;

    @TableField("Password")
    private String Password;

    @TableField("Username")
    private String Username;


}
