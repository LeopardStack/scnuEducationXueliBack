package com.scnujxjy.backendpoint.model.ro.platform_message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.scnujxjy.backendpoint.constant.enums.announceMsg.AnnounceMsgUserTypeEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OldStudentRO extends AnnouncementMsgUserFilterRO{
    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 身份证号码
     */
    private String idCardNumber;

    /**
     * 姓名
     */
    private String name;

    /**
     * 在籍生年级
     */
    private String grade;


    /**
     * 所属学院
     */
    private List<String> collegeList;

    /**
     * 专业名称
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

    /**
     * 学籍状态
     */
    private List<String> academicStatusList;

    /**
     * 旧生身份证号码列表 即平台用户名
     */
    private List<String> usernames;

    @Override
    public String filterArgs() {
        return JSON.toJSONString(this, SerializerFeature.WriteClassName);
    }


    @Override
    public OldStudentRO parseFilterArgs(String jsonString) {
        return JSON.parseObject(jsonString, OldStudentRO.class);
    }
}
