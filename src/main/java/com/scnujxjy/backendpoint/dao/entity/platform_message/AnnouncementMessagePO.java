package com.scnujxjy.backendpoint.dao.entity.platform_message;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 公告消息表
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
@TableName("announcement_message")
public class AnnouncementMessagePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 附件ID
     */
    private Long attachmentId;

    /**
     * 生成时间
     */
    private Date createdAt;


}
