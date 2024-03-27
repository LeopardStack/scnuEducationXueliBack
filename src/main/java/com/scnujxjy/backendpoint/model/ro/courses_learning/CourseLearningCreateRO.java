package com.scnujxjy.backendpoint.model.ro.courses_learning;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseLearningCreateRO {

    @ApiModelProperty(value = "课程ID", example = "1")
    private Long courseId;

    /**
     * 注意 这里的课程名称 与 课程学习 表里面的课程名称不同 这里主要是为了 处理那些 课程名称相同 但是 底层资源一致的特殊情况
     * 比如 两门课的上课老师一致  也一起上课
      */
    @ApiModelProperty(value = "课程名称", example = "Java 语言程序设计")
    private List<String> courseNames;

    /**
     * 这些年级参数 只是用来筛选具体的班级 和课程的
     */
    @ApiModelProperty(value = "年级", example = "2024")
    private List<String> grades;

    @ApiModelProperty(value = "课程类型", example = "直播")
    String courseType;

    @ApiModelProperty(value = "主讲老师用户名", example = "T20071030")
    private String defaultMainTeacherUsername;


    @ApiModelProperty(value = "班级名称集合，用来标识这门课 哪些学生会一起上课",
            example = "广州达德 海珠蓝星")
    private List<String> classIdentifier;

    /**
     * 助教在创建时 可以选择多名 当然 后续 修改也可以
     */
    @ApiModelProperty(value = "助教老师用户名", example = "T2023020232")
    private List<String> assistantUsername;

    @ApiModelProperty(value = "课程简介", example = "该门课是关于XXXX")
    private String courseDescription;


    @ApiModelProperty(value = "课程封面", example = "https://xxxx.png")
    private MultipartFile courseCover;

    @ApiModelProperty(value = "有效状态", example = "N")
    private String valid;

    @ApiModelProperty(value = "强制刷新白名单", example = "Y")
    private String freshWhiteList;
}
