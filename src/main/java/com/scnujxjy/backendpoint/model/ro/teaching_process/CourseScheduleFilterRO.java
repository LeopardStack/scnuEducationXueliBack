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
     * 专业名称
     */
    String majorName;

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
     * 授课方式
     */
    String teachingMethod;

    /**
     * 授课学期
     */
    String teachingSemester;

    /**
     * 班级名称
     */
    String className;

    /**
     * 课程名称
     */
    String courseName;

    /**
     * 主讲老师姓名
     */
    String mainTeachingName;

    /**
     * 助教姓名
     */
    String tutorName;

    /**
     * 直播状态
     */
    String livingStatus;

    /**
     * 批次
     */
    Long batchIndex;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    Date teachingStartDate;

    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    Date teachingEndDate;


    /**
     * 行政班别集合
     */
    List<String> classNames;

    /**
     * 行政班（用于教学点）
     */
    List<String> classSet;

    /**
     * 课程名称集合
     */
    List<String> courseNames;

    /**
     * 考试状态
     */
    private String examStatus;
    /**
     * 考试方式
     */
    private String examMethod;
}
