package com.scnujxjy.backendpoint.model.ro.core_data;

import java.io.Serializable;
import java.util.List;


public class PageBeanResult<T> implements Serializable {
    private static final long serialVersionUID = 8656597559014685635L;
    /**
     * 总条数
     */
    private Long counts;
    /**
     * 数据
     */
    private List<?> rows;
    /**
     * 当前页
     */
    private Integer currentPage;


    public PageBeanResult() {
    }

    public PageBeanResult(Long counts, List<?> rows, Integer currentPage) {
        this.counts = counts;
        this.rows = rows;
        this.currentPage = currentPage;
    }

    public PageBeanResult(Long counts, List<?> rows) {
        this.counts = counts.longValue();
        this.rows = rows;
    }


    public Long getCounts() {
        return counts;
    }

    public void setCounts(Long counts) {
        this.counts = counts;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

}
