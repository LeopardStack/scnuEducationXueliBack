package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ClassInformationOldSystemImportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(index=0, value = "导入")
    private String importBlank;

    @ExcelProperty(index=1, value = "备注")
    private String remark;

    @ExcelProperty(index=2, value = "年级")
    private String grade;

    @ExcelProperty(index=3, value = "入学时间")
    private String admissionDate;

    @ExcelProperty(index=4, value = "毕业时间")
    private String graduateDate;

    @ExcelProperty(index=5, value = "所属学院")
    private String college;

    @ExcelProperty(index=6, value = "班名")
    private String className;

    @ExcelProperty(index=7, value = "专业名称")
    private String majorName;

    @ExcelProperty(index=8, value = "专业代码")
    private String majorCode;

    @ExcelProperty(index=9, value = "层次")
    private String level;

    @ExcelProperty(index=10, value = "学制")
    private String studyDuration;

    @ExcelProperty(index=11, value = "形式")
    private String studyForm;

    @ExcelProperty(index=12, value = "学费标准")
    private String tuitionFee;

    @ExcelProperty(index=13, value = "前缀")
    private String studentNumberPrefix;

    @ExcelProperty(index=14, value = "标识")
    private String classIdentity;

    @ExcelProperty(index=15, value = "状态")
    private String status;

    @ExcelProperty(index=16, value = "班号")
    private String classNumber;

    @ExcelProperty(index=17, value = "院系代码")
    private String collegeCode;
}
