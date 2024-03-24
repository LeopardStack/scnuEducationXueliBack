package com.scnujxjy.backendpoint.model.ro.courses_learning;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseSectionRO {
    @ApiModelProperty(value = "节点ID", example = "1")
    private Long id;


    @ApiModelProperty(value = "课程ID", example = "1")
    private Long courseId;

    @ApiModelProperty(value = "父节点 为空时代表其为顶级节点", example = "1")
    private CourseSectionRO courseSectionParent;

    @ApiModelProperty(value = "子节点集合")
    private List<CourseSectionRO> courseSectionChildren;

    @ApiModelProperty(value = "节点名称", example = "第一次课")
    private String sectionName;

    @ApiModelProperty(value = "节点顺序 用于区分同父节点的节点", example = "1")
    private Integer sequence;

    @ApiModelProperty(value = "节点类型", example = "1")
    private String contentType;

    @ApiModelProperty(value = "用户自己上传的视频资源", example = "1")
    private MultipartFile videoFile;


    @ApiModelProperty(value = "主讲老师账号", example = "T25XXXX")
    private String mainTeacherUsername;

    @ApiModelProperty(value = "主讲老师姓名", example = "张三")
    private String mainTeacherName;

    @ApiModelProperty(value = "开始时间，比如这个节点是一堂课", example = "2024/03/24 15:00")
    private Date startTime;

    @ApiModelProperty(value = "结束时间，比如这个节点是一堂课", example = "2024/03/24 18:00")
    private Date deadLine;

    @ApiModelProperty(value = "节点创建时间", example = "2024/03/24 15:00")
    private Date createdTime;

    @ApiModelProperty(value = "节点更新时间", example = "2024/03/24 15:00")
    private Date updatedTime;
}
