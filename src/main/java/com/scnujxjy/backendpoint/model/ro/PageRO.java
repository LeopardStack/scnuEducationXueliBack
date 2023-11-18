package com.scnujxjy.backendpoint.model.ro;

import cn.hutool.core.util.StrUtil;
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

    public static final String ORDER_FORMAT = " order by %s %s ";

    /**
     * 条件实体
     */
    T entity;
    /**
     * 页码
     */
    private Long pageNumber = 1L;
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
     * @return 返回分页对象
     */
    public <M> Page<M> getPage() {
        return new Page<>(pageNumber, pageSize);
    }

    /**
     * 获取排序类型：ASC-升序，DESC-降序
     *
     * @return ASC-升序，DESC-降序
     */
    public String getOrderType() {
        return orderType.toUpperCase();
    }

    /**
     * 获取排序SQL
     *
     * @return 排序SQL
     */
    public String lastOrderSql() {
        if (StrUtil.isBlank(orderBy)) {
            return "";
        }
        return String.format(ORDER_FORMAT, orderBy, getOrderType());
    }
}
