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
public class NewStudentRO extends AnnouncementMsgUserFilterRO{
    /**
     * 录取新生年级
     */
    private String grade;

    /**
     * 准考证号码
     */
    private String admissionNumber;

    /**
     * 身份证号码
     */
    private String idCardNumber;

    /**
     * 姓名
     */
    private String name;

    /**
     * 所属学院
     */
    private List<String> collegeList;

    /**
     * 录取专业名称
     */
    private List<String> majorNameList;

    /**
     * 层次
     */
    private List<String> levelList;

    /**
     * 学习形式
     */
    private List<String> studyFormList;

    /**
     * 教学点
     */
    private List<String> teachingPointList;

    @Override
    public String filterArgs() {
        return JSON.toJSONString(this, SerializerFeature.WriteClassName);
    }


    @Override
    public AnnouncementMsgUserFilterRO parseFilterArgs(String jsonString) {
        return JSON.parseObject(jsonString, AnnouncementMsgUserFilterRO.class);
    }
}
