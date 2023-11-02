package com.scnujxjy.backendpoint.model.ro.platform_message;

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
public class UserUploadsFilesGetRO {
    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 上传文件 url
     */
    private String uploadFileUrl;

    /**
     * 处理结果文件 url
     */
    private String uploadResultFileUrl;
}
