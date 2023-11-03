package com.scnujxjy.backendpoint.model.ro.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 设置直播间的参数 比如回放设置
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ChannelSetRO {
    /**
     * 频道 ID
     */
    String channelId;

    /**
     * Y 为开启
     * N 为关闭
     */
    String playBack;
}
