package com.scnujxjy.backendpoint.dao.entity.video_stream;


import lombok.Data;

@Data
public class ViewLogThirdResponse {

    private String playId;//表示此次播放动作的ID
    private String userId;//用户ID
    private String channelId;//频道号
    private Integer playDuration;//播放时长，单位：秒
    private Integer stayDuration;//停留时长，单位：秒
    private Long flowSize;//流量大小，单位：bytes
    private String sessionId;//直播场次ID

    private String param1;//使用POLYV观看页的观众ID
    private String param2;//使用POLYV观看页的观众昵称
    private String param3;//观看日志类型，默认为live
    private String param4;//POLYV系统参数
    private String param5;//POLYV系统参数
    private String ipAddress;//IP地址

    private String country;
    private String province;
    private String city;
    private String isp;
    private String referer;
    private String userAgent;
    private String operatingSystem;
    private String browser;
    private String isMobile;
    private String currentDay;
    private Long createdTime;
    private Long lastModified;
    private Integer ptype;
    private Long firstActiveTime;
    private Long lastActiveTime;

}
