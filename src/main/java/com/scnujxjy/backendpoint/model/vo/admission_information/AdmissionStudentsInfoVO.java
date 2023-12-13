package com.scnujxjy.backendpoint.model.vo.admission_information;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdmissionStudentsInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    @ExcelProperty(index = 1, value = "序号")
    private Integer index;

    /**
     * 考生号
     */
    @ExcelIgnore
    private String studentNumber;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名", index = 0)
    private String name;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", index = 1)
    private String gender;

    /**
     * 成人高考录取总分
     */
    @ExcelProperty(value = "成人高考录取总分", index = 2)
    private Integer totalScore;

    /**
     * 录取专业代码
     */
    @ExcelProperty(value = "录取专业代码", index = 3)
    private String majorCode;

    /**
     * 录取专业名称
     */
    @ExcelProperty(value = "录取专业名称", index = 4)
    private String majorName;

    /**
     * 层次
     */
    @ExcelProperty(value = "层次", index = 5)
    private String level;

    /**
     * 学习形式
     */
    @ExcelProperty(value = "层次", index = 6)
    private String studyForm;

    /**
     * 原文化程度
     */
    @ExcelProperty(value = "原文化程度", index = 7)
    private String originalEducation;

    /**
     * 毕业学校
     */
    @ExcelProperty(value = "毕业学校", index = 8)
    private String graduationSchool;

    /**
     * 原毕业日期
     */
    @ExcelProperty(value = "原毕业日期", index = 9)
    private Date graduationDate;

    /**
     * 学生电话
     */
    @ExcelProperty(value = "学生电话", index = 10)
    private String phoneNumber;

    /**
     * 身份证号码
     */
    @ExcelProperty(value = "身份证号码", index = 11)
    private String idCardNumber;

    /**
     * 出生日期
     */
    @ExcelProperty(value = "出生日期", index = 12)
    private Date birthDate;

    /**
     * 通信地址
     */
    @ExcelProperty(value = "学生通信地址", index = 13)
    private String studentAddress;

    /**
     * 邮编
     */
    @ExcelProperty(value = "邮编", index = 14)
    private String postalCode;

    /**
     * 民族
     */
    @ExcelProperty(value = "民族", index = 15)
    private String ethnicity;

    /**
     * 政治面貌
     */
    @ExcelProperty(value = "政治面貌", index = 16)
    private String politicalStatus;

    /**
     * 准考证号码
     */
    @ExcelProperty(value = "准考证号码", index = 17)
    private String admissionNumber;

    /**
     * 考生号（短号）
     */
    @ExcelProperty(value = "考生号（短号）", index = 18)
    private String shortStudentNumber;

    /**
     * 所属学院
     */
    @ExcelProperty(value = "所属学院", index = 19)
    private String collegeName;

    /**
     * 教学点
     */
    @ExcelProperty(value = "教学点", index = 20)
    private String teachingPointName;

    /**
     * 报到地点（教学点地址）
     */
    @ExcelProperty(value = "报到地点（教学点地址）", index = 21)
    private String reportLocation;

    /**
     * 入学照片链接
     */
    @ExcelProperty(value = "入学照片链接", index = 22)
    private String entrancePhotoUrl;

    /**
     * 录取新生年级
     */
    @ExcelProperty(value = "录取新生年级", index = 23)
    private String grade;

    @ExcelProperty(value = "教学点地址", index = 24)
    private String teachingPointAddress;

    @ExcelProperty(value = "教学点联系电话", index = 25)
    private String teachingPointPhone;

    @ExcelProperty(value = "学院地址", index = 26)
    private String collegeAddress;

    @ExcelProperty(value = "学院联系电话", index = 27)
    private String collegePhone;

    @ExcelProperty(value = "缴费方式", index = 28)
    private String paymentType;

    @ExcelProperty(value = "学费标准", index = 29)
    private String tuition;

    @ExcelProperty(value = "招生科类", index = 30)
    private String admissionType;

}
