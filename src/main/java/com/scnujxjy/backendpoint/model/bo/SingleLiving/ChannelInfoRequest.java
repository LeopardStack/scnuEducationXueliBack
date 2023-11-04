package com.scnujxjy.backendpoint.model.bo.SingleLiving;

import lombok.Data;

@Data
public class ChannelInfoRequest {
    private String channelId;
    //"Y"是，"N"否
    private String playbackEnabled;
    private String channelName;
    private String ImgUrl;

    private String currentDay;//格式 "2023-11-04"
    private String startTime;//"2023-11-04 23:00"
    private String endTime;//"2023-11-04 23:00"
}
