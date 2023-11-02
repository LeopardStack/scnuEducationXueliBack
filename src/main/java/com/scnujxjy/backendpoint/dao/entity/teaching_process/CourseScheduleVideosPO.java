package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 排课表视频信息表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-10-30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("course_schedule_videos")
public class CourseScheduleVideosPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 排课表ID
     */
    private Long scheduleId;

    /**
     * 点播平台
     */
    private String platform;

    /**
     * 点播视频UID
     */
    private String videoUid;

    /**
     * Minio本地云存储URL
     */
    private String minioUrl;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
