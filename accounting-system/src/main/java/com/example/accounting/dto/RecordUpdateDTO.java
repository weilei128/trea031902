package com.example.accounting.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
public class RecordUpdateDTO {
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    private Integer type;
    private String category;
    private String remark;
}