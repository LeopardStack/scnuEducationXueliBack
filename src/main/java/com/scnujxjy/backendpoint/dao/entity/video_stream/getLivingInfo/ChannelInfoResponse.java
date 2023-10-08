package com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo;

import lombok.Data;
import java.util.List;

@Data
public class ChannelInfoResponse {
    private Integer code;
    private String status;
    private Boolean success;
    private String requestId;
    private Object error;
    private ChannelDetail data; // 修改这里
}
