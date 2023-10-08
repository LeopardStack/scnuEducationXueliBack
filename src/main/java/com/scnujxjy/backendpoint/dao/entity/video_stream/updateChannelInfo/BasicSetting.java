package com.scnujxjy.backendpoint.dao.entity.video_stream.updateChannelInfo;

import lombok.Data;

/**
 * 基础设置实体类
 */
@Data
public class BasicSetting {
    // 频道名称
    private String name;
    // 频道密码，长度不能超过16位
    private String channelPasswd;
    // 主持人名称
    private String publisher;
    // 直播开始时间，13位时间戳，设置为0表示关闭直播开始时间显示
    private Long startTime;
    // 直播结束时间，13位时间戳
    private Long endTime;
    // 累积观看数
    private Integer pageView;
    // 点赞数
    private Integer likes;
    // 频道图标地址
    private String coverImg;
    // 引导图地址
    private String splashImg;
    // 引导页开关（Y：开启，N：关闭）
    private String splashEnabled;
    // 直播介绍
    private String desc;
    // 咨询提问开关（Y：开启，N：关闭）
    private String consultingMenuEnabled;
    // 是否限制最大观看人数（Y：是，N：否）
    private String maxViewerRestrict;
    // 最大在线人数
    private Integer maxViewer;
    // 频道的所属分类ID
    private String categoryId;
    // 连麦人数
    private Integer linkMicLimit;
    // 是否增加转播关联
    private String operation;
    // 接收转播频道号，多个频道号用半角逗号","隔开
    private String receiveChannelIds;
    // 是否关闭弹幕功能的开关（N：表示不关闭，Y：表示关闭）
    private String closeDanmu;
    // 默认是否显示弹幕信息开关（Y：表示显示，N：表示不显示）
    private String showDanmuInfoEnabled;
    // 是否开启无延时直播开关（Y：表示是，N：表示否）
    private String pureRtcEnabled;
    // 自定义讲师ID，32个以内ASCII码可见字符
    private String customTeacherId;
}