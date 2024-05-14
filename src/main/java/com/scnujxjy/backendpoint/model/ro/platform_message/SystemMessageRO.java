package com.scnujxjy.backendpoint.model.ro.platform_message;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

/**
 * @author lth
 * @version 1.0
 * @description TODO
 * @date 2024/4/26 15:15
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemMessageRO {

    @ApiModelProperty("发送者username")
    private String senderUsername;

    @ApiModelProperty("接收者username")
    private String receiverUsername;

    /**
     * 自增主键ID
     */
    @ApiModelProperty(value = "自增主键id")
    private Long id;

    /**
     * 系统消息类型1，例如：事务消息，系统消息
     */
    @ApiModelProperty(value = "系统消息类型1")
    private String systemMessageType1;

    /**
     * 系统消息类型2，例如：开班申请，校内转专业申请
     */
    @ApiModelProperty(value = "系统消息类型2")
    private String systemMessageType2;

    /**
     * 消息状态
     */
    @ApiModelProperty(value = "消息状态")
    private String messageStatus;

    /**
     * 关联系统ID
     */
    @ApiModelProperty(value = "关联系统ID")
    private Long systemRelatedId;

    /**
     * 创建时间，自动生成
     */
    @ApiModelProperty(value = "创建时间，自动生成")
    private Date createdAt;

    /**
     * 更新时间，自动更新
     */
    @ApiModelProperty(value = "更新时间，自动更新")
    private Date updatedAt;

    @ApiModelProperty("接受者username集合")
    private Set<String> receiverUsernameSet;

    @ApiModelProperty("是否已读")
    private Boolean isRead;
}

