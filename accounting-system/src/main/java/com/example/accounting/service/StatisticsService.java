package com.example.accounting.service;

import com.example.accounting.common.Constants;
import com.example.accounting.common.UserContext;
import com.example.accounting.entity.Record;
import com.example.accounting.util.CsvUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    public Map<String, Object> getWeeklyStatistics() {
        Long userId = UserContext.getUserId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).withHour(23).withMinute(59).withSecond(59);

        return calculateStatistics(userId, weekStart, weekEnd);
    }

    public Map<String, Object> getMonthlyStatistics() {
        Long userId = UserContext.getUserId();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime monthEnd = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);

        return calculateStatistics(userId, monthStart, monthEnd);
    }

    public Map<String, Object> getCustomStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Long userId = UserContext.getUserId();
        return calculateStatistics(userId, startTime, endTime);
    }

    private Map<String, Object> calculateStatistics(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Record> records = CsvUtils.findList(Constants.RECORD_FILE, Record.class,
                r -> r.getUserId().equals(userId)
                        && !r.getCreateTime().isBefore(start)
                        && !r.getCreateTime().isAfter(end));

        BigDecimal totalIncome = records.stream()
                .filter(r -> r.getType() == Constants.TYPE_INCOME)
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = records.stream()
                .filter(r -> r.getType() == Constants.TYPE_EXPENSE)
                .map(Record::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> incomeByCategory = records.stream()
                .filter(r -> r.getType() == Constants.TYPE_INCOME)
                .collect(Collectors.groupingBy(
                        Record::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Record::getAmount, BigDecimal::add)
                ));

        Map<String, BigDecimal> expenseByCategory = records.stream()
                .filter(r -> r.getType() == Constants.TYPE_EXPENSE)
                .collect(Collectors.groupingBy(
                        Record::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Record::getAmount, BigDecimal::add)
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("balance", totalIncome.subtract(totalExpense));
        result.put("incomeByCategory", incomeByCategory);
        result.put("expenseByCategory", expenseByCategory);
        result.put("startTime", start);
        result.put("endTime", end);

        return result;
    }
}