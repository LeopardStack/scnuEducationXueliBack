package com.scnujxjy.backendpoint.model.vo;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class PageVO<T> implements Serializable {
    /**
     * 记录
     */
    private List<T> records;
    /**
     * 一页容量
     */
    private Long size;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页数
     */
    private Long pages;
    /**
     * 当前页数
     */
    private Long current;

    public <M> PageVO(Page<M> tPage, List<T> data) {
        this.pages = tPage.getPages();
        this.size = tPage.getSize();
        this.current = tPage.getCurrent();
        this.total = tPage.getTotal();
        this.records = data;
    }

    public <M> PageVO(PageRO<M> pageRO, Long total, List<T> data) {
        this.pages = total / pageRO.getPageSize();
        this.size = pageRO.getPageSize();
        this.current = pageRO.getPageNumber();
        this.total = total;
        this.records = data;
    }

    public PageVO(Long size, Long total, Long pages, Long current, List<T> data) {
        this.size = size;
        this.total = total;
        this.pages = pages;
        this.current = current;
        this.records = data;
    }

    public PageVO(List<T> data) {
        if (CollUtil.isEmpty(data)) {
            data = new ArrayList<>();
        }
        this.size = (long) data.size();
        this.total = (long) data.size();
        this.pages = 1L;
        this.current = 1L;
        this.records = data;
    }
}
