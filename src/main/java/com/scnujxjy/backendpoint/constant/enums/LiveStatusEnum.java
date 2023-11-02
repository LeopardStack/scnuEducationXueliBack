package com.scnujxjy.backendpoint.constant.enums;

import lombok.Data;

/**
 * unStart：未开始
 * live：直播中
 * end：已结束
 * waiting：等待中
 * playback：回放中
 * banpush：已禁播
 */
public enum LiveStatusEnum {
    UN_START("未开始"),
    UN_START0("未安排"),
    LIVE("直播中"),
    END("已结束"),
    WAITING("等待中"),
    PLAYBACK("回放中"),
    OVER("已彻底完成"), // 标志该直播间不再使用
    BANPUSH("已禁播");


    public String status;

    LiveStatusEnum(String status) {
        this.status = status;
    }

    public static String get(String msg) {
        switch (msg.toLowerCase()) {  // 将输入的字符串转换为小写，以实现不区分大小写的比较
            case "unstart":
                return UN_START.status;
            case "live":
                return LIVE.status;
            case "end":
                return END.status;
            case "waiting":
                return WAITING.status;
            case "playback":
                return PLAYBACK.status;
            case "banpush":
                return BANPUSH.status;
            default:
                throw new IllegalArgumentException("Unknown live status: " + msg);
        }
    }

}
