package com.scnujxjy.backendpoint.model.vo.platform_message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AttachmentVO {
    private Long id;

    /**
     * 关联 id
     */
    private Long relatedId;

    /**
     * 附件类型
     */
    private String attachmentType;

    /**
     * 顺序
     */
    private Integer attachmentOrder;

    /**
     * 附件Minio地址
     */
    private String attachmentMinioPath;

    /**
     * 附件Minio访问地址
     */
    private String attachmentMinioViewPath;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 附件大小
     */
    private Long attachmentSize;

    /**
     * 上传用户名称
     */
    private String name;
}
