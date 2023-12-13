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
    @TableId(type = IdType.AUTO)
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

    /**
     * 发布者的用户 id
     */
    private Long userId;

    /**
     * 发布者姓名
     */
    private String name;

    /**
     * 状态：1-草稿，2-发布，3-撤销，4-删除
     */
    private Integer status;
}
