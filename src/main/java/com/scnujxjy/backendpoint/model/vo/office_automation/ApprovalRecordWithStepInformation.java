package com.scnujxjy.backendpoint.model.vo.office_automation;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.util.Set;

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

    @ApiModelProperty("审批用户id")
    private String username;
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

    @ApiModelProperty("审批username集合")
    private Set<String> approvalUsernameSet;
}
