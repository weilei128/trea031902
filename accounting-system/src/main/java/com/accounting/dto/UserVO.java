package com.accounting.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应VO
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * JWT令牌
     */
    private String token;
}
