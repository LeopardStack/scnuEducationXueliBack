package com.scnujxjy.backendpoint.constant.enums;

public enum FileOperationResponse {
    QUERY_ROOT_DIRECTORY_SUCCESS("查询根目录信息操作成功"),
    QUERY_SPECIFIC_DIRECTORY_SUCCESS("查询指定目录信息操作成功"),
    CHECK_FILE_EXISTENCE_SUCCESS("查询指定文件是否存在成功"),
    UPLOAD_SPECIFIC_FILE_SUCCESS("上传指定文件操作成功"),
    FILE_NOT_FOUND("文件未找到"),
    DIRECTORY_NOT_FOUND("目录未找到"),
    ACCESS_DENIED("访问被拒绝"),
    OPERATION_FAILED("操作失败"),
    INVALID_OPERATION("无效的操作");

    private final String message;

    FileOperationResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

