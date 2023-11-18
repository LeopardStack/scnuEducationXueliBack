package com.scnujxjy.backendpoint.model.vo.office_automation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class ApprovalRecordAllInformation {

    /**
     * 该事件下的步骤以及对应记录
     */
    List<ApprovalStepWithRecordList> stepWithRecordLists;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 审批类型id
     */
    private Long approvalTypeId;
    /**
     * 发起人id
     */
    private String initiatorUserId;
    /**
     * 审批发起时间
     */
    private Date createdAt;
    /**
     * 最后一次更新时间
     */
    private Date updateAt;
    /**
     * 当前步骤id
     */
    private Long currentStepId;
    /**
     * 审批状态：waiting-正在审批，success-成功，failed-失败
     */
    private String status;
}
