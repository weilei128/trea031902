# 个人收支记账系统 API 文档

## 基础信息

- **Base URL**: `http://localhost:8080`
- **响应格式**: JSON
- **统一响应结构**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {}
}
```

## 响应状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未登录或登录已过期 |
| 403 | 权限不足 |
| 500 | 系统错误 |

## 认证方式

除登录和注册接口外，其他接口需要在请求头中携带 JWT Token：

```
Authorization: Bearer {token}
```

---

## 用户模块

### 1. 用户注册

**接口**: `POST /api/user/register`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名（手机号或邮箱） |
| password | string | 是 | 密码（6-20位） |
| confirmPassword | string | 是 | 确认密码 |
| nickname | string | 否 | 昵称 |

**请求示例**:
```json
{
    "username": "13800138000",
    "password": "123456",
    "confirmPassword": "123456",
    "nickname": "张三"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "注册成功",
    "data": {
        "id": 1,
        "username": "13800138000",
        "nickname": "张三",
        "createTime": "2024-01-15 10:30:00",
        "token": "eyJhbGciOiJIUzI1NiIs..."
    }
}
```

---

### 2. 用户登录

**接口**: `POST /api/user/login`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名（手机号或邮箱） |
| password | string | 是 | 密码 |

**请求示例**:
```json
{
    "username": "13800138000",
    "password": "123456"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "登录成功",
    "data": {
        "id": 1,
        "username": "13800138000",
        "nickname": "张三",
        "createTime": "2024-01-15 10:30:00",
        "token": "eyJhbGciOiJIUzI1NiIs..."
    }
}
```

---

### 3. 修改密码

**接口**: `PUT /api/user/password`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | string | 是 | 旧密码 |
| newPassword | string | 是 | 新密码（6-20位） |
| confirmPassword | string | 是 | 确认新密码 |

**请求示例**:
```json
{
    "oldPassword": "123456",
    "newPassword": "654321",
    "confirmPassword": "654321"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "密码修改成功",
    "data": null
}
```

---

### 4. 获取用户信息

**接口**: `GET /api/user/info`

**请求头**: `Authorization: Bearer {token}`

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "id": 1,
        "username": "13800138000",
        "nickname": "张三",
        "createTime": "2024-01-15 10:30:00"
    }
}
```

---

## 记账模块

### 1. 添加收支记录

**接口**: `POST /api/transaction`

**请求头**: `Authorization: Bearer {token}`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| amount | decimal | 是 | 金额（必须大于0） |
| type | string | 是 | 类型：INCOME-收入，EXPENSE-支出 |
| category | string | 是 | 分类（见下方分类列表） |
| remark | string | 否 | 备注 |

**收入分类**: 薪资、奖金、投资、兼职、红包、其他收入

**支出分类**: 餐饮、交通、购物、娱乐、住房、医疗、教育、通讯、其他支出

**请求示例**:
```json
{
    "amount": 100.50,
    "type": "EXPENSE",
    "category": "餐饮",
    "remark": "午餐"
}
```

**响应示例**:
```json
{
    "code": 200,
    "msg": "添加成功",
    "data": {
        "id": 1,
        "userId": 1,
        "amount": 100.50,
        "type": "EXPENSE",
        "typeName": "支出",
        "category": "餐饮",
        "remark": "午餐",
        "createTime": "2024-01-15 12:30:00"
    }
}
```

---

### 2. 修改收支记录

**接口**: `PUT /api/transaction/{id}`

**请求头**: `Authorization: Bearer {token}`

**路径参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | long | 记录ID |

**请求参数**: 同添加接口

**响应示例**:
```json
{
    "code": 200,
    "msg": "修改成功",
    "data": {
        "id": 1,
        "userId": 1,
        "amount": 120.00,
        "type": "EXPENSE",
        "typeName": "支出",
        "category": "餐饮",
        "remark": "晚餐",
        "createTime": "2024-01-15 12:30:00"
    }
}
```

---

### 3. 删除收支记录

**接口**: `DELETE /api/transaction/{id}`

**请求头**: `Authorization: Bearer {token}`

**路径参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | long | 记录ID |

**响应示例**:
```json
{
    "code": 200,
    "msg": "删除成功",
    "data": null
}
```

---

### 4. 查询收支记录详情

**接口**: `GET /api/transaction/{id}`

**请求头**: `Authorization: Bearer {token}`

**路径参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | long | 记录ID |

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "id": 1,
        "userId": 1,
        "amount": 100.50,
        "type": "EXPENSE",
        "typeName": "支出",
        "category": "餐饮",
        "remark": "午餐",
        "createTime": "2024-01-15 12:30:00"
    }
}
```

---

### 5. 分页查询收支记录

**接口**: `GET /api/transaction/list`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页大小，默认10 |
| type | string | 否 | 类型筛选：INCOME/EXPENSE |
| category | string | 否 | 分类筛选 |
| startTime | datetime | 否 | 开始时间（格式：yyyy-MM-dd HH:mm:ss） |
| endTime | datetime | 否 | 结束时间（格式：yyyy-MM-dd HH:mm:ss） |

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "pageNum": 1,
        "pageSize": 10,
        "total": 50,
        "totalPages": 5,
        "list": [
            {
                "id": 1,
                "userId": 1,
                "amount": 100.50,
                "type": "EXPENSE",
                "typeName": "支出",
                "category": "餐饮",
                "remark": "午餐",
                "createTime": "2024-01-15 12:30:00"
            }
        ]
    }
}
```

---

### 6. 获取分类列表

**接口**: `GET /api/transaction/categories`

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | string | 是 | 类型：INCOME/EXPENSE |

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": ["餐饮", "交通", "购物", "娱乐", "住房", "医疗", "教育", "通讯", "其他支出"]
}
```

---

## 统计模块

### 1. 获取统计数据

**接口**: `GET /api/transaction/statistics`

**请求头**: `Authorization: Bearer {token}`

**查询参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startTime | string | 否 | 开始时间（格式：yyyy-MM-dd HH:mm:ss） |
| endTime | string | 否 | 结束时间（格式：yyyy-MM-dd HH:mm:ss） |

**响应示例**:
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "totalIncome": 5000.00,
        "totalExpense": 2500.00,
        "balance": 2500.00,
        "incomeByCategory": [
            {
                "category": "薪资",
                "amount": 5000.00,
                "percentage": 100.00
            }
        ],
        "expenseByCategory": [
            {
                "category": "餐饮",
                "amount": 1000.00,
                "percentage": 40.00
            },
            {
                "category": "购物",
                "amount": 750.00,
                "percentage": 30.00
            },
            {
                "category": "交通",
                "amount": 750.00,
                "percentage": 30.00
            }
        ]
    }
}
```

---

## 错误响应示例

### 参数错误
```json
{
    "code": 400,
    "msg": "金额必须大于0",
    "data": null
}
```

### 未登录
```json
{
    "code": 401,
    "msg": "请先登录",
    "data": null
}
```

### 权限不足
```json
{
    "code": 403,
    "msg": "无权操作此记录",
    "data": null
}
```

### 记录不存在
```json
{
    "code": 500,
    "msg": "记录不存在",
    "data": null
}
```
