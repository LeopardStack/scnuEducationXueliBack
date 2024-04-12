package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class StudentStatusDataSynchronizeVO {
    /**
     * 序号
     */
    @ExcelProperty(value = "序号", index = 0)
    private Long id;

    /**
     * 学号
     */
    @ExcelProperty(value = "学号", index = 1)
    private String studentNumber;

    /**
     * 年级
     */
    @ExcelProperty(value = "年级", index = 2)
    private String grade;

    /**
     * 学院
     */
    @ExcelProperty(value = "学院", index = 3)
    private String college;

    /**
     * 教学点
     */
    @ExcelProperty(value = "教学点", index = 4)
    private String teachingPoint;

    /**
     * 专业名称
     */
    @ExcelProperty(value = "专业名称", index = 5)
    private String majorName;

    /**
     * 学习形式
     */
    @ExcelProperty(value = "学习形式", index = 6)
    private String studyForm;

    /**
     * 层次
     */
    @ExcelProperty(value = "层次", index = 7)
    private String level;

    /**
     * 学制
     */
    @ExcelProperty(value = "学制", index = 8)
    private String studyDuration;

    /**
     * 考生号
     */
    @ExcelProperty(value = "考生号", index = 9)
    private String admissionNumber;

    /**
     * 学籍状态
     */
    @ExcelProperty(value = "学籍状态", index = 10)
    private String academicStatus;

    /**
     * 入学日期，一般为入学年份 + 03
     */
    @ExcelProperty(value = "入学日期", index = 11)
    private Date enrollmentDate;

    /**
     * 身份证号码
     */
    @ExcelProperty(value = "身份证号码", index = 12)
    private String idNumber;

    /**
     * 班级标号
     */
    @ExcelProperty(value = "班级标号", index = 13)
    private String classIdentifier;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", index = 14)
    private String gender;

    /**
     * 出生日期
     */
    @ExcelProperty(value = "出生日期", index = 15)
    private Date birthDate;

    /**
     * 政治面貌
     */
    @ExcelProperty(value = "政治面貌", index = 16)
    private String politicalStatus;

    /**
     * 民族
     */
    @ExcelProperty(value = "民族", index = 17)
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
    @ExcelProperty(value = "学生姓名", index = 18)
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
