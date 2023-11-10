package com.scnujxjy.backendpoint.dao.entity.video_stream.ViewStudentResponse;

import lombok.Data;


@Data
public class ViewSecondStudentResponse {

    private Content[] contents;//分页数据集合
    private Integer pageNumber;//当前页数，第几页
    private Integer pageSize;//	分页大小
    private Integer totalItems;//总记录数
    private Integer totalPages;//总页数

}
