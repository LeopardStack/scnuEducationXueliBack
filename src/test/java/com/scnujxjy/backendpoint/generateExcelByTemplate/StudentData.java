package com.scnujxjy.backendpoint.generateExcelByTemplate;

import lombok.Data;

@Data
public class StudentData {
    private String name;             // 姓名
    private String gender;           // 性别
    private String candidateNumber;  // 考生号
    private String totalScore;      // 照顾总分
    private String level;            // 层次
    private String majorAdmitted;    // 录取专业
    private String tuitionStandard; // 学费标准
    private String studyForm;        // 学习形式
    private String studyDuration;
    private String grade;
    private String oldClassName; // 原班级

    private String newClassName; // 新班级
    private String applyReason; // 申请理由


}
