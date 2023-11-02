package com.scnujxjy.backendpoint.model.ro.teaching_process;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseExtraInformationRO {
    /**
     * 自增主键，课程ID
     */
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
