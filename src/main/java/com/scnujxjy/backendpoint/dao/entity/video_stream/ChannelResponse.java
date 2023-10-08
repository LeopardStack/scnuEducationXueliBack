package com.scnujxjy.backendpoint.dao.entity.video_stream;

import lombok.Data;

import java.util.List;

/**
 * 保利威获取频道号下面的角色信息的返回信息
 */
@Data
public class ChannelResponse {

    /** 响应状态码，200为成功返回，非200为失败 */
    private Integer code;

    /** 响应状态文本信息 */
    private String status;

    /** 响应描述信息，当code为400或者500的时候，辅助描述错误原因 */
    private String message;

    /** 请求失败时为空，请求成功时为角色的信息 */
    private List<ChannelData> data;

    // getters and setters...
}
