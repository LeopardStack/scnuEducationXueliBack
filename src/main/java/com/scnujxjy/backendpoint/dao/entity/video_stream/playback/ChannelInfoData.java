package com.scnujxjy.backendpoint.dao.entity.video_stream.playback;

import lombok.Data;

/**
 * POJO for channel playback settings described in the 'data' section of the response.
 */
@Data
public class ChannelInfoData {

    /** 频道号 */
    private String channelId;

    /** 回放类型: single - 单个视频回放, list - 列表回放 */
    private String type;

    /** 回放的开关: Y - 开启, N - 关闭 */
    private String playbackEnabled;

    /** 回放的视频来源: record - 录制文件, playback - 回放列表, vod - 点播列表 */
    private String origin;

    /** 回放的视频ID */
    private String videoId;

    /** 回放的视频名称 */
    private String videoName;

    /** 回放设置，章节开关: Y - 开启, N - 关闭 */
    private String sectionEnabled;

    /** 是否应用通用设置: Y - 是, N - 否 */
    private String globalSettingEnabled;

    /** 聊天重放: Y - 开启, N - 关闭 */
    private String chatPlaybackEnabled;

    /** 定时回放类型: timedOpen - 定时打开, timedClosed - 定时关闭, period - 时间段内打开, disable - 关闭 */
    private String crontType;

    /** 开放回放的时间, 13位毫秒级时间 */
    private Long startTime;

    /** 关闭回放的时间, 13位毫秒级时间 */
    private Long endTime;
}
