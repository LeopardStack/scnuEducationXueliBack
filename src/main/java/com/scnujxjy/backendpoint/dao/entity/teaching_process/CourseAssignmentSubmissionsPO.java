package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程作业提交表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-12-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("course_assignment_submissions")
public class CourseAssignmentSubmissionsPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 作业ID，关联到course_assignments表
     */
    private Long assignmentId;

    /**
     * 提交学生的用户ID
     */
    private Long studentId;

    /**
     * 提交作业的文件路径
     */
    private String submissionFilePath;

    /**
     * 提交时间
     */
    private Date submissionTime;

    /**
     * 作业成绩
     */
    private BigDecimal grade;

    /**
     * 老师反馈
     */
    private String feedback;

    /**
     * 提交状态，如已提交、已评分等
     */
    private String status;


}
