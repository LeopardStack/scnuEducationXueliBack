package com.scnujxjy.backendpoint.model.vo.admission_information;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = false)
@Builder
public class AdmissionInformationOldSystemImportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 导入空白
     */
    @ExcelProperty(value = "import", index = 0)
    private String importBlank;

    @ExcelProperty(value = "考生号（短号）", index = 1)
    private String shortStudentNumber;

    @ExcelProperty(value = "姓名", index = 2)
    private String name;

    @ExcelProperty(value = "性别", index = 3)
    private String gender;

    @ExcelProperty(value = "总分", index = 4)
    private String totalScore;

    @ExcelProperty(value = "专业代码", index = 5)
    private String majorCode;

    @ExcelProperty(value = "专业名称", index = 6)
    private String majorName;

    @ExcelProperty(value = "层次", index = 7)
    private String level;

    @ExcelProperty(value = "学习形式", index = 8)
    private String studyForm;

    @ExcelProperty(value = "毕业学校", index = 9)
    private String graduationSchool;

    @ExcelProperty(value = "原毕业日期", index = 10)
    private Date graduationDate;

    @ExcelProperty(value = "联系电话", index = 11)
    private String studentPhoneNumber;

    @ExcelProperty(value = "身份证号", index = 12)
    private String idCardNumber;

    @ExcelProperty(value = "出生日期", index = 13)
    private Date birthDate;

    @ExcelProperty(value = "通信地址", index = 14)
    private String studentAddress;

    @ExcelProperty(value = "邮政编码", index = 15)
    private String postalCode;

    @ExcelProperty(value = "文化程度", index = 16)
    private String originalEducation;

    @ExcelProperty(value = "民族", index = 17)
    private String ethnicity;

    @ExcelProperty(value = "政治面貌", index = 18)
    private String politicalStatus;

    @ExcelProperty(value = "准考证号", index = 19)
    private String admissionNumber;
}
