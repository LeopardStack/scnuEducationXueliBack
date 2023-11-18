package com.scnujxjy.backendpoint.dao.mongoEntity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "studentTransferApplications")
@Data
public class StudentTransferApplication {
    @Id
    private String id;
    private String name; // 姓名
    private String gender; // 性别
    private String candidateNumber; // 考生号
    private String studentId; // 学号
    private LocalDate admissionDate; // 入学时间
    private double admissionScore; // 入学总分
    private String educationLevel; // 培养层次
    private String admittedMajor; // 录取专业
    private String originalTuitionFee; // 原学费标准
    private String originalStudyForm; // 原专业学习形式
    private String originalClassGrade; // 原年级班别
    private String intendedMajor; // 拟转专业
    private String currentTuitionFee; // 现学费标准
    private String intendedStudyForm; // 拟转专业学习形式
    private String intendedClassGrade; // 拟转年级班别
    private String applicationReason; // 申请理由
    private String applicantSignature; // 申请人签名
    private LocalDate applicationDate; // 申请时间

    // 转出学院意见
    private String transferOutCollegeOpinion;
    private String transferOutApprover;
    private LocalDate transferOutApprovalDate;

    // 转入学院意见
    private String transferInCollegeOpinion;
    private String transferInApprover;
    private LocalDate transferInApprovalDate;

    // 继续教育学院意见
    private String continuingEducationCollegeOpinion;
    private String continuingEducationApprover;
    private LocalDate continuingEducationApprovalDate;

    // 费用结算情况
    private String feeSettlementStatus;

    // Getter和Setter方法
    // ...
}

