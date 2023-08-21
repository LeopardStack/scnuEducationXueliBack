package com.scnujxjy.backendpoint.dao.entity.video_stream;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 直播记录表
 * </p>
 *
 * @author leopard
 * @since 2023-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class VideoStreamRecords implements Serializable {

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
    private String sonId;

    /**
     * 主链接
     */
    private String url;

    /**
     * 观看链接
     */
    private String watchUrl;


}
