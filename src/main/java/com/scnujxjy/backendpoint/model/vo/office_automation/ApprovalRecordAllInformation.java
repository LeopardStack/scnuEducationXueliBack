package com.scnujxjy.backendpoint.model.vo.office_automation;

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
public class ApprovalRecordAllInformation {
    @ApiModelProperty("步骤以及对应记录")
    List<ApprovalStepWithRecord> approvalStepWithRecordList;
    @ApiModelProperty("主键id")
    private Long id;
    @ApiModelProperty("发起人username")
    private String initiatorUsername;
    @ApiModelProperty("审批类型id")
    private Long approvalTypeId;
    @ApiModelProperty("审批发起时间")
    private Date createdAt;
    @ApiModelProperty("最后一次更新时间")
    private Date updateAt;
    @ApiModelProperty("当前步骤id")
    private Long currentStepId;
    @ApiModelProperty("审批状态：waiting-正在审批，success-成功，failed-失败")
    private String status;

    @ApiModelProperty("查看username集合")
    private Set<String> watchUsernameSet;
    @ApiModelProperty("审核表单 id")
    private String documentId;
}
