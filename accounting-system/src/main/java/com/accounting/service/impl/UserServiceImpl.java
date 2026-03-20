package com.accounting.service.impl;

import com.accounting.config.AppConfig;
import com.accounting.dto.UserLoginDTO;
import com.accounting.dto.UserRegisterDTO;
import com.accounting.dto.UserUpdatePasswordDTO;
import com.accounting.dto.UserVO;
import com.accounting.entity.User;
import com.accounting.exception.BusinessException;
import com.accounting.service.UserService;
import com.accounting.util.CsvUtil;
import com.accounting.util.JwtUtil;
import com.accounting.util.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private CsvUtil csvUtil;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppConfig appConfig;

    private String userFilePath;
    private final AtomicLong idGenerator = new AtomicLong(0);

    private static final String[] USER_HEADERS = {"id", "username", "password", "nickname", "createTime", "updateTime"};
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void init() {
        userFilePath = appConfig.getDataPath() + "/" + appConfig.getUserFile();
        loadMaxId();
    }

    /**
     * 加载最大ID
     */
    private void loadMaxId() {
        List<User> users = loadAllUsers();
        long maxId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L);
        idGenerator.set(maxId);
    }

    /**
     * 加载所有用户
     */
    private List<User> loadAllUsers() {
        return csvUtil.readCsv(userFilePath, true, this::parseUser);
    }

    /**
     * 解析CSV记录为用户对象
     */
    private User parseUser(CSVRecord record) {
        try {
            User user = new User();
            user.setId(Long.parseLong(record.get(0)));
            user.setUsername(record.get(1));
            user.setPassword(record.get(2));
            user.setNickname(record.get(3));
            user.setCreateTime(LocalDateTime.parse(record.get(4), DATE_TIME_FORMATTER));
            user.setUpdateTime(LocalDateTime.parse(record.get(5), DATE_TIME_FORMATTER));
            return user;
        } catch (Exception e) {
            log.error("解析用户记录失败", e);
            return null;
        }
    }

    /**
     * 将用户对象转为CSV记录
     */
    private String[] toCsvRecord(User user) {
        return new String[]{
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getPassword(),
                user.getNickname() != null ? user.getNickname() : "",
                user.getCreateTime().format(DATE_TIME_FORMATTER),
                user.getUpdateTime().format(DATE_TIME_FORMATTER)
        };
    }

    @Override
    public UserVO register(UserRegisterDTO dto) {
        // 校验两次密码是否一致
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        user.setId(idGenerator.incrementAndGet());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordUtil.encrypt(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存到CSV
        csvUtil.appendToCsv(userFilePath, toCsvRecord(user));

        // 转换为VO
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setToken(jwtUtil.generateToken(user.getId()));

        log.info("用户注册成功: {}", dto.getUsername());
        return vo;
    }

    @Override
    public UserVO login(UserLoginDTO dto) {
        // 查找用户
        User user = loadAllUsers().stream()
                .filter(u -> u.getUsername().equals(dto.getUsername()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        // 验证密码
        if (!passwordUtil.verify(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 转换为VO
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setToken(jwtUtil.generateToken(user.getId()));

        log.info("用户登录成功: {}", dto.getUsername());
        return vo;
    }

    @Override
    public void updatePassword(Long userId, UserUpdatePasswordDTO dto) {
        // 校验两次新密码是否一致
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }

        // 加载所有用户
        List<User> users = loadAllUsers();

        // 查找用户
        User user = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 验证旧密码
        if (!passwordUtil.verify(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordUtil.encrypt(dto.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());

        // 保存所有用户
        List<String[]> records = users.stream()
                .map(this::toCsvRecord)
                .collect(java.util.stream.Collectors.toList());
        csvUtil.overwriteCsv(userFilePath, USER_HEADERS, records);

        log.info("用户修改密码成功: {}", user.getUsername());
    }

    @Override
    public UserVO getUserById(Long userId) {
        User user = loadAllUsers().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("用户不存在"));

        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public boolean existsByUsername(String username) {
        return loadAllUsers().stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }
}
