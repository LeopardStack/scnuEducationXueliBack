package com.scnujxjy.backendpoint.constant.enums;

import lombok.Getter;

@Getter
public enum MinioBucketEnum {
    DATA_DOWNLOAD_STUDENT_STATUS("dataexport", "学籍数据"),
    DATA_DOWNLOAD_SCORE_INFORMATION("dataexport", "成绩数据"),
    DATA_DOWNLOAD_STUDENT_FEES("dataexport", "缴费数据"),
    DATA_DOWNLOAD_CLASS_INFORMATIONS("dataexport", "班级数据");

    private String bucketName;
    private String subDirectory;

    MinioBucketEnum(String bucketName, String subDirectory) {
        this.bucketName = bucketName;
        this.subDirectory = subDirectory;
    }
}

