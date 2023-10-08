package com.scnujxjy.backendpoint.dao.entity.video_stream;

import lombok.Data;

/**
 * 频道下的角色信息
 */
@Data
public class ChannelData {

    /** 助教/嘉宾账号 */
    private String account;

    /** POLYV用户ID，和保利威官网一致 */
    private String userId;

    /** 频道号 */
    private String channelId;

    /** 角色密码 */
    private String passwd;

    /** 角色名称 */
    private String nickname;

    /** 角色流名，单独使用无效 */
    private String stream;

    /** 角色状态 */
    private String status;

    /** 创建角色时间 */
    private Long createdTime;

    /** 角色最后修改时间 */
    private Long lastModified;

    /** 频道中所有角色序号 */
    private String sort;

    /** 角色头像 */
    private String avatar;

    /** 角色头衔 */
    private String actor;

    /** 是否拥有翻页权限，只能一个角色有，仅支持三分屏场景 */
    private String pageTurnEnabled;

    /** 是否拥有发布公告权限 */
    private String notifyEnabled;

    /** 是否拥有开启签到权限 */
    private String checkinEnabled;

    /** 是否拥有发起投票权限 */
    private String voteEnabled;

    /** 是否拥有抽奖权限 */
    private String lotteryEnabled;

    /** 角色 */
    private String role;

    /** 助教页在线列表显示开关 */
    private String chatListEnabled;

    /** 助教聊天审核 */
    private String chatAuditEnabled;

    /** 助教监播开关 */
    private String monitorEnabled;

    /** 助教轮巡开关 */
    private String roundTourEnabled;

    /** 锁定直播间功能开关 */
    private String watchLockEnabled;

    /** 角色推流地址 */
    private String pushUrl;

}
