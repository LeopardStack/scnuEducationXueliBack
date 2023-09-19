package com.scnujxjy.backendpoint.model.vo.video_stream;

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
@Accessors(chain = true)
@Builder
public class VideoStreamRecordVO {

    /**
     * 主键id
     */
    private Long id;

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
     * 频道主持人名称
     */
    private String publisher;

    /**
     * 直播开始时间
     */
    private Date startTime;

    /**
     * 直播结束时间
     */
    private Date endTime;

    /**
     * 直播介绍
     */
    private String desc;

    /**
     * 频道的观看页状态，取值为：live（直播中）、end（直播结束）、playback（回放中）、waiting（等待直播）
     */
    private String watchStatus;

    /**
     * 自频道角色
     */
    private Integer role;

    /**
     * 自频道id集合
     */
    private List<Long> sonId;

    /**
     * 主链接
     */
    private String url;

    /**
     * 观看链接
     */
    private String watchUrl;

    /**
     * 单点登录链接
     */
    private String autoUrl;
}
