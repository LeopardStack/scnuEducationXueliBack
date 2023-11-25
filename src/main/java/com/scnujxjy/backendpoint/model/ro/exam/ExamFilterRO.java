package com.scnujxjy.backendpoint.model.ro.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author leopard
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ExamFilterRO {
    /**
     * 年级
     */
    private String grade;

    /**
     * 学院
     */
    private String college;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 层次
     */
    private String level;

    /**
     * 课程名称
     */
    private List<String> courseNames;

    /**
     * 班级名称
     */
    private List<String> classNames;

    /**
     * 学期
     */
    private String  teachingSemester;

    /**
     * 考试状态
     */
    private String examStatus;

    /**
     * 考试方式
     */
    private String examMethod;

    /**
     * 考试形式：开卷、闭卷
     */
    private String examType;
}
