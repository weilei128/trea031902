package com.accounting.dto;

import javax.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收支记录查询请求DTO
 */
@Data
public class TransactionQueryDTO {

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 类型：INCOME-收入，EXPENSE-支出
     */
    private String type;

    /**
     * 分类
     */
    private String category;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于等于1")
    private Integer pageSize = 10;
}
