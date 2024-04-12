package com.scnujxjy.backendpoint.model.vo.course_learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class StudentCourseLearningDataVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程 ID
     */
    Long courseId;

    /**
     * 课程名字
     */
    private String courseName;

    /**
     * 直播间频道 ID
     */
    private String channelId;

    /**
     * 直播观看时长  单位 / 秒
     */
    private Long liveTimeCount;

    /**
     * 点播观看时长  单位 / 秒
     */
    private Long videoTimeCount;
}
