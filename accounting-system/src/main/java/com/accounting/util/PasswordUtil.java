package com.accounting.util;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 密码加密工具类
 */
@Component
public class PasswordUtil {

    private static final String SALT = "accounting_salt_2024";

    /**
     * 加密密码（使用SHA-256）
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    public String encrypt(String password) {
        try {
            String saltedPassword = password + SALT;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     *
     * @param password       原始密码
     * @param hashedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean verify(String password, String hashedPassword) {
        return encrypt(password).equals(hashedPassword);
    }
}
