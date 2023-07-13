package com.scnujxjy.backendpoint.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AllProjectSummaryInfos {

    public AllProjectSummaryInfos() {
    }

    public AllProjectSummaryInfos(Long id, String fileName, String fileType, String filePath, Long uploader, String uploaderName, Date uploadedAt, Long fileSize, Long projectId, String projectName) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
        this.uploader = uploader;
        this.uploaderName = uploaderName;
        this.uploadedAt = uploadedAt;
        this.fileSize = fileSize;
        this.projectId = projectId;
        this.projectName = projectName;
    }

    private Long id_index;

    private Long id;

    private String fileName;

    private String fileType;

    private String filePath;

    private Long uploader;

    private String uploaderName;

    private Date uploadedAt;

    private Long fileSize;

    private Long projectId;

    private String projectName;
}
