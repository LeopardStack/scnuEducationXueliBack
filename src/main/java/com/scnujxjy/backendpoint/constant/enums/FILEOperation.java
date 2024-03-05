package com.scnujxjy.backendpoint.constant.enums;

public enum FILEOperation {
    QUERY_ROOT_DIRECTORY("查询根目录信息"),
    QUERY_SPECIFIC_DIRECTORY("查询指定目录信息"),
    DELETE_SPECIFIC_DIRECTORY("删除指定目录"),
    CHECK_FILE_EXISTENCE("查询指定文件是否存在"),
    DELETE_SPECIFIC_FILE("删除指定文件"),
    UPLOAD_SPECIFIC_FILE("上传指定文件");

    private final String description;

    FILEOperation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

