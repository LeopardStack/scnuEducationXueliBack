package com.scnujxjy.backendpoint.constant.enums;

public enum CourseContentType {
    LIVING("直播"),
    VIDEO("点播"),
    OFF_LINE("线下"),
    NODE("父节点"),
    MIX("混合");

    private final String contentType;

    CourseContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
