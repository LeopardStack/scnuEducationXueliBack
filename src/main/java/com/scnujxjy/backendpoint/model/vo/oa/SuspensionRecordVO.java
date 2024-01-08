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
public class SuspensionRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年份
     */
    private String currentYear;

    /**
     * 序号
     */
    private Integer serialNumber;

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
     * 休学开始时间
     */
    private String suspensionStartDate;

    /**
     * 休学结束时间
     */
    private String suspensionEndDate;

    /**
     * 办理复学手续开始时间
     */
    private String suspensionDealStartDate;

    /**
     * 办理复学手续结束时间
     */
    private String suspensionDealEndDate;

    /**
     * 复学序号
     */
    private String remarkSerialNumber;

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
