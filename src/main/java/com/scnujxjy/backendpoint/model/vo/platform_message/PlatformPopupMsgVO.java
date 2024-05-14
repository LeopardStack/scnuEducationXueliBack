package com.scnujxjy.backendpoint.model.vo.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 这个类 主要为了存储 每个用户的弹框 未读系统消息
 * 目前主要是公告
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PlatformPopupMsgVO {
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

    /**
     * 消息主体
     */
    private Object msgBody;
}
