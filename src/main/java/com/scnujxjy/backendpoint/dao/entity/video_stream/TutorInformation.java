package com.scnujxjy.backendpoint.dao.entity.video_stream;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tutorInformation")
public class TutorInformation {
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
    private String tutorName;

    /**
     * 频道链接
     */
    private String tutorUrl;

    /**
     * 频道密码
     */
    private String tutorPassword;

    /**
     * 助教 userId
     */
    private String userId;

    private String account;
}
