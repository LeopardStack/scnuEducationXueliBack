package com.scnujxjy.backendpoint.model.vo.admission_information;

import com.alibaba.excel.annotation.ExcelProperty;
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
@Accessors(chain = true)
@Builder
public class AdmissionInformationVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @ExcelProperty(value = "自增主键", index = 0)
    private Long id;

    /**
     * 考生号
     */
    @ExcelProperty(value = "考生号", index = 1)
    private String studentNumber;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名", index = 2)
    private String name;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", index = 3)
    private String gender;

    /**
     * 成人高考录取总分
     */
    @ExcelProperty(value = "成人高考录取总分", index = 4)
    private Integer totalScore;

    /**
     * 录取专业代码
     */
    @ExcelProperty(value = "录取专业代码", index = 5)
    private String majorCode;

    /**
     * 录取专业名称
     */
    @ExcelProperty(value = "录取专业名称", index = 6)
    private String majorName;

    /**
     * 层次
     */
    @ExcelProperty(value = "层次", index = 7)
    private String level;

    /**
     * 学习形式
     */
    @ExcelProperty(value = "层次", index = 8)
    private String studyForm;

    /**
     * 原文化程度
     */
    @ExcelProperty(value = "原文化程度", index = 9)
    private String originalEducation;

    /**
     * 毕业学校
     */
    @ExcelProperty(value = "毕业学校", index = 10)
    private String graduationSchool;

    /**
     * 原毕业日期
     */
    @ExcelProperty(value = "原毕业日期", index = 11)
    private Date graduationDate;

    /**
     * 学生电话
     */
    @ExcelProperty(value = "学生电话", index = 12)
    private String phoneNumber;

    /**
     * 身份证号码
     */
    @ExcelProperty(value = "身份证号码", index = 13)
    private String idCardNumber;

    /**
     * 出生日期
     */
    @ExcelProperty(value = "出生日期", index = 14)
    private Date birthDate;

    /**
     * 通信地址
     */
    @ExcelProperty(value = "通信地址", index = 15)
    private String address;

    /**
     * 邮编
     */
    @ExcelProperty(value = "邮编", index = 16)
    private String postalCode;

    /**
     * 民族
     */
    @ExcelProperty(value = "民族", index = 17)
    private String ethnicity;

    /**
     * 政治面貌
     */
    @ExcelProperty(value = "政治面貌", index = 18)
    private String politicalStatus;

    /**
     * 准考证号码
     */
    @ExcelProperty(value = "准考证号码", index = 19)
    private String admissionNumber;

    /**
     * 考生号（短号）
     */
    @ExcelProperty(value = "考生号（短号）", index = 20)
    private String shortStudentNumber;

    /**
     * 所属学院
     */
    @ExcelProperty(value = "所属学院", index = 21)
    private String college;

    /**
     * 教学点
     */
    @ExcelProperty(value = "教学点", index = 22)
    private String teachingPoint;

    /**
     * 报到地点（教学点地址）
     */
    @ExcelProperty(value = "报到地点（教学点地址）", index = 23)
    private String reportLocation;

    /**
     * 入学照片链接
     */
    @ExcelProperty(value = "入学照片链接", index = 24)
    private String entrancePhotoUrl;

    /**
     * 录取新生年级
     */
    @ExcelProperty(value = "录取新生年级", index = 25)
    private String grade;
}
