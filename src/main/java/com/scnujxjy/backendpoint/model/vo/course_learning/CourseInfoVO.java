package com.scnujxjy.backendpoint.model.vo.course_learning;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class CourseInfoVO {
    @ApiModelProperty(value = "课程主键 ID", example = "1")
    private Long courseId;

    @ApiModelProperty(value = "年份", example = "2024")
    private String year;

    @ApiModelProperty(value = "年级", example = "2023")
    private String grade;

    @ApiModelProperty(value = "课程名称", example = "Java语言程序设计")
    private String courseName;

    /**
     * 课程类型 直播、点播、线下、 混合
     */
    @ApiModelProperty(value = "课程类型", example = "直播")
    private String courseType;

    /**
     * 课程描述
     */
    @ApiModelProperty(value = "课程描述")
    private String courseDescription;

    /**
     * 课程封面 URL
     */
    @ApiModelProperty(value = "课程封面 URL")
    private String courseCoverUrl;

    /**
     * 默认主讲老师
     */
    @ApiModelProperty(value = "默认主讲老师用户名", example = "T25320XXX")
    private String defaultMainTeacherUsername;

    /**
     * 主讲老师姓名
     */
    @ApiModelProperty(value = "主讲老师姓名", example = "张三")
    private String defaultMainTeacherName;

    /**
     * 主讲老师姓名
     */
    @ApiModelProperty(value = "助教信息")
    private List<AssistantInfoVO> assistantInfoVOList;

    /**
     * 课程标识符 用来在未来建立好 全局统一的课程标识符后
     * 直接通过课程标识符来匹配 某个年级教学计划中这门课的所有班
     */
    @ApiModelProperty(value = "课程标识符")
    private String courseIdentifier;

    /**
     * 班级名称
     */
    @ApiModelProperty(value = "班级名称集合")
    private String classNames;

    /**
     * 班级名称
     */
    @ApiModelProperty(value = "学生所在班级名称")
    private String className;

    /**
     * 课程是否有效 Y/N
     */
    @ApiModelProperty(value = "课程是否有效")
    private String valid;

    /**
     * 近期排课 如果为空 说明未设置 但是直播课都会设置
     */
    @ApiModelProperty(value = "近期排课时间")
    private Date recentCourseScheduleTime;

    /**
     * 对于已修课程只会看到成绩
     */
    @ApiModelProperty(value = "90")
    private String score;

    /**
     * eg. 已修、在修、未修
     */
    @ApiModelProperty(value = "课程状态")
    private String state;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;
}
