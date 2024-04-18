package com.scnujxjy.backendpoint.dao.entity.courses_learning;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import com.scnujxjy.backendpoint.handler.type_handler.LongTypeHandler;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程作业提交表
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName(value = "course_assignment_submissions", autoResultMap = true)
public class CourseAssignmentSubmissionsPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    @TableField(typeHandler = LongTypeHandler.class)
    private List<Long> submissionAttachments;

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
