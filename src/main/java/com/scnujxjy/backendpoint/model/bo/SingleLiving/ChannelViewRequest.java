package com.scnujxjy.backendpoint.model.bo.SingleLiving;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class ChannelViewRequest {

    private String channelId;
    private String currentDay;//格式 "2023-11-04"
    private String startTime;//"2023-11-04 23:00"
    private String endTime;//"2023-11-04 23:00"

    private String sessionIds;
    private String page;
    private String pageSize;
    private String param1;//观看用户ID，默认查询全部
    private String param2;//观看用户昵称
    private String param3;//vod：观看回放 live：直播   观看日志类型，默认查询全部
}
