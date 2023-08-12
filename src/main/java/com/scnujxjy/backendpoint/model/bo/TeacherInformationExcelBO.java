package com.scnujxjy.backendpoint.model.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherInformationExcelBO {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("出生年月")
    private Date birthDate;

    @ExcelProperty("政治面貌")
    private String politicalStatus;

    @ExcelProperty("学历")
    private String education;

    @ExcelProperty("学位")
    private String degree;

    @ExcelProperty("专业技术职称")
    private String professionalTitle;

    @ExcelProperty("职称级别")
    private String titleLevel;

    @ExcelProperty("毕业学校")
    private String graduationSchool;

    @ExcelProperty("现任职单位")
    private String currentPosition;

    @ExcelProperty("所属学院")
    private String collegeId;

    @ExcelProperty("所属教学点")
    private String teachingPoint;

    @ExcelProperty("行政职务")
    private String administrativePosition;

    @ExcelProperty("工号/学号")
    private String workNumber;

    @ExcelProperty("身份证号码")
    private String idCardNumber;

    @ExcelProperty("联系电话")
    private String phone;

    @ExcelProperty("电子邮箱")
    private String email;

    @ExcelProperty("开始聘用学期")
    private String startTerm;

    @ExcelProperty("类型")
    private String teacherType;
}
