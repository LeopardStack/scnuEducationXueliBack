package com.scnujxjy.backendpoint.model.ro.platform_message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class AnnouncementMessageUsersRO {
    /**
     * 自增ID
     */
    @ApiModelProperty(value = "平台消息的主键  ID")
    private Long id;

    /**
     * 公告消息 的 主键 ID
     */
    @ApiModelProperty(value = "公告消息 的 主键 ID")
    private Long announceMsgId;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "用户列表")
    private List<String> userNameList;

    @ApiModelProperty(value = "用户角色")
    private String roleName;

    // Getter 和 Setter
    @Getter
    @ApiModelProperty(value = "筛选实体")
    private String announcementMsgUserFilterRO;

    // 解析后的对象
    @Getter
    private AnnouncementMsgUserFilterRO parsedAnnouncementMsgUserFilterRO;


    @ApiModelProperty(value = "公告标题")
    private String title;


    @ApiModelProperty(value = "公告标题")
    private String content;

    @ApiModelProperty(value = "是否弹框")
    private Boolean isPopup;

    @ApiModelProperty(value = "公告状态")
    private String status;

    @ApiModelProperty(value = "公告截止时间 可以为空", example = "2024-05-18 19:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") // 用于 Spring MVC 绑定
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8") // 用于 JSON 序列化/反序列化
    private Date dueDate;


    /**
     * 公告附件集合
     */
    @ApiModelProperty(value = "公告附件集合")
    private List<MultipartFile> announcementAttachments;

    /**
     * 公告已有附件集合
     */
    @ApiModelProperty(value = "公告已有附件 ID 集合")
    private List<Long> announcementAttachmentIds;


    public void setAnnouncementMsgUserFilterRO(String announcementMsgUserFilterRO) {
        this.announcementMsgUserFilterRO = announcementMsgUserFilterRO;
    }

    // 解析 JSON 字符串并根据 userType 设置解析后的对象
    public void parseUserFilter() throws Exception {
        if (this.userType == null || this.announcementMsgUserFilterRO == null) {
            throw new IllegalArgumentException("User type or user filter data is missing");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Class<? extends AnnouncementMsgUserFilterRO> filterClass = getClassFromUserType(this.userType);
        this.parsedAnnouncementMsgUserFilterRO = objectMapper.readValue(this.announcementMsgUserFilterRO, filterClass);
    }

    private Class<? extends AnnouncementMsgUserFilterRO> getClassFromUserType(String userType) throws IllegalArgumentException {
        switch (userType) {
            case "管理员":
            case "":
                return ManagerRO.class;
            case "新生":
                return NewStudentRO.class;
            case "在籍生":
                return OldStudentRO.class;
            default:
                throw new IllegalArgumentException("无效的用户类型: " + userType);
        }
    }

}
