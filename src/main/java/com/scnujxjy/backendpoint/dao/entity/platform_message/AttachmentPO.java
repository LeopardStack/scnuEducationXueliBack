package com.scnujxjy.backendpoint.dao.entity.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 附件表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-09-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("attachment")
public class AttachmentPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 附件ID
     */
    private Long attachmentId;

    /**
     * 顺序
     */
    private Integer attachmentOrder;

    /**
     * 附件Minio地址
     */
    private String attachmentMinioUrl;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 附件大小
     */
    private Long attachmentSize;


}
