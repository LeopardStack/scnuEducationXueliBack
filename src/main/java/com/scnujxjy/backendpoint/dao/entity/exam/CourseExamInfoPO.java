package com.scnujxjy.backendpoint.dao.entity.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 存储考试信息
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
@TableName("course_exam_info")
public class CourseExamInfoPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级的唯一标识符
     */
    private String classIdentifier;

    /**
     * 课程名称
     */
    private String course;

    /**
     * 主讲教师姓名
     */
    private String mainTeacher;

    /**
     * 主讲教师用户名
     */
    private String teacherUsername;

    /**
     * 考试方式，默认为线下
     */
    private String examMethod;

    /**
     * 考试状态：已结束、未开始、进行中
     */
    private String examStatus;

    /**
     * 考试形式：开卷、闭卷
     */
    private String examType;

    /**
     * 授课学期
     */
    private String teachingSemester;


    /**
     * 是否有效 Y/N  无效的无法再编辑
     */
    private String isValid;

    /**
     * 课程 ID
     */
    private Long courseId;

}
