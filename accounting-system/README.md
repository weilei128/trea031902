# 个人收支记账系统

基于 Java + Spring Boot + H5 开发的个人收支记账系统，使用 CSV 文件存储数据。

## 功能特性

### 用户模块
- ✅ 用户注册（支持手机号/邮箱）
- ✅ 用户登录
- ✅ 修改密码
- ✅ JWT Token 认证

### 记账模块
- ✅ 添加收支记录（金额、类型、分类、备注）
- ✅ 修改收支记录
- ✅ 删除收支记录
- ✅ 分页查询收支记录
- ✅ 支持按时间范围、类型、分类筛选

### 统计模块
- ✅ 按时间范围统计收支总额
- ✅ 按分类统计占比
- ✅ 可视化数据展示

### 技术特性
- ✅ 统一的响应格式
- ✅ 参数校验
- ✅ 全局异常处理
- ✅ 数据权限控制（仅可操作自己的数据）
- ✅ 跨域支持

## 技术栈

- **后端**: Spring Boot 2.7.18
- **前端**: HTML5 + CSS3 + JavaScript (原生)
- **数据存储**: CSV 文件 (Apache Commons CSV)
- **安全**: JWT + SHA-256 密码加密
- **工具库**: Lombok、Apache Commons CSV

## 环境要求

- **JDK**: 1.8 或更高版本
- **Maven**: 3.6 或更高版本
- **浏览器**: Chrome、Firefox、Edge 等现代浏览器

## 快速开始

### 1. 克隆/下载项目

```bash
cd accounting-system
```

### 2. 编译项目

```bash
mvn clean compile
```

### 3. 运行项目

```bash
mvn spring-boot:run
```

或者打包后运行：

```bash
mvn clean package
java -jar target/accounting-system-1.0.0.jar
```

### 4. 访问系统

打开浏览器访问：http://localhost:8080

## 项目结构

```
accounting-system/
├── pom.xml                          # Maven 配置文件
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── accounting/
│       │           ├── AccountingApplication.java    # 启动类
│       │           ├── config/                       # 配置类
│       │           ├── controller/                   # 控制器
│       │           ├── dto/                          # 数据传输对象
│       │           ├── entity/                       # 实体类
│       │           ├── exception/                    # 异常处理
│       │           ├── interceptor/                  # 拦截器
│       │           ├── service/                      # 服务层
│       │           └── util/                         # 工具类
│       └── resources/
│           ├── application.yml      # 应用配置
│           └── static/
│               └── index.html       # H5前端页面
└── data/                            # CSV数据存储目录
    ├── users.csv                    # 用户数据文件
    └── transactions.csv             # 记账数据文件
```

## API 接口文档

详见 [API文档.md](API文档.md)

### 主要接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/user/register | POST | 用户注册 |
| /api/user/login | POST | 用户登录 |
| /api/user/password | PUT | 修改密码 |
| /api/transaction | POST | 添加记账记录 |
| /api/transaction/{id} | PUT | 修改记账记录 |
| /api/transaction/{id} | DELETE | 删除记账记录 |
| /api/transaction/list | GET | 分页查询记账记录 |
| /api/transaction/statistics | GET | 获取统计数据 |

## 使用指南

### 1. 注册账号
1. 打开首页 http://localhost:8080
2. 点击"注册账号"
3. 填写用户名（手机号或邮箱）、密码、确认密码
4. 点击"注册"按钮

### 2. 登录系统
1. 输入用户名和密码
2. 点击"登录"按钮

### 3. 添加记账记录
1. 选择类型（收入/支出）
2. 选择分类
3. 输入金额
4. 填写备注（可选）
5. 点击"保存"

### 4. 查看统计
- 在"收支概览"卡片查看总收入、总支出、结余
- 在"分类统计"卡片查看各类别占比

### 5. 管理记录
- 在"收支记录"列表中查看所有记录
- 点击"编辑"修改记录
- 点击"删除"删除记录
- 使用筛选功能查找特定记录

## 数据存储

系统使用 CSV 文件存储数据，位于项目目录的 `data` 文件夹中：

- **users.csv**: 存储用户信息（ID、用户名、密码、昵称、创建时间、更新时间）
- **transactions.csv**: 存储记账记录（ID、用户ID、金额、类型、分类、备注、创建时间、更新时间）

## 配置说明

可以在 `application.yml` 中修改配置：

```yaml
server:
  port: 8080  # 服务端口

app:
  data-path: data                    # 数据文件存储路径
  user-file: users.csv               # 用户数据文件名
  transaction-file: transactions.csv # 记账数据文件名

jwt:
  secret: your-secret-key            # JWT密钥
  expiration: 86400000               # Token有效期（毫秒）
```

## 分类预设

### 收入分类
- 薪资、奖金、投资、兼职、红包、其他收入

### 支出分类
- 餐饮、交通、购物、娱乐、住房、医疗、教育、通讯、其他支出

## 响应格式

统一响应格式：

```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {}
}
```

状态码说明：
- 200: 操作成功
- 400: 参数错误
- 401: 未登录或登录已过期
- 403: 权限不足
- 500: 系统错误

## 安全说明

1. 密码使用 SHA-256 加密存储
2. 使用 JWT 进行身份认证
3. Token 有效期为 24 小时
4. 用户只能操作自己的数据

## 浏览器兼容性

- Chrome 80+
- Firefox 75+
- Edge 80+
- Safari 13+

## 常见问题

### 1. 端口被占用

修改 `application.yml` 中的端口配置：
```yaml
server:
  port: 8081
```

### 2. 数据文件权限问题

确保程序有权限读写 `data` 目录，或手动创建该目录。

### 3. 登录状态丢失

Token 有效期为 24 小时，过期后需要重新登录。

## 开发计划

- [ ] 支持数据导出（Excel/PDF）
- [ ] 支持多账本
- [ ] 支持预算设置
- [ ] 支持账单提醒
- [ ] 支持数据备份与恢复

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎反馈。
