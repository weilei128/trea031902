package com.accounting.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * CSV文件操作工具类
 */
@Slf4j
@Component
public class CsvUtil {

    /**
     * 写入CSV文件
     *
     * @param filePath 文件路径
     * @param headers  表头
     * @param records  数据记录
     */
    public void writeCsv(String filePath, String[] headers, List<String[]> records) {
        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                         .setHeader(headers)
                         .build())) {
                for (String[] record : records) {
                    csvPrinter.printRecord((Object[]) record);
                }
                csvPrinter.flush();
            }
        } catch (IOException e) {
            log.error("写入CSV文件失败: {}", filePath, e);
            throw new RuntimeException("写入CSV文件失败", e);
        }
    }

    /**
     * 追加写入CSV文件（不包含表头）
     *
     * @param filePath 文件路径
     * @param record   数据记录
     */
    public void appendToCsv(String filePath, String[] record) {
        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());
            boolean fileExists = Files.exists(path);

            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                csvPrinter.printRecord((Object[]) record);
                csvPrinter.flush();
            }
        } catch (IOException e) {
            log.error("追加CSV文件失败: {}", filePath, e);
            throw new RuntimeException("追加CSV文件失败", e);
        }
    }

    /**
     * 读取CSV文件
     *
     * @param filePath  文件路径
     * @param hasHeader 是否有表头
     * @param mapper    数据映射函数
     * @param <T>       返回类型
     * @return 数据列表
     */
    public <T> List<T> readCsv(String filePath, boolean hasHeader, Function<CSVRecord, T> mapper) {
        List<T> result = new ArrayList<>();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            return result;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setSkipHeaderRecord(hasHeader)
                     .build())) {
            for (CSVRecord record : csvParser) {
                T item = mapper.apply(record);
                if (item != null) {
                    result.add(item);
                }
            }
        } catch (IOException e) {
            log.error("读取CSV文件失败: {}", filePath, e);
            throw new RuntimeException("读取CSV文件失败", e);
        }
        return result;
    }

    /**
     * 覆盖写入CSV文件
     *
     * @param filePath 文件路径
     * @param headers  表头
     * @param records  数据记录
     */
    public void overwriteCsv(String filePath, String[] headers, List<String[]> records) {
        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());
            if (Files.exists(path)) {
                Files.delete(path);
            }
            writeCsv(filePath, headers, records);
        } catch (IOException e) {
            log.error("覆盖写入CSV文件失败: {}", filePath, e);
            throw new RuntimeException("覆盖写入CSV文件失败", e);
        }
    }
}
