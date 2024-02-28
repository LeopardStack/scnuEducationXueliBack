package com.scnujxjy.backendpoint.model.bo.es;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDocumentBO {
    private String id;
    private String fileName;
    private String type;
    private String createBy;
    private Long createTime;
    private String minioURL;
    private Long modifyTime;
    private String data;
    private String content;
    private String highlightedContent;
}
