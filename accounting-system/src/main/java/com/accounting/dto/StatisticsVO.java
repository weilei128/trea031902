package com.accounting.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 统计数据响应VO
 */
@Data
public class StatisticsVO {

    /**
     * 总收入
     */
    private BigDecimal totalIncome;

    /**
     * 总支出
     */
    private BigDecimal totalExpense;

    /**
     * 结余
     */
    private BigDecimal balance;

    /**
     * 收入分类统计
     */
    private List<CategoryStat> incomeByCategory;

    /**
     * 支出分类统计
     */
    private List<CategoryStat> expenseByCategory;

    /**
     * 分类统计项
     */
    @Data
    public static class CategoryStat {
        /**
         * 分类名称
         */
        private String category;

        /**
         * 金额
         */
        private BigDecimal amount;

        /**
         * 占比（百分比）
         */
        private BigDecimal percentage;
    }
}
