package com.scnujxjy.backendpoint.dao.entity.video_stream;

import lombok.Data;

import java.util.List;

@Data
public class ViewLogSecondResponse {
    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalItems;
    private List<ViewLogThirdResponse> contents;

    private Integer startRow;
    private Boolean firstPage;
    private Boolean lastPage;
    private Integer prePageNumber;
    private Integer nextPageNumber;
    private Integer limit;
    private Integer totalPages;
    private Integer endRow;
    private Integer offset;

}
