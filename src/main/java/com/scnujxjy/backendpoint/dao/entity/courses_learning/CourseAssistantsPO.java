package com.scnujxjy.backendpoint.dao.entity.courses_learning;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("course_assistants")
public class CourseAssistantsPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，唯一标识每个记录
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID，表示助教所辅助的课程
     */
    private Long courseId;

    /**
     * 助教的用户名
     */
    private String username;


}
