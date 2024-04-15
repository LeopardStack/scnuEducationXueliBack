package com.scnujxjy.backendpoint.model.vo.course_learning;

import com.scnujxjy.backendpoint.model.vo.platform_message.AttachmentVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseNotificationDetailInfoVO {
    /**
     * 课程公告 ID
     */
    private Long id;

    /**
     * 通知标题
     */
    private String notificationTitle;

    /**
     * 通知内容（富文本）
     */
    private String notificationContent;

    /**
     * 附件集合
     */
    private List<AttachmentVO> attachmentVOList;


    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}
