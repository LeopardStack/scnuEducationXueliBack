package com.scnujxjy.backendpoint.model.ro.platform_message;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AnnouncementMsgUsersRO {
    /**
     * 公告消息的主键 ID
     */
    @ApiModelProperty(value = "公告消息的主键 ID")
    private Long announcementMsgId;

    /**
     * 用户类型
     */
    @ApiModelProperty(value = "用户类型")
    private String userType;
}
