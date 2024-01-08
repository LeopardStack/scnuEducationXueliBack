package com.scnujxjy.backendpoint.model.vo.oa;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = false)
@Builder
public class RetentionRecordVO implements Serializable {

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
     * 身份证号码
     */
    private String idNumber;

    /**
     * 准考证号码
     */
    private String examRegistrationNumber;

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
     * 留级开始时间
     */
    private String retentionStartDate;

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
