package com.scnujxjy.backendpoint.dao.entity.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
     * 用户ID 这里存储的 username
     */
    private String userId;

    /**
     * 消息类型（上传，下载，系统消息）
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
     * 是否弹窗(系统消息,Y表示弹窗，N表示不弹窗)
     */
    private String isPopup;

}
