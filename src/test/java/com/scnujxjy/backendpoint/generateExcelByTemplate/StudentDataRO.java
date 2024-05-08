package com.scnujxjy.backendpoint.generateExcelByTemplate;

import lombok.Data;

@Data
public class StudentDataRO {
    private String majorCode;
    private String name;
    private String phoneNumber;
    private String gender;
    private String candidateNumber;
    private String totalScore;
    private String level;
    private String college;
    private String majorAdmitted;
    private String tuitionStandard; // 学费标准
    private String studyForm;
    private String teachingPointName;
    private String importClassName;
}
