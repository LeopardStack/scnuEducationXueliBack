package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程作业表
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
@TableName("course_assignments")
public class CourseAssignmentsPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程代码
     */
    private String batchId;

    /**
     * 作业标题
     */
    private String assignmentTitle;

    /**
     * 作业描述
     */
    private String description;

    /**
     * 截止日期
     */
    private Date dueDate;

    /**
     * 作业文件路径
     */
    private String filePath;

    /**
     * 发布时间
     */
    private Date postedTime;

    /**
     * 发布者用户ID
     */
    private Long postedBy;


}
