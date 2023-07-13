package com.scnujxjy.backendpoint.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AllProjectSummaryFiles {
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
