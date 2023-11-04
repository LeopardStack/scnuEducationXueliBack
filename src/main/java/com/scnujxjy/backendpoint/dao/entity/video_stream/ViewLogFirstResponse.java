package com.scnujxjy.backendpoint.dao.entity.video_stream;

import lombok.Data;

@Data
public class ViewLogFirstResponse {

    private Integer code;

    private String status;

    private String message;

    private ViewLogSecondResponse data;

}
