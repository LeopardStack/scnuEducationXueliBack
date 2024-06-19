package com.scnujxjy.backendpoint.model.ro.video_stream;

import lombok.Data;

import java.util.Date;

@Data
public class VideoInformationResponse {

    private Long id;

    private String channelId;

    private Long sectionId;

    private String sessionId;

    private String url;

    private String cdnUrl;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String sectionName;

}
