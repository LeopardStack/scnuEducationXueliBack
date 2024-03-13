package com.scnujxjy.backendpoint.model.ro.courses_learning;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CoursesLearningRO {
    @ApiModelProperty(value = "年级", example = "2023")
    String grade;

    @ApiModelProperty(value = "学院", example = "计算机学院")
    String college;

    @ApiModelProperty(value = "教学点", example = "广州达德教学点")
    String teachingPointName;

    @ApiModelProperty(value = "专业", example = "计算机科学与技术")
    String majorName;

    @ApiModelProperty(value = "学习形式", example = "函授")
    String studyForm;

    @ApiModelProperty(value = "层次", example = "专升本")
    String level;

    @ApiModelProperty(value = "课程名称", example = "数据结构")
    String courseName;

    @ApiModelProperty(value = "主讲老师姓名", example = "张三")
    String mainTeacherName;
}