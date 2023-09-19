package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PersonalInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 自增主键
     */
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

    /**
     * 学生姓名
     */
    private String name;
}
