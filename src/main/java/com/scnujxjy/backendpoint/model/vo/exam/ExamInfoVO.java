package com.scnujxjy.backendpoint.model.vo.exam;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 考试信息展示，即通过教学计划让其确定哪些需要本学期安排考试
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExamInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long examId;

    private String grade;

    private String college;

    private String majorName;

    private String level;

    private String studyForm;

    private String adminClass;

    /**
     * 班级名称
     */
    private String className;

    private String courseName;

    private Integer studyHours;

    private String assessmentType;

    private String teachingMethod;

    private String courseType;

    private Integer credit;

    private String teachingSemester;

    /**
     * 如果需要覆盖则读取它
     */
    private String remark;

    /**
     * 课程编号
     */
    private String courseCode;

    /**
     * 课程封面图 Minio 地址
     */
    private String courseCover;

    /**
     * 主讲教师 多个的原因是因为中途可能会换老师
     */
    private List<TeacherInformationVO> mainTeachers;

    /**
     * 辅导教师 多个的原因是因为一个班的学生可能会比较多
     */
    private List<TeacherInformationPO> tutors;

    /**
     * 考试方式 机考、线下等
     */
    private String examMethod;

    /**
     * 考试状态：未开始、已结束、进行中
     */
    private String examStatus;

    /**
     * 考试形式：开卷、闭卷
     */
    private String examType;

    private String mainTeacherUsername;

    private String mainTeacherName;
}
