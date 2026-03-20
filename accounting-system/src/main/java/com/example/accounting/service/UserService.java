package com.example.accounting.service;

import com.example.accounting.common.Constants;
import com.example.accounting.common.UserContext;
import com.example.accounting.entity.User;
import com.example.accounting.util.CsvUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UserService {

    public void register(String username, String password, String phone, String email) {
        User existUser = CsvUtils.findOne(Constants.USER_FILE, User.class,
                u -> u.getUsername().equals(username)
                        || (phone != null && phone.equals(u.getPhone()))
                        || (email != null && email.equals(u.getEmail())));
        if (existUser != null) {
            throw new RuntimeException("用户名、手机号或邮箱已存在");
        }

        User user = new User();
        user.setId(CsvUtils.generateUserId());
        user.setUsername(username);
        user.setPassword(encryptPassword(password));
        user.setPhone(phone);
        user.setEmail(email);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        CsvUtils.write(Constants.USER_FILE, user, User.class);
    }

    public User login(String account, String password) {
        User user = CsvUtils.findOne(Constants.USER_FILE, User.class,
                u -> u.getUsername().equals(account)
                        || (u.getPhone() != null && u.getPhone().equals(account))
                        || (u.getEmail() != null && u.getEmail().equals(account)));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(encryptPassword(password))) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    public void changePassword(String oldPassword, String newPassword) {
        Long userId = UserContext.getUserId();
        User user = CsvUtils.findOne(Constants.USER_FILE, User.class, u -> u.getId().equals(userId));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(encryptPassword(oldPassword))) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(encryptPassword(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        CsvUtils.update(Constants.USER_FILE, user, User.class, u -> u.getId().equals(userId));
    }

    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }
}