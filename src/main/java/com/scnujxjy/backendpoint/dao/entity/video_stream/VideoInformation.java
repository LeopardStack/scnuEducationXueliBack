package com.scnujxjy.backendpoint.dao.entity.video_stream;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("video_information")
public class VideoInformation {

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
     * 助教名称
     */
    private String sessionId;

    /**
     * 频道链接
     */
    private String url;

    /**
     * 频道密码
     */
    private Integer status;

    private Date createTime;

    private Date updateTime;
}
