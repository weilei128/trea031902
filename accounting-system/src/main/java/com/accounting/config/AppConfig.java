package com.accounting.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 应用配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    /**
     * 数据文件存储路径
     */
    private String dataPath = "data";

    /**
     * 用户数据文件
     */
    private String userFile = "users.csv";

    /**
     * 记账数据文件
     */
    private String transactionFile = "transactions.csv";
}
