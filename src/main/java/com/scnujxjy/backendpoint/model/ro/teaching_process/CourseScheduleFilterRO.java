package com.scnujxjy.backendpoint.model.ro.teaching_process;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 排课表课程筛选信息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseScheduleFilterRO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排课表主键 ID
     */
    private Long id;

    /**
     * 年级
     */
    String grade;

    /**
     * 学院
     */
    String college;

    /**
     * 学习形式
     */
    String studyForm;

    /**
     * 层次
     */
    String level;

    /**
     * 学期
     */
    String semester;

    /**
     * 行政班级
     */
    String adminClassName;

    /**
     * 教学班级
     */
    String teachingClassName;


    /**
     * 专业名称
     */
    String majorName;
    /**
     * 课程名称
     */
    String courseName;

    /**
     * 主讲老师姓名
     */
    String mainTeachingName;

    /**
     * 开始时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    Date teachingStartDate;

    /**
     * 截止时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    Date teachingEndDate;
}
