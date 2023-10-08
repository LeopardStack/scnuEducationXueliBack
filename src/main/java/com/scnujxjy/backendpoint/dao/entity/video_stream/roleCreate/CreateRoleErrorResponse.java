package com.scnujxjy.backendpoint.dao.entity.video_stream.roleCreate;

import lombok.Data;

@Data
public class CreateRoleErrorResponse {

    /**
     * 错误代码，用于确定具体的错误原因.
     */
    private Integer code;

    /**
     * 错误描述，与 error.code 对应.
     */
    private String desc;
}