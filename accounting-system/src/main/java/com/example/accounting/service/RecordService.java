package com.example.accounting.service;

import com.example.accounting.common.Constants;
import com.example.accounting.common.PageResult;
import com.example.accounting.common.UserContext;
import com.example.accounting.dto.RecordAddDTO;
import com.example.accounting.dto.RecordQueryDTO;
import com.example.accounting.dto.RecordUpdateDTO;
import com.example.accounting.entity.Record;
import com.example.accounting.util.CsvUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordService {

    public void addRecord(RecordAddDTO dto) {
        validateRecord(dto.getType(), dto.getCategory());

        Long userId = UserContext.getUserId();
        Record record = new Record();
        record.setId(CsvUtils.generateRecordId());
        record.setUserId(userId);
        record.setAmount(dto.getAmount());
        record.setType(dto.getType());
        record.setCategory(dto.getCategory());
        record.setRemark(dto.getRemark());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        CsvUtils.write(Constants.RECORD_FILE, record, Record.class);
    }

    public PageResult<Record> queryRecords(RecordQueryDTO dto) {
        Long userId = UserContext.getUserId();

        List<Record> records = CsvUtils.findList(Constants.RECORD_FILE, Record.class,
                r -> r.getUserId().equals(userId)
                        && (dto.getStartTime() == null || !r.getCreateTime().isBefore(dto.getStartTime()))
                        && (dto.getEndTime() == null || !r.getCreateTime().isAfter(dto.getEndTime()))
                        && (dto.getType() == null || r.getType().equals(dto.getType()))
                        && (!StringUtils.hasText(dto.getCategory()) || r.getCategory().equals(dto.getCategory())));

        records.sort(Comparator.comparing(Record::getCreateTime).reversed());

        int pageNum = dto.getPageNum() == null ? 1 : dto.getPageNum();
        int pageSize = dto.getPageSize() == null ? 10 : dto.getPageSize();

        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, records.size());
        List<Record> pageRecords = start >= records.size() ? Collections.emptyList() : records.subList(start, end);

        return new PageResult<>(pageRecords, records.size(), pageNum, pageSize);
    }

    public void updateRecord(Long id, RecordUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        Record record = CsvUtils.findOne(Constants.RECORD_FILE, Record.class,
                r -> r.getId().equals(id) && r.getUserId().equals(userId));
        if (record == null) {
            throw new RuntimeException("记录不存在或无权限修改");
        }

        if (dto.getAmount() != null) {
            record.setAmount(dto.getAmount());
        }
        if (dto.getType() != null) {
            record.setType(dto.getType());
        }
        if (dto.getCategory() != null) {
            validateRecord(record.getType(), dto.getCategory());
            record.setCategory(dto.getCategory());
        }
        if (dto.getRemark() != null) {
            record.setRemark(dto.getRemark());
        }
        record.setUpdateTime(LocalDateTime.now());

        CsvUtils.update(Constants.RECORD_FILE, record, Record.class,
                r -> r.getId().equals(id) && r.getUserId().equals(userId));
    }

    public void deleteRecord(Long id) {
        Long userId = UserContext.getUserId();
        Record record = CsvUtils.findOne(Constants.RECORD_FILE, Record.class,
                r -> r.getId().equals(id) && r.getUserId().equals(userId));
        if (record == null) {
            throw new RuntimeException("记录不存在或无权限删除");
        }

        CsvUtils.delete(Constants.RECORD_FILE, Record.class,
                r -> r.getId().equals(id) && r.getUserId().equals(userId));
    }

    private void validateRecord(Integer type, String category) {
        if (type == null) {
            throw new RuntimeException("类型不能为空");
        }
        if (category == null) {
            throw new RuntimeException("分类不能为空");
        }
        if (!Constants.isValidCategory(type, category)) {
            throw new RuntimeException("分类不合法");
        }
    }
}