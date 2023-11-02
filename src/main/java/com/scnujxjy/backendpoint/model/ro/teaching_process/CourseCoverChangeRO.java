package com.scnujxjy.backendpoint.model.ro.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author hp
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseCoverChangeRO {
    /**
     * 年级
     */
    String grade;

    /**
     * 专业名称
     */
    String majorName;

    /**
     * 层次
     */
    String level;

    /**
     * 学习形式
     */
    String studyForm;

    /**
     * 班级名称
     */
    String className;

    /**
     * 课程名称
     */
    String courseName;

    /**
     * 新的课程封面图 Minio URL
     */
    String newCourseCover;
}
