package com.scnujxjy.backendpoint.model.ro.courses_learning;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CoursesLearningRO {
    @ApiModelProperty(value = "课程主键ID", example = "1")
    private Long id;

    /**
     * 用于重修学生查询课程
     */
    @ApiModelProperty(value = "课程主键ID集合", example = "1")
    private Set<Long> courseIds;

    @ApiModelProperty(value = "年份", example = "2023")
    String year;

    @ApiModelProperty(value = "年级", example = "2023")
    String grade;

    @ApiModelProperty(value = "学院", example = "计算机学院")
    String college;

    @ApiModelProperty(value = "教学点", example = "广州达德教学点")
    String teachingPointName;

    @ApiModelProperty(value = "班级名称集合 存在一个教学点管理多个不同简称的班级", example = "广州达德")
    List<String> classNameSet;

    @ApiModelProperty(value = "班级名称集合 用于学生获取班级信息", example = "广州达德")
    List<String> classNames;

    @ApiModelProperty(value = "班级名称", example = "广州达德")
    String className;

    @ApiModelProperty(value = "专业", example = "计算机科学与技术")
    String majorName;

    @ApiModelProperty(value = "学习形式", example = "函授")
    String studyForm;

    @ApiModelProperty(value = "层次", example = "专升本")
    String level;

    @ApiModelProperty(value = "课程名称", example = "数据结构")
    String courseName;

    @ApiModelProperty(value = "课程类型", example = "直播")
    String courseType;

    @ApiModelProperty(value = "主讲老师姓名", example = "张三")
    String mainTeacherName;

    @ApiModelProperty(value = "主讲老师用户名", example = "T20071030")
    private String defaultMainTeacherUsername;

    @ApiModelProperty(value = "上课时间 Start", example = "2024 03 15 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date courseStartTime;

    @ApiModelProperty(value = "上课时间 End", example = "2024 04 15 00:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date courseEndTime;
}