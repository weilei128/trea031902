package com.accounting.service;

import com.accounting.dto.UserLoginDTO;
import com.accounting.dto.UserRegisterDTO;
import com.accounting.dto.UserUpdatePasswordDTO;
import com.accounting.dto.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param dto 注册信息
     * @return 用户信息
     */
    UserVO register(UserRegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录信息
     * @return 用户信息（包含token）
     */
    UserVO login(UserLoginDTO dto);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto    密码信息
     */
    void updatePassword(Long userId, UserUpdatePasswordDTO dto);

    /**
     * 根据ID查询用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
}
