package com.example.accounting.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecordQueryDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer type;
    private String category;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}