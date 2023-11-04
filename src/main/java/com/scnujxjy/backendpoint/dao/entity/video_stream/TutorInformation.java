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
     * 频道名称
     */
    private String tutorName;

    /**
     * 频道密码
     */
    private String tutorUrl;

    /**
     * 频道密码
     */
    private String tutorPassword;

    /**
     * 助教密码
     */
    private String userId;


}
