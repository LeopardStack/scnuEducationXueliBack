package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum PolyvEnum {
    WATCH_URL("https://live.polyv.cn/watch/");


    private String key;

    PolyvEnum(String key) {
        this.key = key;
    }
}