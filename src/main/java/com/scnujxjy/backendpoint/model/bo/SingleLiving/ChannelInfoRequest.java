package com.scnujxjy.backendpoint.model.bo.SingleLiving;

import lombok.Data;

@Data
public class ChannelInfoRequest {
    private String channelId;
    //"Y"是，"N"否
    private String playbackEnabled;
    private String channelName;
    private String ImgUrl;
}
