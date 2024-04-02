package com.scnujxjy.backendpoint.model.vo.core_data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherInformationErrorRecord {

    @ExcelProperty(index = 0, value = "用户代码")
    private int userId;

    @ExcelProperty(index = 1, value = "姓名")
    private String name;

    @ExcelProperty(index = 2, value = "性别")
    private String gender;

    @ExcelProperty(index = 3, value = "出生年月")
    private String birthDate;

    @ExcelProperty(index = 4, value = "政治面貌")
    private String politicalStatus;

    @ExcelProperty(index = 5, value = "学历")
    private String education;

    @ExcelProperty(index = 6, value = "学位")
    private String degree;

    @ExcelProperty(index = 7, value = "专业技术职称")
    private String professionalTitle;

    @ExcelProperty(index = 8, value = "职称级别")
    private String titleLevel;

    @ExcelProperty(index = 9, value = "毕业学校")
    private String graduationSchool;

    @ExcelProperty(index = 10, value = "现任职单位")
    private String currentPosition;

    @ExcelProperty(index = 11, value = "所属学院")
    private String collegeId;

    @ExcelProperty(index = 12, value = "所属教学点")
    private String teachingPoint;

    @ExcelProperty(index = 13, value = "行政职务")
    private String administrativePosition;

    @ExcelProperty(index = 14, value = "工号/学号")
    private String workNumber;

    @ExcelProperty(index = 15, value = "身份证号码")
    private String idCardNumber;

    @ExcelProperty(index = 16, value = "联系电话")
    private String phone;

    @ExcelProperty(index = 17, value = "电子邮箱")
    private String email;

    @ExcelProperty(index = 18, value = "开始聘用学期")
    private String startTerm;

    @ExcelProperty(index = 19, value = "教师类型1")
    private String teacherType1;

    @ExcelProperty(index = 20, value = "教师类型2")
    private String teacherType2;

    @ExcelProperty(index = 21, value = "导入失败原因")
    private String errorDescription;
}
