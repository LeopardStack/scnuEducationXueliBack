package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum MessageEnum {
    DOWNLOAD_MSG("下载消息"),
    ANNOUNCEMENT_MSG("公告消息");


    private String message_name;

    MessageEnum(String message_name) {
        this.message_name = message_name;
    }
}
