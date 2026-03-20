package com.accounting.controller;

import com.accounting.dto.*;
import com.accounting.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     *
     * @param dto 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody UserRegisterDTO dto) {
        log.info("用户注册请求: {}", dto.getUsername());
        UserVO vo = userService.register(dto);
        return Result.success("注册成功", vo);
    }

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody UserLoginDTO dto) {
        log.info("用户登录请求: {}", dto.getUsername());
        UserVO vo = userService.login(dto);
        return Result.success("登录成功", vo);
    }

    /**
     * 修改密码
     *
     * @param request 请求对象（获取用户ID）
     * @param dto     密码信息
     * @return 修改结果
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(HttpServletRequest request,
                                       @Valid @RequestBody UserUpdatePasswordDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("修改密码请求: userId={}", userId);
        userService.updatePassword(userId, dto);
        return Result.success("密码修改成功", null);
    }

    /**
     * 获取当前用户信息
     *
     * @param request 请求对象
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO vo = userService.getUserById(userId);
        return Result.success(vo);
    }
}
