package com.scnujxjy.backendpoint.dao.entity.office_automation.approval;

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
@TableName(value = "approval_record", autoResultMap = true)
public class ApprovalRecordPO {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 审批类型id
     */
    private Long approvalTypeId;

    @ApiModelProperty("发起人username")
    private String initiatorUsername;
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

    /**
     * 审核表单 id
     * <p>存储在 MongoDB 中</p>
     */
    private String documentId;

    @ApiModelProperty("查看username集合")
    @TableField(typeHandler = StringSetTypeHandler.class)
    private Set<String> watchUsernameSet;
}
