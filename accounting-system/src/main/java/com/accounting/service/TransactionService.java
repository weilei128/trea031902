package com.accounting.service;

import com.accounting.dto.*;

import java.util.List;

/**
 * 记账服务接口
 */
public interface TransactionService {

    /**
     * 添加收支记录
     *
     * @param userId 用户ID
     * @param dto    记录信息
     * @return 记录信息
     */
    TransactionVO addTransaction(Long userId, TransactionDTO dto);

    /**
     * 修改收支记录
     *
     * @param userId        用户ID
     * @param transactionId 记录ID
     * @param dto           记录信息
     * @return 记录信息
     */
    TransactionVO updateTransaction(Long userId, Long transactionId, TransactionDTO dto);

    /**
     * 删除收支记录
     *
     * @param userId        用户ID
     * @param transactionId 记录ID
     */
    void deleteTransaction(Long userId, Long transactionId);

    /**
     * 查询收支记录详情
     *
     * @param userId        用户ID
     * @param transactionId 记录ID
     * @return 记录信息
     */
    TransactionVO getTransaction(Long userId, Long transactionId);

    /**
     * 分页查询收支记录
     *
     * @param userId 用户ID
     * @param dto    查询条件
     * @return 分页结果
     */
    PageResult<TransactionVO> listTransactions(Long userId, TransactionQueryDTO dto);

    /**
     * 获取统计数据
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 统计数据
     */
    StatisticsVO getStatistics(Long userId, String startTime, String endTime);

    /**
     * 获取有效的分类列表
     *
     * @param type 类型：INCOME-收入，EXPENSE-支出
     * @return 分类列表
     */
    List<String> getCategories(String type);
}
