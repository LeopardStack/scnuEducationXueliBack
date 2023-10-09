package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseInformationVO {

    private Long id;

    @ExcelProperty(index = 0, value = "年级")
    private String grade;

    @ExcelProperty(index = 1, value = "专业名称")
    private String majorName;

    @ExcelProperty(index = 2, value = "层次")
    private String level;

    @ExcelProperty(index = 3, value = "学习形式")
    private String studyForm;

    @ExcelProperty(index = 4, value = "行政班别")
    private String adminClass;

    @ExcelProperty(index = 5, value = "课程名称")
    private String courseName;

    @ExcelProperty(index = 6, value = "学时数")
    private Integer studyHours;

    @ExcelProperty(index = 7, value = "考核类型")
    private String assessmentType;

    @ExcelProperty(index = 8, value = "授课方式")
    private String teachingMethod;

    @ExcelProperty(index = 9, value = "课程类型")
    private String courseType;

    @ExcelProperty(index = 10, value = "学分")
    private Integer credit;

    @ExcelProperty(index = 11, value = "授课学期")
    private String teachingSemester;

    /**
     * 如果需要覆盖则读取它
     */
    @ExcelProperty(index = 12, value = "备注")
    private String remark;

    /**
     * 课程编号
     */
    private String courseCode;


    /**
     * 班级名称
     */
    private String className;
}