package com.scnujxjy.backendpoint.model.vo.office_automation;

import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 审批类型所有信息；
 * <p>展示类型及其说对应的所有步骤</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ApprovalTypeAllInformation {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 类型名称
     */
    private String name;
    /**
     * 审批类型描述
     */
    private String description;
    /**
     * 步骤列表
     */
    private List<ApprovalStepPO> approvalStepList;

}
