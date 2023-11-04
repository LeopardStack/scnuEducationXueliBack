package com.scnujxjy.backendpoint.dao.entity.video_stream;

import lombok.Data;

@Data
public class ViewLogResponse {


    private String userId;
    private String channelId;
    private Integer playDuration;//播放时长，单位：秒
    private Long firstActiveTime;//首次进入直播时间
    private Long lastActiveTime;
    private String param1;//使用POLYV观看页的观众ID
    private String param2;//使用POLYV观看页的观众昵称


}
