# 维修人员服务文档

## 1. 概述

维修人员服务（RepairmanService）是 Motify 车辆维修管理系统的核心服务之一，负责处理所有与维修人员相关的业务逻辑。该服务提供了维修人员管理、维修工单处理、材料记录、进度更新等核心功能。

## 2. 功能列表

### 2.1 维修人员管理
- 维修人员注册
- 维修人员登录
- 获取维修人员信息
- 更新维修人员信息

### 2.2 维修工单管理
- 接收维修工单
- 拒绝维修工单
- 更新维修进度
- 提交维修结果

### 2.3 材料管理
- 记录维修材料使用
- 更新材料价格
- 查询材料使用记录

### 2.4 收入统计
- 查询工时费收入
- 统计历史维修记录

## 3. API 接口说明

### 3.1 维修人员注册
```http
POST /api/todo/repairmen/register
Content-Type: application/json

Request:
{
    "username": "string",     // 用户名，必填
    "password": "string",     // 密码，必填
    "name": "string",         // 姓名，必填
    "phone": "string",        // 手机号，必填
    "specialty": "string",    // 工种，必填
    "type": "string"          // 类型，必填
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "repairmanId": "long",
        "username": "string",
        "name": "string",
        "phone": "string",
        "specialty": "string",
        "type": "string"
    }
}

Error Response:
{
    "code": 400,
    "message": "用户名已存在",
    "data": null
}
```

### 3.2 维修人员登录
```http
POST /api/todo/repairmen/login
Content-Type: application/json

Request:
{
    "username": "string",     // 用户名，必填
    "password": "string"      // 密码，必填
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "repairmanId": "long",
        "username": "string",
        "name": "string",
        "specialty": "string",
        "type": "string"
    }
}

Error Response:
{
    "code": 401,
    "message": "用户名或密码错误",
    "data": null
}
```

### 3.3 获取维修人员信息
```http
GET /api/todo/repairmen/{repairmanId}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "repairmanId": "long",
        "username": "string",
        "name": "string",
        "phone": "string",
        "specialty": "string",
        "type": "string",
        "salary": {
            "hourlyWage": "decimal",
            "monthlyWage": "decimal"
        }
    }
}

Error Response:
{
    "code": 404,
    "message": "维修人员不存在",
    "data": null
}
```

### 3.4 更新维修人员信息
```http
PUT /api/todo/repairmen/{repairmanId}
Content-Type: application/json

Request:
{
    "name": "string",         // 可选
    "phone": "string",        // 可选
    "specialty": "string",    // 可选
    "type": "string"          // 可选
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "repairmanId": "long",
        "username": "string",
        "name": "string",
        "phone": "string",
        "specialty": "string",
        "type": "string"
    }
}

Error Response:
{
    "code": 400,
    "message": "更新失败",
    "data": null
}
```

### 3.5 接收维修工单
```http
POST /api/todo/repairmen/{repairmanId}/maintenance-items/{itemId}/accept
Content-Type: application/json

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "itemId": "long",
        "status": "ACCEPTED",
        "progress": 0
    }
}

Error Response:
{
    "code": 400,
    "message": "工单已被其他维修人员接收",
    "data": null
}
```

### 3.6 拒绝维修工单
```http
POST /api/todo/repairmen/{repairmanId}/maintenance-items/{itemId}/reject
Content-Type: application/json

Request:
{
    "reason": "string"        // 拒绝原因，必填
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "itemId": "long",
        "status": "REJECTED"
    }
}

Error Response:
{
    "code": 400,
    "message": "工单状态不允许拒绝",
    "data": null
}
```

### 3.7 更新维修进度
```http
PUT /api/todo/repairmen/{repairmanId}/maintenance-items/{itemId}/progress
Content-Type: application/json

Request:
{
    "progress": "integer",    // 进度（0-100），必填
    "description": "string"   // 进度描述，必填
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "itemId": "long",
        "progress": "integer",
        "description": "string",
        "updateTime": "datetime"
    }
}

Error Response:
{
    "code": 400,
    "message": "进度值无效",
    "data": null
}
```

