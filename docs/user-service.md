# 用户服务文档

## 1. 概述

用户服务（UserService）是 Motify 车辆维修管理系统的核心服务之一，负责处理所有与用户相关的业务逻辑。该服务提供了用户管理、车辆管理、维修记录管理等核心功能。

## 2. 功能列表

### 2.1 用户管理
- 用户注册
- 用户登录
- 获取用户信息
- 更新用户信息

### 2.2 车辆管理
- 获取用户车辆列表
- 添加车辆

### 2.3 维修记录管理
- 获取用户维修记录
- 提交维修请求

## 3. API 接口说明

### 3.1 用户注册
```http
POST /api/todo/users/register
Content-Type: application/json

Request:
{
    "username": "string",     // 用户名，必填
    "password": "string",     // 密码，必填
    "phone": "string"         // 手机号，必填
}

Response:
{
    "code": 200,              // 状态码
    "message": "success",     // 响应消息
    "data": {
        "userId": "long",     // 用户ID
        "username": "string", // 用户名
        "phone": "string"     // 手机号
    }
}

Error Response:
{
    "code": 400,
    "message": "用户名已存在",
    "data": null
}
```

### 3.2 用户登录
```http
POST /api/todo/users/login
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
        "userId": "long",
        "username": "string",
        "phone": "string"
    }
}

Error Response:
{
    "code": 401,
    "message": "用户名或密码错误",
    "data": null
}
```

### 3.3 获取用户信息
```http
GET /api/todo/users/{userId}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": "long",
        "username": "string",
        "phone": "string"
    }
}

Error Response:
{
    "code": 404,
    "message": "用户不存在",
    "data": null
}
```

### 3.4 更新用户信息
```http
PUT /api/todo/users/{userId}
Content-Type: application/json

Request:
{
    "username": "string",     // 可选
    "password": "string",     // 可选
    "phone": "string"         // 可选
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "userId": "long",
        "username": "string",
        "phone": "string"
    }
}

Error Response:
{
    "code": 400,
    "message": "用户名已存在",
    "data": null
}
```

### 3.5 获取用户车辆列表
```http
GET /api/todo/users/{userId}/cars

Response:
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "carId": "long",
            "brand": "string",
            "model": "string",
            "licensePlate": "string"
        }
    ]
}

Error Response:
{
    "code": 404,
    "message": "用户不存在",
    "data": null
}
```

### 3.6 添加车辆
```http
POST /api/todo/users/{userId}/cars
Content-Type: application/json

Request:
{
    "brand": "string",        // 品牌，必填
    "model": "string",        // 型号，必填
    "licensePlate": "string"  // 车牌号，必填
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "carId": "long",
        "brand": "string",
        "model": "string",
        "licensePlate": "string"
    }
}

Error Response:
{
    "code": 400,
    "message": "车辆信息不完整",
    "data": null
}
```

### 3.7 获取用户维修记录
```http
GET /api/todo/users/{userId}/maintenance-records

Response:
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "recordId": "long",
            "description": "string",
            "status": "string",
            "progress": "integer",
            "result": "string",
            "createTime": "datetime",
            "car": {
                "carId": "long",
                "brand": "string",
                "model": "string",
                "licensePlate": "string"
            }
        }
    ]
}

Error Response:
{
    "code": 404,
    "message": "用户不存在",
    "data": null
}
```

### 3.8 提交维修请求
```http
POST /api/todo/users/{userId}/maintenance-records
Content-Type: application/json

Request:
{
    "carId": "long",          // 车辆ID，必填
    "description": "string"   // 维修描述，必填
}

Response:
{
    "code": 200,
    "message": "success",
    "data": {
        "recordId": "long",
        "description": "string",
        "status": "PENDING",
        "progress": 0,
        "createTime": "datetime",
        "car": {
            "carId": "long",
            "brand": "string",
            "model": "string",
            "licensePlate": "string"
        }
    }
}

Error Response:
{
    "code": 400,
    "message": "车辆不属于当前用户",
    "data": null
}
```

### 3.9 重置密码
```http
POST /api/todo/users/reset-password
Content-Type: application/json

Request:
{
    "phone": "string",        // 手机号，必填
    "code": "string",         // 验证码，必填
    "newPassword": "string"   // 新密码，必填
}

Response:
{
    "code": 200,
    "message": "密码重置成功",
    "data": null
}

Error Response:
{
    "code": 400,
    "message": "验证码错误或已过期",
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
GET /api/v1/users/{userId}/maintenance-records?page=0&size=10&sort=createTime,desc
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
- 登录成功后返回用户信息
- 前端需要保存用户ID用于后续请求
- 需要认证的接口需要验证用户ID

### 5.2 权限控制
- 用户只能访问自己的资源
- 需要验证请求中的用户ID是否匹配

### 5.3 数据验证
- 所有输入数据都经过验证
- 敏感数据（如密码）在传输和存储时都进行加密

## 6. 使用示例

### 6.1 用户注册
```javascript
// 使用 axios
const response = await axios.post('/api/todo/users/register', {
    username: 'testUser',
    password: 'password123',
    phone: '13800138000'
});
```

### 6.2 用户登录
```javascript
const response = await axios.post('/api/todo/users/login', {
    username: 'testUser',
    password: 'password123'
});
// 保存用户ID
const userId = response.data.data.userId;
```

### 6.3 添加车辆
```javascript
const response = await axios.post(`/api/todo/users/${userId}/cars`, {
    brand: 'Toyota',
    model: 'Camry',
    licensePlate: '京A12345'
});
```

### 6.4 提交维修请求
```javascript
const response = await axios.post(`/api/todo/users/${userId}/maintenance-records`, {
    carId: 456,
    description: '发动机异响'
});
``` 