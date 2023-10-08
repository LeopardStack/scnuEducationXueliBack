package com.scnujxjy.backendpoint.dao.entity.video_stream.roleCreate;

import lombok.Data;

@Data
public class CreateRoleDataResponse {
    private String account;
    private String userId;
    private Integer channelId;
    private String passwd;
    private String nickname;
    private String stream;
    /**
     * 角色状态.
     * Y: 开启
     * N: 关闭
     */
    private String status;
    private Long createdTime;
    private Long lastModified;
    private Integer sort;
    private String avatar;
    private String actor;

    /**
     * 角色.
     * Assistant: 助教
     * Guest: 嘉宾
     */
    private String role;

    /**
     * 监播权限.
     * Y: 开启
     * N: 关闭
     */
    private String monitorEnabled;

    /**
     * 翻页权限.
     * Y: 开启
     * N: 关闭
     */
    private String pageTurnEnabled;

    /**
     * 在线列表权限.
     * Y: 开启
     * N: 关闭
     */
    private String chatListEnabled;

    /**
     * 聊天审核权限.
     * Y: 开启
     * N: 关闭
     */
    private String chatAuditEnabled;
}
