package com.scnujxjy.backendpoint.model.ro.courses_learning;

import com.baomidou.mybatisplus.annotation.TableField;
import com.scnujxjy.backendpoint.handler.type_handler.LongTypeHandler;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseNotificationsRO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程公告 ID
     */
    private Long id;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 通知标题
     */
    private String notificationTitle;

    /**
     * 通知内容（富文本）
     */
    private String notificationContent;

    /**
     * 课程通知附件
     */
    @ApiModelProperty(value = "课程通知附件")
    private List<MultipartFile> notificationAttachments;

    /**
     * 是否置顶
     */
    private Boolean isPinned;


}
