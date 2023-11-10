package com.scnujxjy.backendpoint.dao.entity.video_stream.ViewStudentResponse;

import lombok.Data;

@Data
public class ViewFirstStudentResponse {

    private Integer code;//状态码，与 http 状态码相同，用于确定基本的响应状态
    private ViewSecondStudentResponse data;
    private Object error;
    private String requestId;//请求ID，每次请求生成的唯一的 UUID，仅可用于排查、调试，不应该和业务挂上钩
    private String status;//	响应结果，由业务决定，成功返回success，失败返回error

    private Boolean success;//是否成功响应


}
