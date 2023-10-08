package com.scnujxjy.backendpoint.dao.entity.registration_record_card;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class StudentStatusCommonPO {
    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学号
     */
    @ExcelProperty(value = "学号", index = 0)
    private String studentNumber;

    /**
     * 年级
     */
    @ExcelProperty(value = "年级", index = 1)
    private String grade;

    /**
     * 学院
     */
    @ExcelProperty(value = "学院", index = 2)
    private String college;

    /**
     * 教学点
     */
    @ExcelProperty(value = "教学点", index = 3)
    private String teachingPoint;

    /**
     * 专业名称
     */
    @ExcelProperty(value = "专业名称", index = 4)
    private String majorName;

    /**
     * 学习形式
     */
    @ExcelProperty(value = "学习形式", index = 5)
    private String studyForm;

    /**
     * 层次
     */
    private String level;

    /**
     * 学制
     */
    private String studyDuration;

    /**
     * 考生号
     */
    private String admissionNumber;

    /**
     * 学籍状态
     */
    private String academicStatus;

    /**
     * 入学日期，一般为入学年份 + 03
     */
    private Date enrollmentDate;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 班级标号
     */
    private String classIdentifier;

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
     * 学生姓名
     */
    private String name;

    /**
     * 原毕业学校
     */
    private String graduationSchool;

    /**
     * 原文化程度
     */
    private String originalEducation;

    /**
     * 原毕业日期
     */
    private Date graduationDate;

    /**
     * 毕业论文ID
     */
    private Long thesisId;

    /**
     * 毕业照片
     */
    private String graduationPhoto;

    /**
     * 毕业证号
     */
    private String graduationNumber;

    /**
     * 文号
     */
    private String documentNumber;
}
