package com.scnujxjy.backendpoint.dao.entity.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 平台消息表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-09-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("platform_message")
public class PlatformMessagePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 相应的消息表中的ID
     */
    private Long relatedMessageId;

    /**
     * 消息生成时间
     */
    private Date createdAt;

    /**
     * 是否已读
     */
    private Boolean isRead;


    /**
     * 是否弹框 是否弹框 Y 表示弹框 N 或者 空 表示不弹框
     */
    private String isPopup;


}
