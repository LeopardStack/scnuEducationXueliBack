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
@TableName("Student")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "UserID", type = IdType.AUTO)
    private Long UserID;

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

    @TableField("Company")
    private String Company;

    @TableField("GraduatedFrom")
    private String GraduatedFrom;

    @TableField("Education")
    private String Education;

    @TableField("ImagePath")
    private String ImagePath;


}
