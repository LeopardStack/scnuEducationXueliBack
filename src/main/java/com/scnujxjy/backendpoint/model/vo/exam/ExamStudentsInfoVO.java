package com.scnujxjy.backendpoint.model.vo.exam;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExamStudentsInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    @ExcelProperty(index = 1, value = "序号")
    private Integer index;

    /**
     * 学院名称
     */
    @ExcelProperty(index = 2, value = "教学学院名称")
    private String college;

    /**
     * 年级
     */
    @ExcelProperty(index = 3, value = "年级")
    private String grade;

    /**
     * 专业名称
     */
    @ExcelProperty(index = 4, value = "专业名称")
    private String majorName;

    /**
     * 层次
     */
    @ExcelProperty(index = 5, value = "层次")
    private String level;

    /**
     * 学习形式
     */
    @ExcelProperty(index = 6, value = "学习形式")
    private String studyForm;

    /**
     * 教学班别
     */
    @ExcelProperty(index = 7, value = "教学班别")
    private String teachingClass;

    /**
     * 行政班别
     */
    @ExcelProperty(index = 8, value = "行政班别")
    private String className;


    /**
     * 本学期采用“线上机考-人脸识别”机考课程名称
     */
    @ExcelProperty(index = 9, value = "本学期采用“线上机考-人脸识别”机考课程名称")
    private String courseName;


    /**
     * 主讲教师（命题）
     */
    @ExcelProperty(index = 10, value = "主讲教师（命题）")
    private String mainTeacherName;

    /**
     * 主讲老师电话
     */
    @ExcelProperty(index = 11, value = "主讲老师电话")
    private String mainTeacherPhone;

    /**
     * 学号
     */
    @ExcelProperty(index = 12, value = "学号")
    private String studentNumber;

    /**
     * 考生姓名
     */
    @ExcelProperty(index = 13, value = "考生姓名")
    private String studentName;

    /**
     * 备注
     */
    @ExcelProperty(index = 14, value = "备注")
    private String remark;

}
