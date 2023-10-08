package com.scnujxjy.backendpoint.dao.entity.video_stream.updateChannelInfo;

import lombok.Data;

/**
 * 登记观看信息字段实体类
 */
@Data
public class InfoField {
    // 登记信息名，最多为8字符
    private String name;
    // 登记类型（如name, text, mobile等）
    private String type;
    // 下拉选项时，下拉的选项值，以英文逗号分割
    private String[] options;
    // 文本框输入提示，最多为8字符
    private String placeholder;
    // 短信验证开关（Y：开启，N：关闭）
    private String sms;
}