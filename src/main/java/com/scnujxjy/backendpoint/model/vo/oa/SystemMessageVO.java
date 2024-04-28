package com.scnujxjy.backendpoint.model.vo.oa;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;

/**
 * 系统消息视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessageVO {
    /**
     * 系统消息类型1
     */
    private String systemMessageType1;

    /**
     * 系统消息类型2
     */
    private String systemMessageType2;

    /**
     * 消息状态
     */
    private String messageStatus;

    /**
     * 关联系统ID
     */
    private Long systemRelatedId;

    /**
     * 创建时间，格式化为字符串
     */
    private String formattedCreatedAt;

    /**
     * 更新时间，格式化为字符串
     */
    private String formattedUpdatedAt;

    /**
     * 扩展字段，例如关联的用户名或其他关联信息
     */
    private String relatedUserName;
}
