package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class ScoreInformationDownloadVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0, value = "序号")
    private long index;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 学号
     */
    @ExcelProperty(index = 1, value = "学号")
    private String studentId;

    /**
     * 姓名
     */
    @ExcelProperty(index = 2, value = "姓名")
    private String name;

    private String classIdentifier;

    /**
     * 年级
     */
    @ExcelProperty(index = 3, value = "年级")
    private String grade;

    /**
     * 学院
     */
    @ExcelProperty(index = 4, value = "学院")
    private String college;

    /**
     * 班级名称
     */
    @ExcelProperty(index = 5, value = "班级名称")
    private String className;

    /**
     * 学习形式
     */
    @ExcelProperty(index = 6, value = "学习形式")
    private String studyForm;

    /**
     * 层次
     */
    @ExcelProperty(index = 7, value = "层次")
    private String level;


    /**
     * 专业名称
     */
    @ExcelProperty(index = 8, value = "专业名称")
    private String majorName;

    /**
     * 学期
     */
    @ExcelProperty(index = 9, value = "学期")
    private String semester;

    /**
     * 课程名称
     */
    @ExcelProperty(index = 10, value = "课程名称")
    private String courseName;

    private String courseCode;

    /**
     * 课程类型
     */
    @ExcelProperty(index = 11, value = "课程类型")
    private String courseType;

    /**
     * 考核类型
     */
    @ExcelProperty(index = 12, value = "考核类型")
    private String assessmentType;

    /**
     * 总评
     */
    @ExcelProperty(index = 13, value = "总评")
    private String finalScore;

    /**
     * 第一次补考成绩
     */
    @ExcelProperty(index = 14, value = "第一次补考成绩")
    private String makeupExam1Score;

    /**
     * 第二次补考成绩
     */
    @ExcelProperty(index = 15, value = "第二次补考成绩")
    private String makeupExam2Score;

    /**
     * 结业后补考成绩
     */
    @ExcelProperty(index = 16, value = "结业后补考成绩")
    private String postGraduationScore;

    /**
     * 备注
     */
    @ExcelProperty(index = 17, value = "备注")
    private String remarks;

    /**
     * 状态
     */
    @ExcelProperty(index = 18, value = "状态")
    private String status;

}
