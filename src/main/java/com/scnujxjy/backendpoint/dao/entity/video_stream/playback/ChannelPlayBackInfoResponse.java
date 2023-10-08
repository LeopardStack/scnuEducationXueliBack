package com.scnujxjy.backendpoint.dao.entity.video_stream.playback;

import lombok.Data;

/**
 * POJO for the main response from the channel info API.
 */
@Data
public class ChannelPlayBackInfoResponse {

    /** 响应状态码 */
    private Integer code;

    /** 响应状态文本信息 */
    private String status;

    /** 响应描述信息 */
    private String message;

    /** 请求成功返回频道回放设置 */
    private ChannelInfoData data;
}
