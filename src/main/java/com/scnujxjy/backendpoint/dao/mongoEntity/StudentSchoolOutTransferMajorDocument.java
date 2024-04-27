package com.scnujxjy.backendpoint.dao.mongoEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "student_school_out_transfer_major_document")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class StudentSchoolOutTransferMajorDocument {
    @Id
    private String id;

    @ApiModelProperty("姓名")
    private String studentName;

    @ApiModelProperty("学生username")
    private String studentUsername;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("身份证号")
    private String idCardNumber;

    @ApiModelProperty("入学时间")
    private Date ruxueTime;

    @ApiModelProperty("总分")
    private Integer totalScore;

    @ApiModelProperty("考生号")
    private String candidateNumber;

    @ApiModelProperty("转出学校")
    private String fromSchoolName;

    @ApiModelProperty("转入学校")
    private String toSchoolName;

    @ApiModelProperty("转出专业")
    private String fromMajorName;

    @ApiModelProperty("转入专业")
    private String toMajorName;

    @ApiModelProperty("转出层次")
    private String fromLevel;

    @ApiModelProperty("转入层次")
    private String toLevel;

    @ApiModelProperty("转出学习形式")
    private String fromStudyForm;

    @ApiModelProperty("转入学习形式")
    private String toStudyForm;

    @ApiModelProperty("转出教学点")
    private String fromTeachingPoint;

    @ApiModelProperty("转入教学点")
    private String toTeachingPoint;

    @ApiModelProperty(value = "转出上课地点", notes = "使用-分割市区县")
    private String fromClassLocation;

    @ApiModelProperty(value = "转入上课地点", notes = "使用-分割市区县")
    private String toClassLocation;

    @ApiModelProperty("申请理由")
    private String applicationReason;

    @ApiModelProperty("申请人签名")
    private String applicationPersonName;

    @ApiModelProperty("手机")
    private String applicationPhoneNumber;

    @ApiModelProperty("申请时间")
    private Date applicationDate;

    @ApiModelProperty("转出学校意见")
    private String fromSchoolOpinion;

    @ApiModelProperty("转出学校签名")
    private String fromSchoolSignature;

    @ApiModelProperty("转出学校签名时间")
    private Date fromSchoolDate;

    @ApiModelProperty("转入学校意见")
    private String toSchoolOpinion;

    @ApiModelProperty("转入学院签名")
    private String toSchoolSignature;

    @ApiModelProperty("转入学校签名时间")
    private Date toSchoolDate;

    @ApiModelProperty("转出教育厅意见")
    private String fromEducationDepartmentOpinion;

    @ApiModelProperty("转出教育厅签名")
    private String fromEducationDepartmentSignature;

    @ApiModelProperty("转出教育厅签名时间")
    private Date fromEducationDepartmentDate;

    @ApiModelProperty("转入教育厅意见")
    private String toEducationDepartmentOpinion;

    @ApiModelProperty("转入教育厅签名")
    private String toEducationDepartmentSignature;

    @ApiModelProperty("转入教育厅签名时间")
    private Date toEducationDepartmentDate;
}
