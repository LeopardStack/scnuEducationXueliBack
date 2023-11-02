package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程信息表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-10-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("course_extra_information")
public class CourseExtraInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键，课程ID
     */
    @TableId(value = "course_id", type = IdType.AUTO)
    private Long courseId;

    /**
     * 课程标题
     */
    private String courseTitle;

    /**
     * 课程简介
     */
    private String courseDescription;

    /**
     * 课程通知
     */
    private String courseAnnouncement;

    /**
     * 排课表id
     */
    private Long courseScheduleId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程封面图 Minio 相对路径
     */
    private String courseCover;


}
