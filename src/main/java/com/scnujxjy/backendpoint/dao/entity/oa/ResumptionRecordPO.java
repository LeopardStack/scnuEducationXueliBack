package com.scnujxjy.backendpoint.dao.entity.oa;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 复学记录表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("resumption_record")
public class ResumptionRecordPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 序号
     */
    private Integer serialNumber;

    /**
     * 年份
     */
    private String currentYear;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 准考证号码
     */
    private String examRegistrationNumber;

    /**
     * 姓名
     */
    private String name;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 旧的班级标识
     */
    private String oldClassIdentifier;

    /**
     * 旧的年级
     */
    private String oldGrade;

    /**
     * 旧的学习形式
     */
    private String oldStudyForm;

    /**
     * 旧的专业名称
     */
    private String oldMajorName;

    /**
     * 新的班级标识
     */
    private String newClassIdentifier;

    /**
     * 新的年级
     */
    private String newGrade;

    /**
     * 新的学习形式
     */
    private String newStudyForm;

    /**
     * 新的专业名称
     */
    private String newMajorName;

    /**
     * 复学时间
     */
    private String resumptionDate;

    /**
     * 休学序号
     */
    private String suspensionSerialNumber;

    /**
     * 申请原因
     */
    private String reason;

    /**
     * 办理日期
     */
    private String handleDate;

    /**
     * 办理人
     */
    private Long approvalUserId;

    /**
     * 审批历史记录表ID
     */
    private Long approvalHistoryId;


}
