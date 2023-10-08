package com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo;

import com.scnujxjy.backendpoint.dao.entity.video_stream.updateChannelInfo.AuthSetting;
import lombok.Data;

import java.util.List;

@Data
public class ChannelDetail {
    private String channelId;
    private String name;
    private String scene;
    private String newScene;
    private String template;
    private String channelPasswd;
    private String publisher;
    private Long startTime;
    private Long endTime;
    private String pureRtcEnabled;
    private Integer pageView;
    private Integer likes;
    private String coverImg;
    private String splashImg;
    private String splashEnabled;
    private String bgImg;
    private String desc;
    private String consultingMenuEnabled;
    private String maxViewerRestrict;
    private Integer maxViewer;
    private String watchStatus;
    private String watchStatusText;
    private UserCategory userCategory;
    private List<AuthSetting> authSettings;
    private Integer linkMicLimit;
    private String createdAccountId;
    private String createdAccountEmail;
    private Long createdTime;
}
