package com.scnujxjy.backendpoint.dao.entity.video_stream.roleCreate;

import lombok.Data;

@Data
public class PurviewList {

    /**
     * 权限编码.
     * chatListEnabled: 在线列表（仅支持助教）
     * pageTurnEnabled: 翻页（仅支持助教，且仅能设置一个助教有翻页权限）
     * monitorEnabled: 监播（仅支持助教，且仅能设置一个助教有监播权限）
     * chatAuditEnabled: 聊天审核（仅支持助教）
     */
    private String code;

    /**
     * 权限开关.
     * Y: 开启
     * N: 关闭
     */
    private String enabled;
}
