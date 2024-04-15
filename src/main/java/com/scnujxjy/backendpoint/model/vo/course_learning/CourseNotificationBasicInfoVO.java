package com.scnujxjy.backendpoint.model.vo.course_learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseNotificationBasicInfoVO {
    /**
     * 课程公告 ID
     */
    private Long id;

    /**
     * 通知标题
     */
    private String notificationTitle;


    /**
     * 创建时间
     */
    private Date createdAt;
}
