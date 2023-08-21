package com.scnujxjy.backendpoint.model.bo.video_stream;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ChannelRequestBO {

    /**
     * 频道名称
     */
    String name;

    /**
     * 讲师进入直播密码
     */
    String channelPasswd;

    /**
     * 连麦人数，-1=<取值范围<=账号级的连麦人数，-1：表示使用账号默认的连麦人数，最大16人（注：账号级连麦人数需通知平台管理员设置才生效）
     */
    Integer linkMicLimit;

    /**
     * 是否为无延时直播，Y 表示开启，默认为N
     */
    String pureRtcEnabled;

    /**
     * 主持人名称
     */
    String publisher;

    /**
     * 直播开始时间，13位时间戳，设置为0 表示关闭直播开始时间显示
     */
    Long startTime;

    /**
     * 直播介绍
     */
    String desc;

    /**
     * 讲师昵称
     */
    String nickname;

    /**
     * 讲师头衔：讲师昵称不为空时必填
     */
    String actor;

    /**
     * 文件
     */
    File file;

    /**
     * 子频道申请信息
     */
    List<SonChannelRequestBO> sonChannelRequestBOS;

}
