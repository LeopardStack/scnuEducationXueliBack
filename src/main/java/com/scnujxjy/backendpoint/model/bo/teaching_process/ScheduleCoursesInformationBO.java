package com.scnujxjy.backendpoint.model.bo.teaching_process;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 排课表课程信息管理类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ScheduleCoursesInformationBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 批次
     */
    Long batchIndex;

    /**
     * 主讲教师名字
     */
    String mainTeacherName;

    /**
     * 主讲教师账号
     */
    String teacherUsername;

    /**
     * 教学班别
     */
    String teachingClass;

    /**
     * 课程名称
     */
    String courseName;

    /**
     * 上课日期
     */
    Date teachingDate;

    /**
     * 上课时间
     */
    String teachingTime;

    /**
     * 直播间信息 id
     */
    String onlinePlatform;

}
