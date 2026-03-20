package com.accounting.service.impl;

import com.accounting.config.AppConfig;
import com.accounting.dto.*;
import com.accounting.entity.Transaction;
import com.accounting.exception.BusinessException;
import com.accounting.service.TransactionService;
import com.accounting.util.CsvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 记账服务实现类
 */
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private CsvUtil csvUtil;

    @Autowired
    private AppConfig appConfig;

    private String transactionFilePath;
    private final AtomicLong idGenerator = new AtomicLong(0);

    private static final String[] TRANSACTION_HEADERS = {"id", "userId", "amount", "type", "category", "remark", "createTime", "updateTime"};
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 预定义分类
    private static final Map<String, List<String>> CATEGORIES = new HashMap<>();

    static {
        CATEGORIES.put("INCOME", Arrays.asList("薪资", "奖金", "投资", "兼职", "红包", "其他收入"));
        CATEGORIES.put("EXPENSE", Arrays.asList("餐饮", "交通", "购物", "娱乐", "住房", "医疗", "教育", "通讯", "其他支出"));
    }

    @PostConstruct
    public void init() {
        transactionFilePath = appConfig.getDataPath() + "/" + appConfig.getTransactionFile();
        loadMaxId();
    }

    private void loadMaxId() {
        List<Transaction> transactions = loadAllTransactions();
        long maxId = transactions.stream()
                .mapToLong(Transaction::getId)
                .max()
                .orElse(0L);
        idGenerator.set(maxId);
    }

    private List<Transaction> loadAllTransactions() {
        return csvUtil.readCsv(transactionFilePath, true, this::parseTransaction);
    }

    private Transaction parseTransaction(CSVRecord record) {
        try {
            Transaction transaction = new Transaction();
            transaction.setId(Long.parseLong(record.get(0)));
            transaction.setUserId(Long.parseLong(record.get(1)));
            transaction.setAmount(new BigDecimal(record.get(2)));
            transaction.setType(record.get(3));
            transaction.setCategory(record.get(4));
            transaction.setRemark(record.get(5));
            transaction.setCreateTime(LocalDateTime.parse(record.get(6), DATE_TIME_FORMATTER));
            transaction.setUpdateTime(LocalDateTime.parse(record.get(7), DATE_TIME_FORMATTER));
            return transaction;
        } catch (Exception e) {
            log.error("解析交易记录失败", e);
            return null;
        }
    }

    private String[] toCsvRecord(Transaction transaction) {
        return new String[]{
                String.valueOf(transaction.getId()),
                String.valueOf(transaction.getUserId()),
                transaction.getAmount().toString(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getRemark() != null ? transaction.getRemark() : "",
                transaction.getCreateTime().format(DATE_TIME_FORMATTER),
                transaction.getUpdateTime().format(DATE_TIME_FORMATTER)
        };
    }

    private TransactionVO toVO(Transaction transaction) {
        TransactionVO vo = new TransactionVO();
        BeanUtils.copyProperties(transaction, vo);
        vo.setTypeName("INCOME".equals(transaction.getType()) ? "收入" : "支出");
        return vo;
    }

    @Override
    public TransactionVO addTransaction(Long userId, TransactionDTO dto) {
        // 验证分类是否有效
        validateCategory(dto.getType(), dto.getCategory());

        Transaction transaction = new Transaction();
        transaction.setId(idGenerator.incrementAndGet());
        transaction.setUserId(userId);
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setCategory(dto.getCategory());
        transaction.setRemark(dto.getRemark());
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setUpdateTime(LocalDateTime.now());

        csvUtil.appendToCsv(transactionFilePath, toCsvRecord(transaction));

        log.info("添加收支记录成功: userId={}, id={}", userId, transaction.getId());
        return toVO(transaction);
    }

    @Override
    public TransactionVO updateTransaction(Long userId, Long transactionId, TransactionDTO dto) {
        // 验证分类是否有效
        validateCategory(dto.getType(), dto.getCategory());

        List<Transaction> transactions = loadAllTransactions();

        Transaction transaction = transactions.stream()
                .filter(t -> t.getId().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("记录不存在"));

        // 验证权限
        if (!transaction.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此记录");
        }

        // 更新记录
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setCategory(dto.getCategory());
        transaction.setRemark(dto.getRemark());
        transaction.setUpdateTime(LocalDateTime.now());

        // 保存所有记录
        List<String[]> records = transactions.stream()
                .map(this::toCsvRecord)
                .collect(java.util.stream.Collectors.toList());
        csvUtil.overwriteCsv(transactionFilePath, TRANSACTION_HEADERS, records);

        log.info("更新收支记录成功: userId={}, id={}", userId, transactionId);
        return toVO(transaction);
    }

    @Override
    public void deleteTransaction(Long userId, Long transactionId) {
        List<Transaction> transactions = loadAllTransactions();

        Transaction transaction = transactions.stream()
                .filter(t -> t.getId().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("记录不存在"));

        // 验证权限
        if (!transaction.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作此记录");
        }

        // 删除记录
        transactions.remove(transaction);

        // 保存所有记录
        List<String[]> records = transactions.stream()
                .map(this::toCsvRecord)
                .collect(java.util.stream.Collectors.toList());
        csvUtil.overwriteCsv(transactionFilePath, TRANSACTION_HEADERS, records);

        log.info("删除收支记录成功: userId={}, id={}", userId, transactionId);
    }

    @Override
    public TransactionVO getTransaction(Long userId, Long transactionId) {
        Transaction transaction = loadAllTransactions().stream()
                .filter(t -> t.getId().equals(transactionId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("记录不存在"));

        // 验证权限
        if (!transaction.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看此记录");
        }

        return toVO(transaction);
    }

    @Override
    public PageResult<TransactionVO> listTransactions(Long userId, TransactionQueryDTO dto) {
        List<Transaction> allTransactions = loadAllTransactions().stream()
                .filter(t -> t.getUserId().equals(userId))
                .filter(t -> dto.getType() == null || t.getType().equals(dto.getType()))
                .filter(t -> dto.getCategory() == null || t.getCategory().equals(dto.getCategory()))
                .filter(t -> dto.getStartTime() == null || !t.getCreateTime().isBefore(dto.getStartTime()))
                .filter(t -> dto.getEndTime() == null || !t.getCreateTime().isAfter(dto.getEndTime()))
                .sorted(Comparator.comparing(Transaction::getCreateTime).reversed())
                .collect(java.util.stream.Collectors.toList());

        // 分页
        int total = allTransactions.size();
        int start = (dto.getPageNum() - 1) * dto.getPageSize();
        int end = Math.min(start + dto.getPageSize(), total);

        List<TransactionVO> list = allTransactions.subList(start, end).stream()
                .map(this::toVO)
                .collect(java.util.stream.Collectors.toList());

        return new PageResult<>(dto.getPageNum(), dto.getPageSize(), (long) total, list);
    }

    @Override
    public StatisticsVO getStatistics(Long userId, String startTime, String endTime) {
        LocalDateTime start = startTime != null ? LocalDateTime.parse(startTime, DATE_TIME_FORMATTER) : null;
        LocalDateTime end = endTime != null ? LocalDateTime.parse(endTime, DATE_TIME_FORMATTER) : null;

        List<Transaction> transactions = loadAllTransactions().stream()
                .filter(t -> t.getUserId().equals(userId))
                .filter(t -> start == null || !t.getCreateTime().isBefore(start))
                .filter(t -> end == null || !t.getCreateTime().isAfter(end))
                .collect(java.util.stream.Collectors.toList());

        // 计算总收入和总支出
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 按分类统计收入
        Map<String, BigDecimal> incomeMap = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        // 按分类统计支出
        Map<String, BigDecimal> expenseMap = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        StatisticsVO vo = new StatisticsVO();
        vo.setTotalIncome(totalIncome);
        vo.setTotalExpense(totalExpense);
        vo.setBalance(totalIncome.subtract(totalExpense));

        // 计算收入分类占比
        vo.setIncomeByCategory(incomeMap.entrySet().stream()
                .map(e -> {
                    StatisticsVO.CategoryStat stat = new StatisticsVO.CategoryStat();
                    stat.setCategory(e.getKey());
                    stat.setAmount(e.getValue());
                    stat.setPercentage(totalIncome.compareTo(BigDecimal.ZERO) > 0
                            ? e.getValue().multiply(new BigDecimal("100")).divide(totalIncome, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO);
                    return stat;
                })
                .sorted(Comparator.comparing(StatisticsVO.CategoryStat::getAmount).reversed())
                .collect(java.util.stream.Collectors.toList()));

        // 计算支出分类占比
        vo.setExpenseByCategory(expenseMap.entrySet().stream()
                .map(e -> {
                    StatisticsVO.CategoryStat stat = new StatisticsVO.CategoryStat();
                    stat.setCategory(e.getKey());
                    stat.setAmount(e.getValue());
                    stat.setPercentage(totalExpense.compareTo(BigDecimal.ZERO) > 0
                            ? e.getValue().multiply(new BigDecimal("100")).divide(totalExpense, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO);
                    return stat;
                })
                .sorted(Comparator.comparing(StatisticsVO.CategoryStat::getAmount).reversed())
                .collect(java.util.stream.Collectors.toList()));

        return vo;
    }

    @Override
    public List<String> getCategories(String type) {
        return CATEGORIES.getOrDefault(type, new ArrayList<>());
    }

    /**
     * 验证分类是否有效
     */
    private void validateCategory(String type, String category) {
        List<String> validCategories = CATEGORIES.get(type);
        if (validCategories == null || !validCategories.contains(category)) {
            throw new BusinessException("无效的分类: " + category + "，有效分类: " + validCategories);
        }
    }
}
