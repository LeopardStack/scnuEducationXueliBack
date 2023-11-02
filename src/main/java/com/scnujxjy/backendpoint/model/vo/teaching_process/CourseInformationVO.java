package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.alibaba.excel.annotation.ExcelIgnore;
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

    @ExcelIgnore
    private Long id;

    @ExcelProperty(index = 0, value = "年级")
    private String grade;

    @ExcelProperty(index = 1, value = "学院")
    private String college;

    @ExcelProperty(index = 2, value = "专业名称")
    private String majorName;

    @ExcelProperty(index = 3, value = "层次")
    private String level;

    @ExcelProperty(index = 4, value = "学习形式")
    private String studyForm;


    @ExcelIgnore
    private String adminClass;

    /**
     * 班级名称
     */
    @ExcelProperty(index = 5, value = "行政班别")
    private String className;

    @ExcelProperty(index = 6, value = "课程名称")
    private String courseName;

    @ExcelProperty(index = 7, value = "学时数")
    private Integer studyHours;

    @ExcelProperty(index = 8, value = "考核类型")
    private String assessmentType;

    @ExcelProperty(index = 9, value = "授课方式")
    private String teachingMethod;

    @ExcelProperty(index = 10, value = "课程类型")
    private String courseType;

    @ExcelProperty(index = 11, value = "学分")
    private Integer credit;

    @ExcelProperty(index = 12, value = "授课学期")
    private String teachingSemester;

    /**
     * 如果需要覆盖则读取它
     */
    @ExcelProperty(index = 13, value = "备注")
    private String remark;

    /**
     * 课程编号
     */
    @ExcelProperty(index = 14, value = "课程编号")
    private String courseCode;

    /**
     * 课程封面图 Minio 地址
     */
    @ExcelIgnore
    private String courseCover;

}