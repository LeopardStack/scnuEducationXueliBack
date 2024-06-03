package com.scnujxjy.backendpoint.model.ro.admission_information;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EnrollmentPlanExcelVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String describe = "华南师范大学高等学历继续教育2024年招生计划申报表";
    @ExcelProperty(value = {describe, "招生专业"})
    private String majorName;
    @ExcelProperty(value = {describe, "学习形式"})
    private String studyForm;
    @ExcelProperty(value = {describe, "学制"})
    private String educationLength;

    @ExcelProperty(value = {describe, "培养层次"})
    private String trainingLevel;

    @ExcelProperty(value = {describe, "招生人数"})
    private Integer enrollmentNumber;
    @ExcelProperty(value = {describe, "招生对象"})
    private String targerStudents;
    @ExcelProperty(value = {describe, "招生区域"})
    private String schoolLocation;

    @ExcelProperty(value = {describe, "具体办学地点（***教学点)"})
    private String enrollmentRegion;
    @ExcelProperty(value = {describe, "联系电话"})
    private String contactNumber;

    @ExcelProperty(value = "年")
    private String year;
    @ExcelProperty(value = "月")
    private String month;
    @ExcelProperty(value = "日")
    private String day;
}
