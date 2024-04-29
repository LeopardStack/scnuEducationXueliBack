package com.scnujxjy.backendpoint.model.vo.platform_message;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统消息视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增主键ID")
    private Long id;

    @ApiModelProperty(value = "系统消息类型1")
    private String systemMessageType1;


    @ApiModelProperty(value = "系统消息类型2")
    private String systemMessageType2;


    @ApiModelProperty(value = "消息状态")
    private String messageStatus;


    @ApiModelProperty(value = "关联系统id")
    private Long systemRelatedId;


    @ApiModelProperty(value = "创建时间，自动生成")
    private Date createdAt;


    @ApiModelProperty(value = "更新时间，自动更新")
    private Date updatedAt;


}
