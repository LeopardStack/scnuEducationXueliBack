package com.scnujxjy.backendpoint.model.vo.exam;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 谢辉龙
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExamTeachersInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    @ExcelProperty(index = 1, value = "序号")
    private Integer index;

    /**
     * 学院名称
     */
    @ExcelProperty(index = 2, value = "教学学院名称")
    private String college;

    /**
     * 专业名称
     */
    @ExcelProperty(index = 3, value = "教学学院名称")
    private String majorName;

    /**
     * 学习形式
     */
    @ExcelProperty(index = 4, value = "学习形式")
    private String studyForm;

    /**
     * 层次
     */
    @ExcelProperty(index = 5, value = "层次")
    private String level;

    /**
     * 行政班别
     */
    @ExcelProperty(index = 6, value = "行政班别")
    private String className;

    /**
     * 教学班别
     */
    @ExcelProperty(index = 7, value = "教学班别")
    private String teachingClass;

    /**
     * 本学期采用“线上机考-人脸识别”机考课程名称
     */
    @ExcelProperty(index = 8, value = "本学期采用“线上机考-人脸识别”机考课程名称")
    private String courseName;

    /**
     * 是否用继教学院平台直播上课
     */
    @ExcelProperty(index = 9, value = "是否用继教学院平台直播上课")
    private String xueliPlatform;

    /**
     * 开/闭卷
     */
    @ExcelProperty(index = 10, value = "开/闭卷")
    private String examType;

    /**
     * 主讲教师（命题）
     */
    @ExcelProperty(index = 11, value = "主讲教师（命题）")
    private String mainTeacherName;

    /**
     * 主讲老师电话
     */
    @ExcelProperty(index = 12, value = "主讲老师电话")
    private String mainTeacherPhone;

    /**
     * 辅导教师（阅卷）
     */
    @ExcelProperty(index = 13, value = "辅导教师（阅卷）")
    private String tutorName;

    /**
     * 辅导教师电话
     */
    @ExcelProperty(index = 14, value = "辅导教师电话")
    private String tutorPhone;

    /**
     * 备注
     */
    @ExcelProperty(index = 15, value = "备注")
    private String remark;



}
