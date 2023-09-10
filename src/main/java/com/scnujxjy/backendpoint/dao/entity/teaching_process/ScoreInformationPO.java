package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("score_information")
public class ScoreInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty(value = "自增主键", index = 0)
    private Long id;

    @ExcelProperty(value = "学号", index = 1)
    private String studentId;

    @ExcelProperty(value = "班级标号", index = 2)
    private String classIdentifier;

    @ExcelProperty(value = "年级", index = 3)
    private String grade;

    @ExcelProperty(value = "学院", index = 4)
    private String college;

    @ExcelProperty(value = "专业名称", index = 5)
    private String majorName;

    @ExcelProperty(value = "学期", index = 6)
    private String semester;

    @ExcelProperty(value = "课程名称", index = 7)
    private String courseName;

    @ExcelProperty(value = "课程编号", index = 8)
    private String courseCode;

    @ExcelProperty(value = "课程类型", index = 9)
    private String courseType;

    @ExcelProperty(value = "考核类型", index = 10)
    private String assessmentType;

    @ExcelProperty(value = "总评成绩", index = 11)
    private Double finalScore;

    @ExcelProperty(value = "补考1成绩", index = 12)
    private Double makeupExam1Score;

    @ExcelProperty(value = "补考2成绩", index = 13)
    private Double makeupExam2Score;

    @ExcelProperty(value = "结业后补考成绩", index = 14)
    private Double postGraduationScore;

    @ExcelProperty(value = "备注信息", index = 15)
    private String remarks;
}
