package com.scnujxjy.backendpoint.entity.registration_record_card;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 个人基本信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PersonalInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 民族
     */
    private String ethnicity;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 证件类型
     */
    private String idType;

    /**
     * 证件号码
     */
    private String idNumber;

    /**
     * 邮编
     */
    private String postalCode;

    /**
     * 电话
     */
    private String phoneNumber;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 通信地址
     */
    private String address;

    /**
     * 入学照片
     */
    private String entrancePhoto;

    /**
     * 是否残疾人
     */
    private String isDisabled;

    /**
     * 年级
     */
    private String grade;


}
