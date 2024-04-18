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
public class CourseAssignmentVO {
    /**
     * 课程作业 ID
     */
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 作业名
     */
    private String assignmentName;

    /**
     * 作业描述
     */
    private String assignmentDescription;

    /**
     * 附件信息
     */
    private List<AttachmentVO> attachmentVOList;

    /**
     * 截止日期
     */
    private Date dueDate;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}
