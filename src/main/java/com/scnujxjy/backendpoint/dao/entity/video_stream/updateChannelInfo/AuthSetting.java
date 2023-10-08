package com.scnujxjy.backendpoint.dao.entity.video_stream.updateChannelInfo;

import lombok.Data;

/**
 * 观看条件设置实体类
 */
@Data
public class AuthSetting {
    // 主要观看条件为1，次要观看条件为2
    private Integer rank;
    // 是否开启条件观看（N：关闭，Y：开启）
    private String enabled;
    // 授权类型（如pay, code, phone等）
    private String authType;
    // 付费观看欢迎语标题
    private String payAuthTips;
    // 价格，单位为元
    private Float price;
    // 付费有效截止日期，格式为13位时间戳
    private String watchEndTime;
    // 付费有效时长，单位天
    private Integer validTimePeriod;
    // 验证码
    private String authCode;
    // 提示文案
    private String qcodeTips;
    // 公众号二维码地址
    private String qcodeImg;
    // 提示文案
    private String authTips;
    // 白名单入口文案
    private String whiteListEntryText;
    // 白名单输入提示
    private String whiteListInputTips;
    // 登记观看信息，上限为5个
    private InfoField[] infoFields;
    // 欢迎标题
    private String infoAuthTips;
    // 提示信息
    private String infoDesc;
    // 入口文本
    private String infoEntryText;
    // SecretKey
    private String externalKey;
    // 自定义url
    private String externalUri;
    // 跳转地址
    private String externalRedirectUri;
    // SecretKey
    private String customKey;
    // 自定义url
    private String customUri;
    // 独立授权SecretKey
    private String directKey;
    // 到课名单开关（N：关闭，Y：开启）
    private String expectedArrivalEnabled;
}