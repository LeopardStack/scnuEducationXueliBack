package com.scnujxjy.backendpoint.dao.entity.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
     * 附件名称
     */
    private String attachmentName;

    /**
     * 附件大小
     */
    private Long attachmentSize;

    /**
     * 用户 名
     */
    private String username;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}
