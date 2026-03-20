package com.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收支记录响应VO
 */
@Data
public class TransactionVO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 类型：INCOME-收入，EXPENSE-支出
     */
    private String type;

    /**
     * 类型中文名称
     */
    private String typeName;

    /**
     * 分类
     */
    private String category;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
