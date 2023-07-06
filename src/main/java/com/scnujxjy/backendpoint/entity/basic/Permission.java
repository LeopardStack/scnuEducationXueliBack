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
@TableName("Permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "PermissionID", type = IdType.AUTO)
    private Long PermissionID;

    @TableField("PermissionName")
    private String PermissionName;

    @TableField("Description")
    private String Description;

    @TableField("Resource")
    private String Resource;


    public Permission() {
    }

    public Permission(String permissionName, String description, String resource) {
        PermissionName = permissionName;
        Description = description;
        Resource = resource;
    }
}
