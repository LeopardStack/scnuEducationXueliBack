package com.scnujxjy.backendpoint.dao.mongoEntity.oa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Document(collection = "suspension_of_study_document")
public class SuspensionOfStudyDocument {

    @Id
    private String id;

    // 姓名
    private String name;

    // 学生的用户名（系统登录名）
    private String studentUsername;

    // 专业
    private String major;

    // 层次
    private String level;

    // 学号
    private String studentNumber;

    // 办学形式
    private String educationMode;

    // 院系ID
    private String collegeId;

    // 院系名称
    private String collegeName;

    // 学制
    private String educationalSystem;

    // 年级
    private String grade;

    // 年级标识符
    private String gradeIdentifier;

    // 班级标识符
    private String classIdentifier;

    // 班别
    private String classType;

    // 申请理由
    private String reasonForApplication;

    // 休学开始时间
    private Date suspensionStartTime;

    // 休学结束时间
    private Date suspensionEndTime;

    // 教学学院经办人
    private String academicCollegeOperator;

    // 缴费情况
    private String paymentStatus;

    // 教学学院批复时间
    private Date academicCollegeApprovalTime;

    // 继续教育学院经办人
    private String continuingEducationCollegeOperator;

    private List<Long> attachmentIds; // 申请人上传的附件

    // 继续教育学院批复时间
    private Date continuingEducationCollegeApprovalTime;

}