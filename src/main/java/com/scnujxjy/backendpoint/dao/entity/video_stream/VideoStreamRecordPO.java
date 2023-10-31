package com.scnujxjy.backendpoint.dao.entity.video_stream;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.scnujxjy.backendpoint.handler.type_handler.LongListTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 直播记录表
 * </p>
 *
 * @author leopard
 * @since 2023-08-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("video_stream_record")
public class VideoStreamRecordPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 频道id
     */
    private String channelId;

    /**
     * 频道名称
     */
    private String name;

    /**
     * 频道密码
     */
    private String channelPasswd;

    /**
     * 助教密码
     */
    private String tutorPasswd;

    /**
     * 频道主持人名称
     */
    private String publisher;

    /**
     * 直播开始时间
     */
    private Date startTime;

    /**
     * 直播结束时间
     */
    private Date endTime;

    /**
     * 直播介绍
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 频道的观看页状态，取值为：live（直播中）、end（直播结束）、playback（回放中）、waiting（等待直播）
     */
    private String watchStatus;

    /**
     * 自频道角色
     */
    private Integer role;

    /**
     * 自频道id集合
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> sonId;

    /**
     * 主链接
     */
    private String url;

    /**
     * 观看链接
     */
    private String watchUrl;

    /**
     * 助教观看链接
     */
    private String tutorUrl;

}
