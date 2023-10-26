package com.scnujxjy.backendpoint.model.bo.SingleLiving;

import lombok.Data;

import java.util.Date;

/**
 * 创建频道的请求体
 */
@Data
public class ChannelCreateRequestBO {
    /**
     * 直播间标题
     */
    private String livingRoomTitle;
    /**
     * 开始时间
     */
    private Date startDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 是否回放
     */
    private Boolean playRollback;
    /**
     * 使用开启无延迟 Y 开启 N 不开启
     */
    private String pureRtcEnabled;
}
