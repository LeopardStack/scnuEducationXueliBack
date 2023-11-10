package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ScheduleCoursesInformationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    public ScheduleCoursesInformationVO(Long batchIndex) {
        this.batchIndex = batchIndex;
    }

    /**
     * 批次
     */
    Long batchIndex;

    /**
     * 学院信息列表 可能存在跨学院合班
     */
    List<String> colleges;

    /**
     * 专业名称
     */
    List<String> majorNames;

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
     * 现在的直播状态
     */
    String livingStatus;

    /**
     * 现在的直播间信息 ID
     */
    String onlinePlatform;

    /**
     * 现在的直播间频道ID
     */
    String channelId;

    /**
     * 合班的班级 Set
     */
    List<String> className;

    /**
     * 距离今天最近的上课日期
     */
    Date teachingDate;

    /**
     * 距离现在最近的上课时间
     */
    String teachingTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduleCoursesInformationVO that = (ScheduleCoursesInformationVO) o;
        return Objects.equals(batchIndex, that.batchIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchIndex);
    }
}
