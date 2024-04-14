package com.scnujxjy.backendpoint.dao.mongoEntity;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
@EqualsAndHashCode(callSuper = true)
@Document(collection = "studentTransferApplications")
public class StudentTransferApplication extends OAApplicationForm{
    @Id
    private String id;
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

    // 转出学院意见
    private String transferOutCollegeOpinion;
    private String transferOutApprover;
    private Date transferOutApprovalDate;

    // 转入学院意见
    private String transferInCollegeOpinion;
    private String transferInApprover;
    private Date transferInApprovalDate;

    // 继续教育学院意见
    private String continuingEducationCollegeOpinion;
    private String continuingEducationApprover;
    private Date continuingEducationApprovalDate;

    // 费用结算情况
    private String feeSettlementStatus;
}

