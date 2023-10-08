package com.scnujxjy.backendpoint.dao.entity.video_stream;

import lombok.Data;

@Data
public class LiveRequestBody {

    /** 直播名称，最大长度100 */
    private String name;

    /** 直播场景
     topclass（大班课）
     double（双师课，该场景需开通）
     train（企业培训）
     alone（活动营销）
     seminar（研讨会）
     guide（导播，该场景需开通） */
    private String newScene;

    /** 直播模板
     ppt（三分屏-横屏）
     portrait_ppt（三分屏-竖屏）
     alone（纯视频-横屏）
     portrait_alone（纯视频-竖屏）
     topclass（纯视频极速-横屏）
     portrait_topclass（纯视频极速-竖屏）
     seminar（研讨会）
     字段约束：
     直播场景（newScene字段）为 topclass（大班课）时，字段支持ppt（三分屏-横屏）、portrait_ppt（三分屏-竖屏）、
        alone（纯视频-横屏）、portrait_alone（纯视频-竖屏）、topclass（纯视频极速-横屏）、portrait_topclass（纯视频极速-竖屏）
     直播场景（newScene字段）为 train（企业培训）或 alone（活动营销）时，该字段支持ppt（三分屏-横屏）、
        portrait_ppt（三分屏-竖屏）、alone（纯视频-横屏）、portrait_alone（纯视频-竖屏）
     直播场景（newScene字段）为 double（双师课）时，该字段支持ppt（三分屏-横屏）、alone（纯视频-横屏）
     直播场景（newScene字段）为 seminar（研讨会）时，该字段支持seminar（研讨会）
     直播场景（newScene字段）为 guide（导播）时，该字段支持alone（纯视频-横屏）、portrait_alone（纯视频-竖屏） */
    private String template;

    /** 讲师登录密码，直播场景不是研讨会时有效，长度6-16位，不传则由系统随机生成。（接口允许设置纯数字或纯字母，若需要在直播后台编辑，建议同时包含数字和字母） */
    private String channelPasswd;

    /** 研讨会主持人密码，仅直播场景是研讨会时有效，长度6-16位，不传则由系统随机生成。研讨会主持人密码和参会人密码不能相同。
     * （接口允许设置纯数字或纯字母，若需要在直播后台编辑，建议同时包含数字和字母） */
    private String seminarHostPassword;

    /** 研讨会参会人密码，仅直播场景是研讨会时有效，长度6-16位，不传则由系统随机生成。研讨会主持人密码和参会人密码不能相同。
     * （接口允许设置纯数字或纯字母，若需要在直播后台编辑，建议同时包含数字和字母） */
    private String seminarAttendeePassword;

    /** 直播延迟 Y无延时 N普通延迟 */
    private String pureRtcEnabled;

    /** 转播类型 normal不开启、transmit发起转播、receive接收转播（该功能需要开通），部分直播场景不支持转播设置，具体请阅读接口约束 */
    private String type;

    /** 线上双师 transmit大房间、receive小房间 */
    private String doubleTeacherType;

    /** 中英双语直播开关 Y开、N关 */
    private String cnAndEnLiveEnabled;

    /** 封面图片地址，非保利威域名下的图片需调用上传频道所有装修图片素材上传 */
    private String splashImg;

    /** 连麦人数限制，最多16人，不传使用账号最大连麦人数（可联系商务修改） */
    private Integer linkMicLimit;

    /** 分类ID，可通过“查询直播分类”接口获取 */
    private Integer categoryId;

    /** 开始时间，时间戳，如：1629734400000【注：仅做直播前倒计时显示，不对讲师开播操作产生影响】 */
    private Long startTime;

    /** 结束时间，时间戳，如：1629845600000【注：仅做未开播时直播状态判断显示，不对讲师开播操作产生影响】 */
    private Long endTime;

    /** 子账号邮箱，填写时频道会创建在该子账号下（子账号不能被删除或者禁用），暂无法通过接口获取 */
    private String subAccount;

    /** 自定义讲师ID，32个以内ASCII码可见字符 */
    private String customTeacherId;


}

