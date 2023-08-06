package com.scnujxjy.backendpoint.model.ro;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PageRO<T> {

    /**
     * 条件实体
     */
    T entity;
    /**
     * 页码
     */
    private Long pageNumber = 0L;
    /**
     * 一页数据量
     */
    private Long pageSize = 10L;
    /**
     * 排序字段
     */
    private String orderBy;
    /**
     * 排序方式: ASC, DESC
     */
    private String orderType = "ASC";

    /**
     * 是否获取所有数据：默认否
     */
    private Boolean isAll = false;

    /**
     * 模糊搜索的关键词
     */
    private String keyword;

    /**
     * 获取一个分页对象
     *
     * @return
     */
    public <M> Page<M> getPage() {
        Page<M> pages = new Page<>(pageNumber, pageSize);
        pages.addOrder(isAsc() ? OrderItem.asc(orderBy) : OrderItem.desc(orderBy));
        return pages;

    }

    public String getOrderType() {
        return orderType.toUpperCase();
    }

    public Boolean isAsc() {
        return getOrderType().contains("ASC");
    }
}
