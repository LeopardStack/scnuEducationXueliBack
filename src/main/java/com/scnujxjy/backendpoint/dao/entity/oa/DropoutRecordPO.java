package com.scnujxjy.backendpoint.dao.entity.oa;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 退学记录表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dropout_record")
public class DropoutRecordPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 序号
     */
    private String serialNumber;

    /**
     * 准考证号码
     */
    private String examRegistrationNumber;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 姓名
     */
    private String name;

    /**
     * 旧的学习形式
     */
    private String oldStudyForm;

    /**
     * 旧的年级
     */
    private String oldGrade;

    /**
     * 旧的专业名称
     */
    private String oldMajorName;

    /**
     * 旧的班级标识
     */
    private String oldClassIdentifier;

    /**
     * 办理人
     */
    private Long approvalUserId;

    /**
     * 申请原因
     */
    private String reason;

    /**
     * 办理日期
     */
    private String approvalDate;

    /**
     * 审批历史记录表ID
     */
    private Long approvalHistoryId;


}
