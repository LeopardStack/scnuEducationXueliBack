package com.scnujxjy.backendpoint.model.ro.platform_message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnounceMsgUserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

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
    private List<String> departmentList;


    /**
     * 学院
     */
    private List<String> collegeNameList;

    /**
     * 教学点
     */
    private List<String> teachingPointNameList;

    /**
     * 角色
     */
    private List<String> roleNameList;

    @Override
    public String filterArgs() {
        return JSON.toJSONString(this, SerializerFeature.WriteClassName);
    }


    @Override
    public ManagerRO parseFilterArgs(String jsonString) {
        return JSON.parseObject(jsonString, ManagerRO.class);
    }
}
