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
@TableName("PlatformManager")
public class PlatformManager implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "UserID")
    private Long UserID;

    @TableField("Name")
    private String Name;

    @TableField("Gender")
    private String Gender;

    @TableField("PhoneNumber")
    private String PhoneNumber;


}
