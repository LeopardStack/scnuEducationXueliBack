package com.scnujxjy.backendpoint.dao.entity.oa;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scnujxjy.backendpoint.handler.type_handler.LongTypeHandler;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 事务审批步骤记录表
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "approval_step_record", autoResultMap = true)
public class ApprovalStepRecordPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 事务申请记录 ID
     */
    private Long applicationRecordId;

    /**
     * 步骤 ID
     */
    private Integer stepId;

    /**
     * 用户 ID
     */
    @TableField(typeHandler = LongTypeHandler.class)
    private List<Long> processUserIds;

    /**
     * 审批意见
     */
    private String opinion;

    /**
     * 附件 ID
     */
    private Integer attachmentId;

    /**
     * 下一个步骤 ID
     */
    private Integer nextStepId;

    /**
     * 审批状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
