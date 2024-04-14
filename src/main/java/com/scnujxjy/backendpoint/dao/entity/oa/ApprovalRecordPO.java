package com.scnujxjy.backendpoint.dao.entity.oa;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 事务申请记录表
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
@EqualsAndHashCode(callSuper = false)
@TableName("approval_record")
public class ApprovalRecordPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 事务申请类型 ID
     */
    private Integer applicationTypeId;

    /**
     * 申请人 用户标识  新生是 准考证号 旧生是学号 其他人是 用户名
     */
    private String userIdentify;

    /**
     * 当前审批步骤 id
     */
    private Integer currentStepId;

    /**
     * 当前审批状态
     */
    private String currentStatus;

    /**
     * 申请表单 ID
     */
    private String applicationFormId;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
