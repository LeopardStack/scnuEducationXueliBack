package com.scnujxjy.backendpoint.dao.entity.video_stream;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("video_information")
public class VideoInformation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String channelId;

    private Long sectionId;

    private String sessionId;

    private String url;

    private String cdnUrl;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
