package com.scnujxjy.backendpoint.model.vo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        if (Objects.isNull(tPage)) {
            tPage = new Page<>();
        }
        this.pages = tPage.getPages();
        this.size = tPage.getSize();
        this.current = tPage.getCurrent();
        this.total = tPage.getTotal();
        this.records = data;
    }

    public <M> PageVO(PageRO<M> pageRO, Long total, List<T> data) {
        if (Objects.isNull(pageRO)) {
            pageRO = new PageRO<>();
        }
        this.pages = NumberUtil.div(total, pageRO.getPageSize(), 0, RoundingMode.HALF_UP).longValue();
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

    public static Boolean isPageVONull(PageVO pageVO) {
        if (Objects.isNull(pageVO)
                || CollUtil.isEmpty(pageVO.getRecords())) {
            return true;
        }
        return false;
    }
}
