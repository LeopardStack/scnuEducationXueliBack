package com.scnujxjy.backendpoint.dao.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 存储阅卷助教
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("course_exam_assistants")
public class CourseExamAssistantsPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课程ID，关联class_course_info表
     */
    private Long courseId;

    /**
     * 阅卷助教姓名
     */
    private String assistantName;

    /**
     * 阅卷教师用户名
     */
    private String teacherUsername;


    /**
     * 考试信息ID
     */
    private Long examId;
}
