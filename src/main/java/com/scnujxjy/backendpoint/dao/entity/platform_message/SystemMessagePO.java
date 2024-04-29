package com.scnujxjy.backendpoint.dao.entity.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统消息表
 */
@ApiModel(description = "系统消息表")
@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("system_message")
public class SystemMessagePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键ID
     */
    @ApiModelProperty(value = "自增主键ID")
    @TableId(type = IdType.AUTO)
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

    @ApiModelProperty("发送者username")
    private String senderUsername;

}