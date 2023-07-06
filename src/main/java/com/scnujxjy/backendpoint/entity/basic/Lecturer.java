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
@TableName("Lecturer")
public class Lecturer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "UserID", type = IdType.AUTO)
    private Long UserID;

    @TableField("Name")
    private String Name;

    @TableField("Gender")
    private String Gender;

    @TableField("IDCardNumber")
    private String IDCardNumber;

    @TableField("Email")
    private String Email;

    @TableField("Address")
    private String Address;

    @TableField("PhoneNumber")
    private String PhoneNumber;

    @TableField("WorkPlace")
    private String WorkPlace;

    @TableField("ResearchField")
    private String ResearchField;

    @TableField("Title")
    private String Title;

    @TableField("Description")
    private String Description;


}
