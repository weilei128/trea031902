package com.example.accounting.controller;

import com.example.accounting.common.Result;
import com.example.accounting.dto.ChangePasswordDTO;
import com.example.accounting.dto.UserLoginDTO;
import com.example.accounting.dto.UserRegisterDTO;
import com.example.accounting.entity.User;
import com.example.accounting.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Validated UserRegisterDTO dto) {
        userService.register(dto.getUsername(), dto.getPassword(), dto.getPhone(), dto.getEmail());
        return Result.success();
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Void> login(@RequestBody @Validated UserLoginDTO dto, HttpSession session) {
        User user = userService.login(dto.getAccount(), dto.getPassword());
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        return Result.success();
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PostMapping("/changePassword")
    public Result<Void> changePassword(@RequestBody @Validated ChangePasswordDTO dto) {
        userService.changePassword(dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }
}