package com.scnujxjy.backendpoint.dao.entity.teaching_process;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("course_schedule")
public class CourseSchedulePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ExcelProperty(value = "年级", index = 1)
    private String grade;

    @ExcelProperty(value = "专业名称", index = 2)
    private String majorName;

    @ExcelProperty(value = "层次", index = 3)
    private String level;

    @ExcelProperty(value = "学习形式", index = 4)
    private String studyForm;

    @ExcelProperty(value = "行政班别", index = 5)
    private String adminClass;

    @ExcelProperty(value = "教学班别", index = 6)
    private String teachingClass;

    @ExcelProperty(value = "学生人数", index = 7)
    private Integer studentCount;

    @ExcelProperty(value = "课程名称", index = 8)
    private String courseName;

    @ExcelProperty(value = "学时数", index = 9)
    private Integer classHours;

    @ExcelProperty(value = "考核类型", index = 10)
    private String examType;

    @ExcelProperty(value = "主讲教师", index = 11)
    private String mainTeacherName;

    @ExcelProperty(value = "主讲教师工号/学号", index = 12)
    private String mainTeacherId;

    @ExcelProperty(value = "主讲教师身份证号码", index = 13)
    private String mainTeacherIdentity;

    @ExcelProperty(value = "辅导教师", index = 14)
    private String tutorName;

    @ExcelProperty(value = "辅导教师工号", index = 15)
    private String tutorId;

    @ExcelProperty(value = "辅导教师身份证号码", index = 16)
    private String tutorIdentity;

    @ExcelProperty(value = "授课方式", index = 17)
    private String teachingMethod;

    @ExcelProperty(value = "上课地点", index = 18)
    private String classLocation;

    @ExcelProperty(value = "在线教学平台及网络课程资源拥有情况", index = 19)
    private String onlinePlatform;

    @ExcelProperty(value = "授课日期", index = 20)
    private Date teachingDate;

    @ExcelProperty(value = "授课时间", index = 21)
    private String teachingTime;

    @TableField(value = "teacher_username")
    private String teacherUsername;

    @TableField(value = "teaching_assistant_username")
    private String teachingAssistantUsername;

    /**
     * 排课表批次，用来唯一确定一次合班的排课表记录
     */
    @ExcelIgnore
    private Long batchIndex;
}
