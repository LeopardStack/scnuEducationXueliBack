package com.scnujxjy.backendpoint.dao.entity.courses_learning;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("live_scheduling")
public class CoursesLearningPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String grade;

    private String courseName;

    private String courseType;

    private String courseDescription;

    private String courseCoverUrl;

    private String defaultMainTeacherUsername;

    /**
     * 课程标识符 用来在未来建立好 全局统一的课程标识符后
     * 直接通过课程标识符来匹配 某个年级教学计划中这门课的所有班
     */
    private String courseIdentifier;

    private String valid;

    private Date createdTime;

    private Date updatedTime;


}
