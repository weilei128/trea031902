package com.example.accounting.controller;

import com.example.accounting.common.Result;
import com.example.accounting.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

@Api(tags = "统计模块")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @ApiOperation("获取本周统计数据")
    @GetMapping("/weekly")
    public Result<Map<String, Object>> getWeeklyStatistics() {
        Map<String, Object> result = statisticsService.getWeeklyStatistics();
        return Result.success(result);
    }

    @ApiOperation("获取本月统计数据")
    @GetMapping("/monthly")
    public Result<Map<String, Object>> getMonthlyStatistics() {
        Map<String, Object> result = statisticsService.getMonthlyStatistics();
        return Result.success(result);
    }

    @ApiOperation("获取自定义时间范围统计数据")
    @GetMapping("/custom")
    public Result<Map<String, Object>> getCustomStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> result = statisticsService.getCustomStatistics(startTime, endTime);
        return Result.success(result);
    }
}