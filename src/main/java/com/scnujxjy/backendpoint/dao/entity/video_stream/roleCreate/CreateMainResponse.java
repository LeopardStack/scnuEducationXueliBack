package com.scnujxjy.backendpoint.dao.entity.video_stream.roleCreate;
import lombok.Data;

@Data
public class CreateMainResponse {
    private Integer code;
    private String status;
    private Boolean success;
    private String requestId;
    private CreateRoleErrorResponse error;
    private CreateRoleDataResponse data;
}