### 3.8 提交维修结果
```http
POST /api/todo/repairmen/{repairmanId}/maintenance-items/{itemId}/complete
Content-Type: application/json

Request:
{
    "result": "string",           // 维修结果，必填
    "materials": [                // 使用的材料列表
        {
            "materialId": "long",
            "quantity": "integer",
            "price": "decimal"
        }
    ],
    "workingHours": "decimal"     // 工作时长，必填
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "itemId": "long",
        "status": "COMPLETED",
        "result": "string",
        "totalAmount": "decimal",
        "workingHours": "decimal",
        "materialCost": "decimal"
    }
}

Error Response:
{
    "code": 400,
    "message": "维修结果不完整",
    "data": null
}
```

### 3.9 查询工时费收入
```http
GET /api/todo/repairmen/{repairmanId}/income
Content-Type: application/json

Request:
{
    "startDate": "date",      // 开始日期，可选
    "endDate": "date"         // 结束日期，可选
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "totalIncome": "decimal",
        "workingHours": "decimal",
        "completedItems": "integer",
        "details": [
            {
                "date": "date",
                "income": "decimal",
                "hours": "decimal",
                "items": "integer"
            }
        ]
    }
}

Error Response:
{
    "code": 400,
    "message": "日期范围无效",
    "data": null
}
```

## 4. 通用说明

### 4.1 请求头
- Content-Type: application/json

### 4.2 响应格式
所有接口统一返回格式：
```json
{
    "code": "integer",    // 状态码
    "message": "string",  // 响应消息
    "data": "object"      // 响应数据
}
```

### 4.3 状态码说明
- 200: 成功
- 400: 请求参数错误
- 401: 未认证或认证失败
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误

### 4.4 分页参数
对于列表类接口，支持以下分页参数：
```http
GET /api/todo/repairmen/{repairmanId}/maintenance-items?page=0&size=10&sort=createTime,desc
```
- page: 页码（从0开始）
- size: 每页大小
- sort: 排序字段和方向

### 4.5 错误处理
所有错误响应都包含：
- code: 错误码
- message: 错误描述
- data: null

## 5. 安全说明

### 5.1 认证
- 使用基本的登录状态管理
- 登录成功后返回维修人员信息
- 前端需要保存维修人员ID用于后续请求
- 需要认证的接口需要验证维修人员ID

### 5.2 权限控制
- 维修人员只能访问自己的资源
- 需要验证请求中的维修人员ID是否匹配
- 维修人员只能处理分配给自己的工单

### 5.3 数据验证
- 所有输入数据都经过验证
- 敏感数据（如密码）在传输和存储时都进行加密
- 工时和材料使用记录需要详细记录

## 6. 使用示例

### 6.1 维修人员注册
```javascript
const response = await axios.post('/api/todo/repairmen/register', {
    username: 'repairman1',
    password: 'password123',
    name: '张三',
    phone: '13800138000',
    specialty: '机修',
    type: 'FULL_TIME'
});
```

### 6.2 维修人员登录
```javascript
const response = await axios.post('/api/todo/repairmen/login', {
    username: 'repairman1',
    password: 'password123'
});
// 保存维修人员ID
const repairmanId = response.data.data.repairmanId;
```

### 6.3 接收维修工单
```javascript
const response = await axios.post(
    `/api/todo/repairmen/${repairmanId}/maintenance-items/${itemId}/accept`
);
```

### 6.4 更新维修进度
```javascript
const response = await axios.put(
    `/api/todo/repairmen/${repairmanId}/maintenance-items/${itemId}/progress`,
    {
        progress: 50,
        description: '已完成发动机检修，准备更换零件'
    }
);
```

### 6.5 提交维修结果
```javascript
const response = await axios.post(
    `/api/todo/repairmen/${repairmanId}/maintenance-items/${itemId}/complete`,
    {
        result: '发动机异响问题已解决，更换了正时皮带',
        materials: [
            {
                materialId: 1,
                quantity: 1,
                price: 299.99
            }
        ],
        workingHours: 4.5
    }
);
```

### 6.6 查询收入统计
```javascript
const response = await axios.get(
    `/api/todo/repairmen/${repairmanId}/income`,
    {
        params: {
            startDate: '2024-01-01',
            endDate: '2024-01-31'
        }
    }
);
``` 