package com.scnujxjy.backendpoint.entity.registration_record_card;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 学位信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DegreeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 考生号
     */
    private String admissionNumber;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 姓名
     */
    private String name;

    /**
     * 姓名拼音
     */
    private String namePinyin;

    /**
     * 性别
     */
    private String gender;

    /**
     * 民族
     */
    private String ethnicity;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 证件类型
     */
    private String idType;

    /**
     * 证件号码
     */
    private String idNumber;

    /**
     * 校长名
     */
    private String principalName;

    /**
     * 证书姓名
     */
    private String certificateName;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 入学时间
     */
    private Date admissionDate;

    /**
     * 毕业时间
     */
    private Date graduationDate;

    /**
     * 学制
     */
    private String studyPeriod;

    /**
     * 培养形式
     */
    private String studyForm;

    /**
     * 学位证号
     */
    private String degreeCertificateNumber;

    /**
     * 学位授予日期
     */
    private Date degreeDate;

    /**
     * 学位类型
     */
    private String degreeType;

    /**
     * 学位办理文号
     */
    private String degreeProcessNumber;

    /**
     * 学位外语通过编号
     */
    private String degreeForeignLanguagePassNumber;

    /**
     * 所属学院
     */
    private String college;

    /**
     * 毕业证号
     */
    private String graduationCertificateNumber;

    /**
     * 平均分
     */
    private BigDecimal averageScore;

    /**
     * 授予学院
     */
    private String awardingCollege;

    /**
     * 学位外语科目
     */
    private String degreeForeignLanguageSubject;

    /**
     * 学位外语通过日期
     */
    private Date degreeForeignLanguagePassDate;

    /**
     * 学位照片链接
     */
    private String degreePhotoUrl;


}
