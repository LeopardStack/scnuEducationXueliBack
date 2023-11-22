package com.scnujxjy.backendpoint.model.vo.office_automation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ApprovalRecordWithStepInformation {
    /**
     * 步骤记录id
     */
    private Long stepRecordId;
    /**
     * 审批id
     */
    private Long approvalId;
    /**
     * 步骤id
     */
    private Long stepId;
    /**
     * 审批用户id
     */
    private String userId;
    /**
     * 最后一次更新时间
     */
    private Date updateAt;
    /**
     * 审批逻辑：0-或，1-与
     */
    private Integer logic;
    /**
     * 审批意见
     */
    private String comment;
    /**
     * 下一步步骤id
     */
    private Long nextStepId;
    /**
     * 附件id
     */
    private Long attachmentId;
    /**
     * 状态：success-成功，failed-失败，waiting-流程中、transfer-流转
     */
    private String status;

    /**
     * 审批类型id
     */
    private Long approvalTypeId;

    /**
     * 步骤顺序
     */
    private Integer stepOrder;

    /**
     * 步骤描述
     */
    private String description;

    /**
     * 用户审批集合
     */
    private List<Long> userApprovalSet;
}
