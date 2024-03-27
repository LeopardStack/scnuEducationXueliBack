package com.scnujxjy.backendpoint.model.ro.courses_learning;

import com.scnujxjy.backendpoint.model.bo.course_learning.TeacherInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseScheduleSearchRO {
    @ApiModelProperty(value = "课程主键ID", example = "145")
    private Long courseId;

    @ApiModelProperty(value = "年级", example = "2023")
    private String grade;

    @ApiModelProperty(value = "学院", example = "教育科学学院")
    private String college;

    @ApiModelProperty(value = "专业名称", example = "小学教育")
    private String majorName;

    @ApiModelProperty(value = "层次", example = "专升本")
    private String level;

    @ApiModelProperty(value = "学习形式", example = "函授")
    private String studyForm;

    @ApiModelProperty(value = "教学点", example = "广州达德教学点")
    private String teachingPointName;

    @ApiModelProperty(value = "班级名称", example = "广州达德")
    private String className;

    @ApiModelProperty(value = "课程名称", example = "教育学")
    private String courseName;

    @ApiModelProperty(value = "主讲教师")
    private TeacherInfo teacherInfo;

    @ApiModelProperty(value = "课程类型", example = "直播")
    private String courseType;

    @ApiModelProperty(value = "课程开始时间", example = "20240304 11:30")
    private Date startTime;

    @ApiModelProperty(value = "课程结束时间", example = "20240304 15:30")
    private Date deadLine;
}
