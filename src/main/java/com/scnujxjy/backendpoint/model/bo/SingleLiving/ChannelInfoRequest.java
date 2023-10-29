package com.scnujxjy.backendpoint.model.bo.SingleLiving;

import lombok.Data;

@Data
public class ChannelInfoRequest {
    private String channelId;
    private String playbackEnabled;
    private String channelName;
    private String ImgUrl;
}
