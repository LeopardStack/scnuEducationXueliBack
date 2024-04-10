package com.scnujxjy.backendpoint.model.ro.admission_information;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdmissionInformationRO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @ExcelIgnore
    private Long id;

    /**
     * 考生号
     */
    @ExcelProperty(index=0, value = "考生号")
    private String shortStudentNumber;

    /**
     * 姓名
     */
    @ExcelProperty(index=1, value = "姓名")
    private String name;

    /**
     * 性别
     */
    @ExcelProperty(index=2, value = "性别")
    private String gender;

    /**
     * 成人高考录取总分
     */
    @ExcelProperty(index=3, value = "成人高考录取总分")
    private Integer totalScore;

    /**
     * 录取专业代码
     */
    @ExcelProperty(index=4, value = "录取专业代码")
    private String majorCode;

    /**
     * 录取专业名称
     */
    @ExcelProperty(index=5, value = "录取专业名称")
    private String majorName;

    /**
     * 层次
     */
    @ExcelProperty(index=6, value = "层次")
    private String level;

    /**
     * 学习形式
     */
    @ExcelProperty(index=7, value = "学习形式")
    private String studyForm;

    /**
     * 原文化程度
     */
    @ExcelProperty(index=8, value = "原文化程度")
    private String originalEducation;

    /**
     * 毕业学校
     */
    @ExcelProperty(index=9, value = "毕业学校")
    private String graduationSchool;

    /**
     * 原毕业日期
     */
    @ExcelProperty(index=10, value = "原毕业日期")
    private String graduationDate;

    /**
     * 学生电话
     */
    @ExcelProperty(index=11, value = "学生电话")
    private String phoneNumber;

    /**
     * 身份证号码
     */
    @ExcelProperty(index=12, value = "身份证号码")
    private String idCardNumber;

    /**
     * 出生日期
     */
    @ExcelProperty(index=13, value = "出生日期")
    private String birthDate;

    /**
     * 通信地址
     */
    @ExcelProperty(index=14, value = "通信地址")
    private String address;

    /**
     * 邮编
     */
    @ExcelProperty(index=15, value = "邮编")
    private String postalCode;

    /**
     * 民族
     */
    @ExcelProperty(index=16, value = "民族")
    private String ethnicity;

    /**
     * 政治面貌
     */
    @ExcelProperty(index=17, value = "政治面貌")
    private String politicalStatus;

    /**
     * 准考证号码
     */
    @ExcelProperty(index=18, value = "准考证号码")
    private String admissionNumber;


    /**
     * 所属学院
     */
    @ExcelProperty(index=19, value = "所属学院")
    private String college;

    /**
     * 教学点
     */
    @ExcelProperty(index=20, value = "教学点")
    private String teachingPoint;

    /**
     * 教学点集合
     * 因为 存在一个教务员管理着两个教学点
     */
    private List<String> teachingPoints;

    /**
     * 报到地点（教学点地址）
     */
    @ExcelProperty(index=21, value = "报到地点（教学点地址）")
    private String reportLocation;

    /**
     * 入学照片链接
     */
    private String entrancePhotoUrl;

    /**
     * 录取新生年级
     */
    @ExcelProperty(index=23, value = "录取新生年级")
    private String grade;

    /**
     * 是否确认
     */
    private String isConfirmed;
}
