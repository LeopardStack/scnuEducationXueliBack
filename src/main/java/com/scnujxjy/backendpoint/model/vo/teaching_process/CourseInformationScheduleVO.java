package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 该类是为了显示管理端的课程信息
 * 与教学计划不同 默认返回的是本学期在学课程
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseInformationScheduleVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 层次
     */
    private String level;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 行政班别标识
     */
    private String adminClass;

    /**
     * 行政班别
     */
    private String className;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 授课方式
     */
    private String teachingMethod;

    /**
     * 课程学习时长
     */
    private String studyHours;

    /**
     * 学分
     */
    private String credit;

    /**
     * 授课学期
     */
    private String teachingSemester;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 课程编号
     */
    private String courseCode;

    /**
     * 课程封面 Minio 地址
     */
    private String courseCover;

    /**
     * 主讲教师 多个的原因是因为中途可能会换老师
     */
    private List<TeacherInformationVO> mainTeachers;

    /**
     * 辅导教师 多个的原因是因为一个班的学生可能会比较多
     */
    private List<TeacherInformationVO> tutors;
}
