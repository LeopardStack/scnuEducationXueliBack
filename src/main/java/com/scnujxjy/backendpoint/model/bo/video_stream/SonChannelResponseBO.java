package com.scnujxjy.backendpoint.model.bo.video_stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class SonChannelResponseBO {

    /**
     * 频道号
     */
    private String channelId;

    /**
     * 角色密码
     */
    private String passwd;
}
