package com.scnujxjy.backendpoint.model.ro.platform_message;

import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnounceMsgUserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ManagerRO extends AnnouncementMsgUserFilterRO{
    /**
     * 用户名
     */
    private String username;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 工号/学号
     */
    private String workNumber;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 部门
     */
    private String department;


    /**
     * 学院
     */
    private String collegeName;

    /**
     * 教学点
     */
    private String teachingPointName;

    @Override
    public String filterArgs() {
        return AnnounceMsgUserTypeEnum.MANAGER.getUserType() + " "
                + toString()
                ;
    }
}
