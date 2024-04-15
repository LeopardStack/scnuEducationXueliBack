package com.scnujxjy.backendpoint.dao.entity.courses_learning;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 课程作业提交表
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course_assignment_submissions")
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
     * 用户ID
     */
    private Long userId;

    /**
     * 作业ID
     */
    private Long assignmentId;

    /**
     * 作业提交附件集合
     */
    private String submissionAttachments;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;


}
