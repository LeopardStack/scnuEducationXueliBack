package com.scnujxjy.backendpoint.model.vo.course_learning;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseLearningStudentInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "课程主键 ID", example = "1")
    private Long courseId;

    @ApiModelProperty(value = "学生所属年级", example = "2024")
    private String grade;

    @ApiModelProperty(value = "学院", example = "计算机学院")
    private String college;

    @ApiModelProperty(value = "专业", example = "计算机科学与技术")
    private String majorName;

    @ApiModelProperty(value = "学习形式", example = "函授")
    private String studyForm;

    @ApiModelProperty(value = "层次", example = "专升本")
    private String level;

    @ApiModelProperty(value = "教学点", example = "广州达德教学点")
    private String teachingPointName;

    @ApiModelProperty(value = "班级名称", example = "广州达德")
    private String className;

    @ApiModelProperty(value = "身份证号码", example = "4310222XXXX")
    private String idNumber;

    @ApiModelProperty(value = "学号", example = "23000XXXX")
    private String studentNumber;

    @ApiModelProperty(value = "姓名", example = "张三")
    private String name;

    @ApiModelProperty(value = "是否补修", example = "true")
    private Boolean isRetake;
}
