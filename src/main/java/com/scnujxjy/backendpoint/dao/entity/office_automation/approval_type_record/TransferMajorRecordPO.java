package com.scnujxjy.backendpoint.dao.entity.office_automation.approval_type_record;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liweitang
 * @description transfer_major_record
 * @date 2023-11-25
 */
@Data
@TableName("transfer_major_record")
public class TransferMajorRecordPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前年份
     */
    @TableId(type = IdType.AUTO)
    private Integer nowYear;

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
     * 旧的班级标识
     */
    private String oldClassIdentifier;

    /**
     * 新的班级标识
     */
    private String newClassIdentifier;

    /**
     * 旧的年级
     */
    private String oldGrade;

    /**
     * 新的年级
     */
    private String newGrade;

    /**
     * 旧的学习形式
     */
    private String oldStudyForm;

    /**
     * 新的学习形式
     */
    private String newStudyForm;

    /**
     * 审批记录表 id
     */
    private Long approvalRecordId;

}