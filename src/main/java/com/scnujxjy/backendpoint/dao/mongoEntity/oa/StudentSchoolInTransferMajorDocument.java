package com.scnujxjy.backendpoint.dao.mongoEntity.oa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Document(collection = "student_school_in_transfer_major_document")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class StudentSchoolInTransferMajorDocument {
    @Id
    private String id;

    /**
     * 审批类型id，用来区分新生省内、新生省外、新生校内、
     */
    private Integer transferType;

    /**
     * 学生在系统中的 user_id
     */
    private String studentUsername;
    private String name; // 姓名
    private String gender; // 性别
    private String candidateNumber; // 考生号
    private String studentId; // 学号
    private Date admissionDate; // 入学时间
    private Integer admissionScore; // 入学总分
    private String educationLevel; // 培养层次
    private String admittedMajor; // 录取专业
    private BigDecimal originalTuitionFee; // 原学费标准
    private String originalStudyForm; // 原专业学习形式
    private String originalClassName; // 原年级班级名称
    private String originalClassIdentifier; // 原年级班级标识符
    private String intendedMajor; // 拟转专业
    private BigDecimal currentTuitionFee; // 现学费标准
    private String intendedStudyForm; // 拟转专业学习形式
    private String intendedClassName; // 拟转年级班级名称
    private String intendedClassIdentifier; // 拟转年级班级标识符
    private String applicationReason; // 申请理由
    private List<Long> attachmentIds; // 申请人上传的附件
    private Date applicationDate; // 申请时间

    // 转入学院意见
    private String toCollegeOpinion;
    private String toCollegeReviewer;
    private Date toCollegeDate;
    // 转出学院意见
    private String fromCollegeOpinion;
    private String fromCollegeReviewer;
    private Date fromCollegeApprovalDate;

    /**
     * 转入学院 id
     */
    private String toCollegeId;

    /**
     * 转出学院id
     */
    private String fromCollegeId;

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
    private String continuingEducationApprover;
    private Date continuingEducationApprovalDate;

    // 费用结算情况
    private String feeSettlementStatus;
}

