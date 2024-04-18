package com.scnujxjy.backendpoint.model.ro.courses_learning;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseAssignmentRO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @ApiModelProperty(value = "课程作业 ID")
    private Long id;

    /**
     * 课程ID
     */
    @ApiModelProperty(value = "课程作业的课程 ID")
    private Long courseId;

    /**
     * 作业名
     */
    @ApiModelProperty(value = "课程作业的名称")
    private String assignmentName;

    /**
     * 作业描述
     */
    @ApiModelProperty(value = "课程作业的描述")
    private String assignmentDescription;

    /**
     * 课程作业附件
     */
    @ApiModelProperty(value = "课程作业附件")
    private List<MultipartFile> assignmentAttachments;

    /**
     * 课程作业上传附件
     */
    @ApiModelProperty(value = "课程作业附件")
    private List<MultipartFile> postAssignmentAttachments;

    /**
     * 课程作业附件ID 集合
     */
    @ApiModelProperty(value = "课程作业附件 ID 集合")
    private List<Long> assignmentAttachmentIds;

    /**
     * 截止日期
     */
    @ApiModelProperty(value = "截止日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") // 用于 Spring MVC 绑定
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8") // 用于 JSON 序列化/反序列化
    private Date dueDate;

    /**
     * 学生用户名
     */
    @ApiModelProperty(value = "学生用户名")
    private String username;

    /**
     * 作业提交 ID
     */
    @ApiModelProperty(value = "作业提交 ID")
    private Long assignmentPostId;

    /**
     * 分数
     */
    @ApiModelProperty(value = "分数")
    private String score;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

}
