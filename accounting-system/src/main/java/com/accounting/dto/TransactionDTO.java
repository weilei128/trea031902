package com.accounting.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 收支记录请求DTO
 */
@Data
public class TransactionDTO {

    /**
     * 金额（必须为正数）
     */
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    /**
     * 类型：INCOME-收入，EXPENSE-支出
     */
    @NotBlank(message = "类型不能为空")
    @Pattern(regexp = "^(INCOME|EXPENSE)$", message = "类型必须是INCOME（收入）或EXPENSE（支出）")
    private String type;

    /**
     * 分类
     */
    @NotBlank(message = "分类不能为空")
    private String category;

    /**
     * 备注
     */
    private String remark;
}
