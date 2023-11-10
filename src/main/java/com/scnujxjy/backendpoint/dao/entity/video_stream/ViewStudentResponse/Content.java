package com.scnujxjy.backendpoint.dao.entity.video_stream.ViewStudentResponse;

import lombok.Data;

@Data
public class Content {
    private String area;
    private String browser;
    private String ip;
    private String nick;
    private String playDuration;
    private String sessionId;//场次号
    private String startTime;
    private String viewType;
    private String viewerId;
    private String param4;
    private String param5;
}
