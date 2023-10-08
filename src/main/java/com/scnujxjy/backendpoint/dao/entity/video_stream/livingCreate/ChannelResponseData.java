package com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate;

import lombok.Data;

@Data
public class ChannelResponseData {
    /**
     * 频道ID
     */
    private Integer channelId;
    /**
     * POLYV用户ID，和保利威官网一致，获取路径：官网->登录->直播（开发设置）
     */
    private String userId;
    /**
     * 讲师登录密码，直播场景不是研讨会时不为null，长度6-16位
     */
    private String channelPasswd;
    /**
     * 研讨会主持人密码，仅直播场景是研讨会时不为null，长度6-16位
     */
    private String seminarHostPassword;
    /**
     * 研讨会参会人密码，仅直播场景是研讨会时不为null，长度6-16位
     */
    private String seminarAttendeePassword;
}
