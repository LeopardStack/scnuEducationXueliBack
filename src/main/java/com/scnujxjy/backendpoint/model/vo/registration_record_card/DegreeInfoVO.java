package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class DegreeInfoVO {
    /**
     * 自增主键
     */
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
