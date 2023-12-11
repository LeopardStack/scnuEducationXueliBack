package com.scnujxjy.backendpoint.model.vo.newStudentVo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationVO;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = false)
@Builder
public class NewStudentExcel  implements Serializable {

    @ExcelProperty(value = "考生号",index = 0)
    private String studentNumber;

    @ExcelProperty(value = "姓名",index = 1)
    private String name;

    @ExcelProperty(value = "性别",index = 2)
    private String gender;

    @ExcelProperty(value = "录取总分",index = 3)
    private Integer totalScore;

    @ExcelProperty(value = "录取专业代码", index = 4)
    private String majorCode;

    @ExcelProperty(value = "专业名称",index = 5)
    private String majorName;

    @ExcelProperty(value = "层次",index = 6)
    private String level;

    @ExcelProperty(value = "学习形式",index = 7)
    private String studyForm;

    @ExcelProperty(value = "学习形式", index = 8)
    private  String originalEducation;

    @ExcelProperty(value = "毕业院校",index = 9)
    private String graduationSchool;

    @ExcelProperty(value = "毕业日期",index = 10)
    private Date graduationDate;

    @ExcelProperty(value = "联系电话",index = 11)
    private String phoneNumber;

    @ExcelProperty(value = "身份证号码",index = 12)
    private String idCardNumber;

    @ExcelProperty(value = "出生日期",index = 13)
    private Date birthDate;

    @ExcelProperty(value = "联系地址",index = 14)
    private String address;

    @ExcelProperty(value = "邮编",index = 15)
    private String postalCode;

    @ExcelProperty(value = "民族",index = 16)
    private String ethnicity;

    @ExcelProperty(value = "政治面貌",index = 17)
    private String politicalStatus;

    @ExcelProperty(value = "准考证号码",index = 18)
    private String admissionNumber;

    @ExcelProperty(value = "所属学院",index = 19)
    private  String college;

    @ExcelProperty(value = "所属教学点",index = 20)
    private String teachingPoint;

    @ExcelProperty(value = "报道地址",index = 21)
    private String reportLocation;

    @ExcelProperty(value = "入学照片",index = 22)
    private String entrancePhotoUrl;

    @ExcelProperty(value = "年级",index = 23)
    private String grade;
}
