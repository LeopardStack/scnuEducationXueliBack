package com.scnujxjy.backendpoint.model.ro.teaching_process;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ScoreInformationFilterRO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 层次
     */
    private String level;

    /**
     * 班级标识
     */
    private String classIdentifier;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学院
     */
    private String college;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 学期
     */
    private String semester;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程代码
     */
    private String courseCode;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 考核类型
     */
    private String assessmentType;

    /**
     * 总评
     */
    private String finalScore;

    /**
     * 第一次补考成绩
     */
    private String makeupExam1Score;

    /**
     * 第二次补考成绩
     */
    private String makeupExam2Score;

    /**
     * 结业后补考成绩
     */
    private String postGraduationScore;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态，比如作弊、缺考等等
     */
    private String status;

    /**
     * 班级名称
     */
    private String className;


}
