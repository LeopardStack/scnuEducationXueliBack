package com.scnujxjy.backendpoint.model.ro.oa;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    /**
     * 发起用户审批的人
     */
    private Long userId;

    /**
     * 发起用户审批的人用户名
     */
    private String username;

    /**
     * 系统消息类型1
     */
    private String systemMessageType1;

    /**
     * 系统消息类型2
     */
    private String systemMessageType2;

    /**
     * 关联系统ID
     */
    private String systemRelatedId;

    /**
     * 消息状态
     */
    private String messageStatus;

    /**
     * 发起用户审批能够看到的用户的id集合
     */
    private List<Long> userIds;
}
