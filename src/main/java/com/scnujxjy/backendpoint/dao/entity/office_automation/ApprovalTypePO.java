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
@TableName("approval_type")
public class ApprovalTypePO {
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
}
