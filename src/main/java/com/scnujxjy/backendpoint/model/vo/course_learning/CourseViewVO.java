package com.scnujxjy.backendpoint.model.vo.course_learning;

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
@Accessors(chain = true)
@Builder
public class CourseViewVO {
    @ApiModelProperty(value = "节点ID", example = "10")
    private Long id;

    @ApiModelProperty(value = "课程主键ID", example = "145")
    private Long courseId;

    private Long parentSectionId;

    private String sectionName;

    private Integer sequence;

    private String contentType;

    private Long contentId;

    private String mainTeacherUsername;

    private Date startTime;

    private Date deadline;

    private String valid;

    private Date createdTime;

    private Date updatedTime;

    @ApiModelProperty(value = "频道ID", example = "145")
    private String channelId;

    @ApiModelProperty(value = "直播状态", example = "145")
    private String livingRoomState;
}
