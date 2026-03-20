package com.accounting.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求DTO
 */
@Data
public class UserRegisterDTO {

    /**
     * 用户名（手机号或邮箱）
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^(1[3-9]\\d{9}|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$",
            message = "用户名必须是手机号或邮箱格式")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 昵称（可选）
     */
    private String nickname;
}
