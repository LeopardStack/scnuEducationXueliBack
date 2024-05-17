package com.scnujxjy.backendpoint.model.ro.admission_information;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentPlanApplyRO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @ApiModelProperty(value = "招生计划主键 ID", example = "1")
    private Integer id;

    /**
     * 年份
     */
    @ApiModelProperty(value = "年份", example = "2024")
    private Integer year;

    /**
     * 招生专业名称
     */
    @ApiModelProperty(value = "招生专业名称", example = "学前教育")
    private String majorName;

    /**
     * 招生专业名称
     */
    @ApiModelProperty(value = "招生专业名称集合 用于筛选", example = "学前教育")
    private List<String> majorNameList;

    /**
     * 学习形式
     */
    @ApiModelProperty(value = "学习形式", example = "函授")
    private String studyForm;

    /**
     * 学习形式
     */
    @ApiModelProperty(value = "学习形式集合 用于筛选", example = "函授")
    private List<String> studyFormList;

    /**
     * 学制
     */
    @ApiModelProperty(value = "学制", example = "3")
    private String educationLength;

    /**
     * 培养层次
     */
    @ApiModelProperty(value = "培养层次", example = "专升本")
    private String trainingLevel;

    /**
     * 培养层次
     */
    @ApiModelProperty(value = "培养层次集合 用于筛选", example = "专升本")
    private List<String> trainingLevelList;

    /**
     * 招生人数
     */
    @ApiModelProperty(value = "招生人数", example = "115")
    private Integer enrollmentNumber;

    /**
     * 招生对象
     */
    @ApiModelProperty(value = "招生对象", example = "具有高中毕业程度的教师、干部、职工等")
    private String targetStudents;

    /**
     * 招生区域
     */
    @ApiModelProperty(value = "招生区域", example = "广州达德")
    private String enrollmentRegion;

    /**
     * 具体办学地点
     */
    @ApiModelProperty(value = "具体办学地点", example = "广州市达德教学点")
    private String schoolLocation;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话", example = "8521231234")
    private String contactNumber;

    /**
     * 主管院系
     */
    @ApiModelProperty(value = "主管院系", example = "教育科学学院")
    private String college;

    /**
     * 主管院系集合
     */
    @ApiModelProperty(value = "主管院系集合 用于筛选", example = "教育科学学院")
    private List<String> collegeList;

    /**
     * 教学点名称
     */
    @ApiModelProperty(value = "教学点名称", example = "广州达德教学点")
    private String teachingPointName;

    /**
     * 教学点名称
     */
    @ApiModelProperty(value = "教学点名称集合 用于筛选", example = "广州达德教学点")
    private List<String> teachingPointNameList;
}
