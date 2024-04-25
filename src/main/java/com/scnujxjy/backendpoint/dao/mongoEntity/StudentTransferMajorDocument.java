package com.scnujxjy.backendpoint.dao.mongoEntity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "student_transfer_major_document")
@Data
@Builder
public class StudentTransferMajorDocument {
    @Id
    private String id;

    /**
     * 审批类型id，用来区分新生省内、新生省外、新生校内、
     */
    private Integer transferType;

    /**
     * 学生在系统中的 user_id
     */
    private Long studentUserId;
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
    private String transferOutApproval;
    private LocalDate transferOutApprovalDate;
    /**
     * 转出学院 id
     */
    private String fromCollegeId;

    private String fromCollegeName;

    // 转入学院意见
    private String transferInCollegeOpinion;
    private String transferInApproval;
    private LocalDate transferInApprovalDate;

    /**
     * 转入学院 id
     */
    private String toCollegeId;

    /**
     * 转入学院名称
     */
    private String toCollegeName;

    // 继续教育学院意见
    /**
     * 继续教育学院 id
     */
    private String continuingEducationCollegeId;
    private String continuingEducationCollegeOpinion;
    private String continuingEducationApproval;
    private LocalDate continuingEducationApprovalDate;

    // 费用结算情况
    private String feeSettlementStatus;

    // Getter和Setter方法
    // ...
}

