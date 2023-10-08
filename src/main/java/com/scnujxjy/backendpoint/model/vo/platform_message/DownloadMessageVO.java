package com.scnujxjy.backendpoint.model.vo.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.scnujxjy.backendpoint.dao.entity.platform_message.DownloadMessagePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件Minio地址
     */
    private String fileMinioUrl;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 生成时间
     */
    private Date createdAt;

    /**
     * 是否已读
     */
    private Boolean isRead;
}