package com.scnujxjy.backendpoint.model.vo.office_automation;

import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ApprovalStepWithRecordList {
    /**
     * 包含该步骤的记录
     */
    List<ApprovalStepRecordPO> approvalStepRecordList;
    /**
     * 主键id
     */
    private Long id;
    /**
     * 步骤顺序
     */
    private Integer stepOrder;
    /**
     * 审核逻辑：0-与，1-或
     */
    private Integer logic;
    /**
     * 步骤描述
     */
    private String description;
    /**
     * 事件类型id
     */
    private Long approvalTypeId;
}
