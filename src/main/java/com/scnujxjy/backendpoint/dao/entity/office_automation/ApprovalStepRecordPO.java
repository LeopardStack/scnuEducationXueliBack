package com.scnujxjy.backendpoint.dao.entity.office_automation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.scnujxjy.backendpoint.handler.type_handler.set.StringSetTypeHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName(value = "approval_step_record", autoResultMap = true)
public class ApprovalStepRecordPO {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 审批id
     */
    private Long approvalId;
    /**
     * 步骤id
     */
    private Long stepId;

    @ApiModelProperty("审批用户username")
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
     * 用户审批集合
     */
    @TableField(typeHandler = StringSetTypeHandler.class)
    private Set<String> approvalUsernameSet;
}
