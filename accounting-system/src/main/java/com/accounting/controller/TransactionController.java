package com.accounting.controller;

import com.accounting.dto.*;
import com.accounting.service.TransactionService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 记账控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * 添加收支记录
     *
     * @param request 请求对象
     * @param dto     记录信息
     * @return 添加结果
     */
    @PostMapping
    public Result<TransactionVO> addTransaction(HttpServletRequest request,
                                                @Valid @RequestBody TransactionDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("添加收支记录: userId={}", userId);
        TransactionVO vo = transactionService.addTransaction(userId, dto);
        return Result.success("添加成功", vo);
    }

    /**
     * 修改收支记录
     *
     * @param request       请求对象
     * @param transactionId 记录ID
     * @param dto           记录信息
     * @return 修改结果
     */
    @PutMapping("/{id}")
    public Result<TransactionVO> updateTransaction(HttpServletRequest request,
                                                   @PathVariable("id") Long transactionId,
                                                   @Valid @RequestBody TransactionDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("修改收支记录: userId={}, transactionId={}", userId, transactionId);
        TransactionVO vo = transactionService.updateTransaction(userId, transactionId, dto);
        return Result.success("修改成功", vo);
    }

    /**
     * 删除收支记录
     *
     * @param request       请求对象
     * @param transactionId 记录ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTransaction(HttpServletRequest request,
                                          @PathVariable("id") Long transactionId) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除收支记录: userId={}, transactionId={}", userId, transactionId);
        transactionService.deleteTransaction(userId, transactionId);
        return Result.success("删除成功", null);
    }

    /**
     * 查询收支记录详情
     *
     * @param request       请求对象
     * @param transactionId 记录ID
     * @return 记录详情
     */
    @GetMapping("/{id}")
    public Result<TransactionVO> getTransaction(HttpServletRequest request,
                                                @PathVariable("id") Long transactionId) {
        Long userId = (Long) request.getAttribute("userId");
        TransactionVO vo = transactionService.getTransaction(userId, transactionId);
        return Result.success(vo);
    }

    /**
     * 分页查询收支记录
     *
     * @param request 请求对象
     * @param dto     查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageResult<TransactionVO>> listTransactions(HttpServletRequest request,
                                                              @Valid TransactionQueryDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        PageResult<TransactionVO> result = transactionService.listTransactions(userId, dto);
        return Result.success(result);
    }

    /**
     * 获取统计数据
     *
     * @param request   请求对象
     * @param startTime 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param endTime   结束时间（格式：yyyy-MM-dd HH:mm:ss）
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public Result<StatisticsVO> getStatistics(HttpServletRequest request,
                                              @RequestParam(required = false) String startTime,
                                              @RequestParam(required = false) String endTime) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("获取统计数据: userId={}, startTime={}, endTime={}", userId, startTime, endTime);
        StatisticsVO vo = transactionService.getStatistics(userId, startTime, endTime);
        return Result.success(vo);
    }

    /**
     * 获取分类列表
     *
     * @param type 类型：INCOME-收入，EXPENSE-支出
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<String>> getCategories(@RequestParam String type) {
        List<String> categories = transactionService.getCategories(type);
        return Result.success(categories);
    }
}
