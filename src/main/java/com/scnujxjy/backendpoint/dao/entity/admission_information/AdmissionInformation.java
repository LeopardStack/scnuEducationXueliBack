package com.scnujxjy.backendpoint.dao.entity.admission_information;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 录取学生信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AdmissionInformation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 考生号
     */
    private String studentNumber;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 成人高考录取总分
     */
    private Integer totalScore;

    /**
     * 录取专业代码
     */
    private String majorCode;

    /**
     * 录取专业名称
     */
    private String majorName;

    /**
     * 层次
     */
    private String level;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 原文化程度
     */
    private String originalEducation;

    /**
     * 毕业学校
     */
    private String graduationSchool;

    /**
     * 原毕业日期
     */
    private Date graduationDate;

    /**
     * 学生电话
     */
    private String phoneNumber;

    /**
     * 身份证号码
     */
    private String idCardNumber;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 通信地址
     */
    private String address;

    /**
     * 邮编
     */
    private String postalCode;

    /**
     * 民族
     */
    private String ethnicity;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 准考证号码
     */
    private String admissionNumber;

    /**
     * 考生号（短号）
     */
    private String shortStudentNumber;

    /**
     * 所属学院
     */
    private String college;

    /**
     * 教学点
     */
    private String teachingPoint;

    /**
     * 报到地点（教学点地址）
     */
    private String reportLocation;

    /**
     * 入学照片链接
     */
    private String entrancePhotoUrl;

    /**
     * 录取新生年级
     */
    private String grade;


}
