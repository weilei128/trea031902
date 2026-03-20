package com.accounting.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页查询结果封装类
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 当前页数据列表
     */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}
