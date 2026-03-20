package com.accounting.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 简单的Token工具类（基于内存存储）
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    // 内存中存储token
    private static final Map<String, TokenInfo> TOKEN_MAP = new HashMap<>();

    /**
     * Token信息
     */
    private static class TokenInfo {
        Long userId;
        long expireTime;

        TokenInfo(Long userId, long expireTime) {
            this.userId = userId;
            this.expireTime = expireTime;
        }
    }

    /**
     * 生成Token
     *
     * @param userId 用户ID
     * @return Token
     */
    public String generateToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        long expireTime = System.currentTimeMillis() + expiration;
        TOKEN_MAP.put(token, new TokenInfo(userId, expireTime));
        return token;
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        TokenInfo info = TOKEN_MAP.get(token);
        if (info == null) {
            return null;
        }
        if (System.currentTimeMillis() > info.expireTime) {
            TOKEN_MAP.remove(token);
            return null;
        }
        return info.userId;
    }

    /**
     * 验证Token是否有效
     *
     * @param token Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        TokenInfo info = TOKEN_MAP.get(token);
        if (info == null) {
            return false;
        }
        if (System.currentTimeMillis() > info.expireTime) {
            TOKEN_MAP.remove(token);
            return false;
        }
        return true;
    }

    /**
     * 移除Token
     *
     * @param token Token
     */
    public void removeToken(String token) {
        TOKEN_MAP.remove(token);
    }
}
