package com.scnujxjy.backendpoint.model.vo.core_data;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewStudentNotPayExcelVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    @ExcelProperty(value = "序号", index = 0)
    private Integer index;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名", index = 1)
    private String name;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", index = 2)
    private String gender;

    /**
     * 成人高考录取总分
     */
    @ExcelProperty(value = "成人高考录取总分", index = 3)
    private Integer totalScore;

    /**
     * 录取专业代码
     */
    @ExcelProperty(value = "录取专业代码", index = 4)
    private String majorCode;

    /**
     * 录取专业名称
     */
    @ExcelProperty(value = "录取专业名称", index = 5)
    private String majorName;

    /**
     * 层次
     */
    @ExcelProperty(value = "层次", index = 6)
    private String level;

    /**
     * 学习形式
     */
    @ExcelProperty(value = "层次", index = 7)
    private String studyForm;

    /**
     * 原文化程度
     */
    @ExcelProperty(value = "原文化程度", index = 8)
    private String originalEducation;

    /**
     * 毕业学校
     */
    @ExcelProperty(value = "毕业学校", index = 9)
    private String graduationSchool;

    /**
     * 原毕业日期
     */
    @ExcelProperty(value = "原毕业日期", index = 10)
    private Date graduationDate;

    /**
     * 最新的学生个人电话
     */
    @ExcelProperty(value = "学生电话", index = 11)
    private String phoneNumber;

    /**
     * 身份证号码
     */
    @ExcelProperty(value = "身份证号码", index = 12)
    private String idCardNumber;

    /**
     * 出生日期
     */
    @ExcelProperty(value = "出生日期", index = 13)
    private Date birthDate;

    /**
     * 通信地址
     */
    @ExcelProperty(value = "学生通信地址", index = 14)
    private String studentAddress;

    /**
     * 邮编
     */
    @ExcelProperty(value = "邮编", index = 15)
    private String postalCode;

    /**
     * 民族
     */
    @ExcelProperty(value = "民族", index = 16)
    private String ethnicity;

    /**
     * 政治面貌
     */
    @ExcelProperty(value = "政治面貌", index = 17)
    private String politicalStatus;

    /**
     * 准考证号码
     */
    @ExcelProperty(value = "准考证号码", index = 18)
    private String admissionNumber;

    /**
     * 考生号（短号）
     */
    @ExcelProperty(value = "考生号（短号）", index = 19)
    private String shortStudentNumber;

    /**
     * 所属学院
     */
    @ExcelProperty(value = "所属学院", index = 20)
    private String collegeName;

    /**
     * 教学点
     */
    @ExcelProperty(value = "教学点", index = 21)
    private String teachingPointName;

    /**
     * 报到地点（教学点地址）
     */
    @ExcelProperty(value = "报到地点（教学点地址）", index = 22)
    private String reportLocation;


    /**
     * 录取新生年级
     */
    @ExcelProperty(value = "录取新生年级", index = 23)
    private String grade;


    @ExcelProperty(value = "缴费方式", index = 24)
    private String paymentType;

    @ExcelProperty(value = "学费标准", index = 25)
    private String tuition;

    @ExcelProperty(value = "招生科类", index = 26)
    private String admissionType;

    @ExcelProperty(value = "是否已确认", index = 27)
    private Integer isConfirmed;
}
