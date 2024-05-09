package com.scnujxjy.backendpoint.constant.enums.announceMsg;

import org.apache.xmlbeans.impl.xb.xsdschema.All;

public enum AnnounceType {
    All("all");

    private String Type;

    AnnounceType(String type) {
        Type = type;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
