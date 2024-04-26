package com.scnujxjy.backendpoint.dao.entity.oa;

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
    /**
     * 自增主键ID
     */
    @ApiModelProperty(value = "自增主键ID")
    private Long id;

    /**
     * 系统消息类型1
     */
    @ApiModelProperty(value = "系统消息类型1")
    private String systemMessageType1;

    /**
     * 系统消息类型2
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

    private static final long serialVersionUID = 1L;
}