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
 * 事务申请类型表
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
@TableName("approval_type")
public class ApprovalTypePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Integer id;

    /**
     * 事务申请类型名称
     */
    private String applicationName;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
