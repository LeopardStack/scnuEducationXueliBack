package com.scnujxjy.backendpoint.model.ro.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseBatchExtraInformationRO {
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
    private List<Long> courseScheduleId;  // 更新为 List<Long> 类型

    /**
     * 课程名称
     */
    private List<String> courseName;  // 更新为 List<String> 类型

    /**
     * 课程封面图 Minio 相对路径
     */
    private String courseCover;
}
