package com.scnujxjy.backendpoint.dao.entity.office_automation;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("approval_step")
public class ApprovalStepPO {
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
