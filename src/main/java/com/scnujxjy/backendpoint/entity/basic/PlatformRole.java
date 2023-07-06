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
@TableName("PlatformRole")
public class PlatformRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "RoleID", type = IdType.AUTO)
    private Long RoleID;

    @TableField("RoleName")
    private String RoleName;

    @TableField("RoleDescription")
    private String roleDescription;


}
