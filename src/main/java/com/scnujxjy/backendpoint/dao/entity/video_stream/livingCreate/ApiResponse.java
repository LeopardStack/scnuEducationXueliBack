package com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate;

import lombok.Data;

@Data
public class ApiResponse {
    /**
     * 状态码，与 http 状态码相同，用于确定基本的响应状态
     */
    private Integer code;
    /**
     * 响应结果，由业务决定，成功返回success，失败返回error
     */
    private String status;
    /**
     * 是否成功响应
     */
    private Boolean success;
    /**
     * 请求ID，每次请求生成的唯一的 UUID，仅可用于排查、调试，不应该和业务挂上钩
     */
    private String requestId;
    /**
     * 频道响应对象
     */
    private ChannelResponseData data;
    /**
     * 错误信息
     */
    private Object error; //如果有更详细的错误描述，可以进一步为它定义一个类
}
