package com.example.accounting.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class CsvUtils {

    private static final String DATA_DIR = "data";
    private static final AtomicLong userIdGenerator = new AtomicLong(0);
    private static final AtomicLong recordIdGenerator = new AtomicLong(0);

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            initIdGenerators();
        } catch (Exception e) {
            log.error("初始化数据目录失败", e);
        }
    }

    private static void initIdGenerators() {
        try {
            File userFile = new File(DATA_DIR + "/users.csv");
            if (userFile.exists()) {
                List<String> lines = Files.readAllLines(Paths.get(DATA_DIR + "/users.csv"));
                if (lines.size() > 1) {
                    String lastLine = lines.get(lines.size() - 1);
                    String[] fields = lastLine.split(",");
                    if (fields.length > 0 && !fields[0].equals("id")) {
                        userIdGenerator.set(Long.parseLong(fields[0]));
                    }
                }
            }

            File recordFile = new File(DATA_DIR + "/records.csv");
            if (recordFile.exists()) {
                List<String> lines = Files.readAllLines(Paths.get(DATA_DIR + "/records.csv"));
                if (lines.size() > 1) {
                    String lastLine = lines.get(lines.size() - 1);
                    String[] fields = lastLine.split(",");
                    if (fields.length > 0 && !fields[0].equals("id")) {
                        recordIdGenerator.set(Long.parseLong(fields[0]));
                    }
                }
            }
        } catch (Exception e) {
            log.error("初始化ID生成器失败", e);
        }
    }

    public static synchronized long generateUserId() {
        return userIdGenerator.incrementAndGet();
    }

    public static synchronized long generateRecordId() {
        return recordIdGenerator.incrementAndGet();
    }

    public static <T> void write(String fileName, T entity, Class<T> clazz) {
        List<T> list = readAll(fileName, clazz);
        list.add(entity);
        writeAll(fileName, list, clazz);
    }

    public static <T> void writeAll(String fileName, List<T> list, Class<T> clazz) {
        String filePath = DATA_DIR + "/" + fileName;
        try (Writer writer = new FileWriter(filePath)) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withSeparator(',')
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            beanToCsv.write(list);
        } catch (Exception e) {
            log.error("写入CSV文件失败: {}", filePath, e);
            throw new RuntimeException("写入数据失败");
        }
    }

    public static <T> List<T> readAll(String fileName, Class<T> clazz) {
        String filePath = DATA_DIR + "/" + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
                strategy.setType(clazz);
                CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                        .withMappingStrategy(strategy)
                        .build();
                return csvToBean.parse();
            } catch (Exception e) {
                log.error("读取CSV文件失败: {}", filePath, e);
            }
        }
        return new ArrayList<>();
    }

    public static <T> void update(String fileName, T entity, Class<T> clazz, java.util.function.Predicate<T> predicate) {
        List<T> list = readAll(fileName, clazz);
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) {
                list.set(i, entity);
                break;
            }
        }
        writeAll(fileName, list, clazz);
    }

    public static <T> void delete(String fileName, Class<T> clazz, java.util.function.Predicate<T> predicate) {
        List<T> list = readAll(fileName, clazz);
        list.removeIf(predicate);
        writeAll(fileName, list, clazz);
    }

    public static <T> T findOne(String fileName, Class<T> clazz, java.util.function.Predicate<T> predicate) {
        List<T> list = readAll(fileName, clazz);
        return list.stream().filter(predicate).findFirst().orElse(null);
    }

    public static <T> List<T> findList(String fileName, Class<T> clazz, java.util.function.Predicate<T> predicate) {
        List<T> list = readAll(fileName, clazz);
        return list.stream().filter(predicate).collect(java.util.stream.Collectors.toList());
    }

    public static <T> List<T> findPage(String fileName, Class<T> clazz, java.util.function.Predicate<T> predicate,
                                      int pageNum, int pageSize, Comparator<T> comparator) {
        List<T> list = findList(fileName, clazz, predicate);
        if (comparator != null) {
            list.sort(comparator);
        }
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start >= list.size()) {
            return Collections.emptyList();
        }
        return list.subList(start, end);
    }

    public static <T> long count(String fileName, Class<T> clazz, java.util.function.Predicate<T> predicate) {
        return findList(fileName, clazz, predicate).size();
    }
}