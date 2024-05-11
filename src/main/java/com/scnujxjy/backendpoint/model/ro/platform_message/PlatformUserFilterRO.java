package com.scnujxjy.backendpoint.model.ro.platform_message;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PlatformUserFilterRO {
    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "筛选实体", notes = "这是JSON字符串")
    private String announcementMsgUserFilterRO; // 存储JSON字符串

    @ApiModelProperty(value = "页码")
    private Long pageNumber = 1L;

    @ApiModelProperty(value = "一页数据量")
    private Long pageSize = 10L;

    private transient AnnouncementMsgUserFilterRO parsedAnnouncementMsgUserFilterRO; // 不进行序列化

    public void parseUserFilter() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Class<? extends AnnouncementMsgUserFilterRO> filterClass = getClassFromUserType(this.userType);
        this.parsedAnnouncementMsgUserFilterRO = objectMapper.readValue(this.announcementMsgUserFilterRO, filterClass);
    }

    private Class<? extends AnnouncementMsgUserFilterRO> getClassFromUserType(String userType) {
        switch (userType) {
            case "管理员":
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
