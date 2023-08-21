package com.scnujxjy.backendpoint.model.bo.video_stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ChannelResponseBO {


    /**
     * 频道id
     */
    private String channelId;

    /**
     * 频道名称
     */
    private String name;

    /**
     * 频道密码
     */
    private String channelPasswd;

    /**
     * 主持人名称
     */
    private String publisher;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 直播介绍
     */
    private String desc;

    /**
     * 咨询提问开关
     */
    private String consultingMenuEnabled;

    /**
     * 频道的观看页状态：取值为：live（直播中）、end（直播结束）、playback（回放中）、waiting（等待直播）
     */
    private String watchStatus;

    /**
     * 子频道创建响应信息
     */
    private List<SonChannelResponseBO> sonChannelResponseBOS;

}
