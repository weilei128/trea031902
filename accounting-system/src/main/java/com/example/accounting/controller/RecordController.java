package com.example.accounting.controller;

import com.example.accounting.common.PageResult;
import com.example.accounting.common.Result;
import com.example.accounting.dto.RecordAddDTO;
import com.example.accounting.dto.RecordQueryDTO;
import com.example.accounting.dto.RecordUpdateDTO;
import com.example.accounting.entity.Record;
import com.example.accounting.service.RecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "记账模块")
@RestController
@RequestMapping("/api/record")
public class RecordController {

    @Resource
    private RecordService recordService;

    @ApiOperation("添加收支记录")
    @PostMapping
    public Result<Void> addRecord(@RequestBody @Validated RecordAddDTO dto) {
        recordService.addRecord(dto);
        return Result.success();
    }

    @ApiOperation("查询收支记录")
    @GetMapping
    public Result<PageResult<Record>> queryRecords(RecordQueryDTO dto) {
        PageResult<Record> result = recordService.queryRecords(dto);
        return Result.success(result);
    }

    @ApiOperation("修改收支记录")
    @PutMapping("/{id}")
    public Result<Void> updateRecord(@PathVariable Long id, @RequestBody RecordUpdateDTO dto) {
        recordService.updateRecord(id, dto);
        return Result.success();
    }

    @ApiOperation("删除收支记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return Result.success();
    }
}