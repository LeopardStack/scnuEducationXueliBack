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
public class UserUploadsRO {

    /**
     * 消息类型
     */
    private String msgType;
}
