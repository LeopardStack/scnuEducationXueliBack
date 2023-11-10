package com.scnujxjy.backendpoint.model.bo.SingleLiving;

import lombok.Data;

@Data
public class ChannelViewStudentRequest {
    private String viewerId;
    private String startDate;//开始日期。格式为yyyy-MM-dd
    private String endDate;//结束日期，必须和开始日期在同一个月。格式为yyyy-MM-dd
    private String pageNumber;
    private String pageSize;

    private String sessionId;
}
