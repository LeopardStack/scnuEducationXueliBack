package com.scnujxjy.backendpoint.model.ro.exam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 批量设置命题教师和阅卷助教
 * @author leopard
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class BatchSetTeachersInfoRO {
    /**
     * 主讲教师 userId int 类型
     */
    String mainTeacher;

    /**
     * 助教 userId int 类型
     */
    List<String> assistants;

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
     * 是否清除所有的命题人和阅卷人信息
     * 前提是主讲为空并且助教也为空
     */
    private Boolean clearAllTeachers;
}
