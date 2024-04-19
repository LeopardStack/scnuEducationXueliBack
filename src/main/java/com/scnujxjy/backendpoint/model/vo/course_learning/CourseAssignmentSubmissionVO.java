package com.scnujxjy.backendpoint.model.vo.course_learning;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.scnujxjy.backendpoint.handler.type_handler.LongTypeHandler;
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
public class CourseAssignmentSubmissionVO {
    /**
     * 作业提交 ID
     */
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 作业ID
     */
    private Long assignmentId;

    /**
     * 作业提交附件集合
     */
    private List<AttachmentVO> assignmentSubmissionList;

    /**
     * 作业分数
     */
    private String score;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}
