package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 存储学生学号和批次ID的表，批次ID代表教师、课程以及合班的班级
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("retake_students")
public class RetakeStudentsPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生学号
     */
    private String studentNumber;

    /**
     * 插入到指定班级中进行补休
     */
    private String classIdentifier;

    /**
     * 课程代码
     */
    private String courseCode;

    /**
     * 课程名称
     */
    private String courseName;


}
