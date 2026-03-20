# 个人收支记账系统

## 项目简介

基于Java + SpringBoot + H5开发的个人收支记账系统，支持用户注册登录、收支记录管理、数据统计分析等功能。

## 技术栈

- 后端：SpringBoot 2.7.12 + Java 8
- 数据存储：CSV文件
- 前端：Vue.js + Vant UI
- 接口文档：Swagger2

## 核心功能

### 用户模块
- 用户注册（支持用户名/手机号/邮箱）
- 用户登录
- 修改密码
- 退出登录

### 记账模块
- 添加收支记录（金额、类型、分类、备注）
- 查询收支记录（时间范围、类型、分类筛选，支持分页）
- 修改收支记录
- 删除收支记录

### 统计模块
- 按周统计收支总额
- 按月统计收支总额
- 按分类统计收支占比
- 自定义时间范围统计

## 项目结构

```
accounting-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── accounting/
│   │   │               ├── AccountingSystemApplication.java  # 启动类
│   │   │               ├── common/                          # 通用类
│   │   │               │   ├── Constants.java               # 常量定义
│   │   │               │   ├── GlobalExceptionHandler.java  # 全局异常处理
│   │   │               │   ├── PageResult.java              # 分页结果封装
│   │   │               │   ├── Result.java                  # 统一响应结果
│   │   │               │   └── UserContext.java             # 用户上下文
│   │   │               ├── config/                          # 配置类
│   │   │               │   ├── SwaggerConfig.java           # Swagger配置
│   │   │               │   └── WebConfig.java               # Web配置
│   │   │               ├── controller/                      # 控制层
│   │   │               │   ├── RecordController.java        # 记账接口
│   │   │               │   ├── StatisticsController.java    # 统计接口
│   │   │               │   └── UserController.java          # 用户接口
│   │   │               ├── dto/                             # 数据传输对象
│   │   │               ├── entity/                          # 实体类
│   │   │               ├── interceptor/                     # 拦截器
│   │   │               ├── service/                         # 业务逻辑层
│   │   │               └── util/                            # 工具类
│   │   └── resources/
│   │       ├── static/
│   │       │   └── index.html                               # H5前端页面
│   │       └── application.properties                       # 配置文件
│   └── test/
├── pom.xml                                                  # Maven配置
└── README.md                                                # 说明文档
```

## 快速开始

### 环境要求
- JDK 1.8 或以上版本
- Maven 3.6 或以上版本

### 运行步骤

1. **克隆或下载项目到本地**

2. **进入项目目录**
   ```bash
   cd accounting-system
   ```

3. **编译项目**
   ```bash
   mvn clean compile -DskipTests
   ```

4. **运行项目**
   ```bash
   mvn spring-boot:run
   ```
   或者打包后运行：
   ```bash
   mvn clean package -DskipTests
   java -jar target/accounting-system-0.0.1-SNAPSHOT.jar
   ```

5. **访问系统**
   - 前端页面：http://localhost:8080
   - Swagger接口文档：http://localhost:8080/swagger-ui.html

### 数据存储

系统使用CSV文件存储数据，数据文件位于`data/`目录下：
- `users.csv` - 用户数据
- `records.csv` - 收支记录数据

首次运行系统会自动创建数据目录和文件。

## 接口说明

### 用户模块接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/user/register` | POST | 用户注册 |
| `/api/user/login` | POST | 用户登录 |
| `/api/user/logout` | POST | 退出登录 |
| `/api/user/changePassword` | POST | 修改密码 |

### 记账模块接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/record` | POST | 添加收支记录 |
| `/api/record` | GET | 查询收支记录 |
| `/api/record/{id}` | PUT | 修改收支记录 |
| `/api/record/{id}` | DELETE | 删除收支记录 |

### 统计模块接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/statistics/weekly` | GET | 获取本周统计数据 |
| `/api/statistics/monthly` | GET | 获取本月统计数据 |
| `/api/statistics/custom` | GET | 获取自定义时间范围统计 |

## 分类配置

系统预设了收支分类，可通过`Constants.java`修改：

**收入分类**：薪资、奖金、投资收益、兼职收入、礼金、其他收入

**支出分类**：餐饮、购物、交通、住房、娱乐、医疗、教育、其他支出

## 统一响应格式

```json
{
    "code": 200,
    "msg": "成功",
    "data": {
        // 返回数据
    }
}
```

- `code`：响应状态码，200表示成功，其他表示失败
- `msg`：响应消息
- `data`：响应数据

## 页面操作说明

1. **注册/登录**
   - 首次使用请先注册账号
   - 注册后使用用户名/手机号/邮箱登录

2. **添加记录**
   - 点击首页右下角的"+"按钮
   - 选择收支类型（收入/支出）
   - 输入金额、选择分类、填写备注（可选）
   - 点击保存

3. **查看记录**
   - 首页显示最近的收支记录
   - 点击记录可查看详情

4. **查看统计**
   - 切换到"统计"页面
   - 可查看本周或本月的收支统计
   - 显示收支总额、余额、各分类占比

5. **个人中心**
   - 切换到"我的"页面
   - 可修改密码或退出登录

## 开发说明

- 后端采用MVC架构，分层清晰
- 统一异常处理，返回友好的错误提示
- 参数校验使用JSR-380注解
- 前端使用Vue.js + Vant UI，适配移动端

## 注意事项

- 数据存储在CSV文件中，请确保`data/`目录有读写权限
- 密码采用MD5加密，生产环境建议使用更安全的加密方式
- 系统未做分布式部署，仅适合个人使用
