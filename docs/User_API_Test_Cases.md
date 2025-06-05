# User API 测试用例

## 测试环境配置
- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/json`

## 测试数据准备

### 预置测试数据
在开始测试前，请确保数据库中有以下测试数据：

```sql
-- 插入测试用户
INSERT INTO users (user_id, username, password, phone, name, email) VALUES 
(1, 'testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXXZh2M.q4kh5xGRKaOL7k9NRSG', '13800138001', '测试用户1', 'test1@example.com'),
(2, 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXXZh2M.q4kh5xGRKaOL7k9NRSG', '13800138002', '测试用户2', 'test2@example.com');

-- 插入测试车辆
INSERT INTO cars (car_id, brand, model, license_plate, user_id) VALUES 
(1, 'Toyota', 'Camry', '京A12345', 1),
(2, 'Honda', 'Accord', '京B67890', 1),
(3, 'BMW', 'X5', '沪C11111', 2);

-- 插入测试维修人员
INSERT INTO repairmen (repairman_id, username, password, name, type, phone) VALUES 
(1, 'repairman1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXXZh2M.q4kh5xGRKaOL7k9NRSG', '维修师傅1', '发动机维修', '13900139001');

-- 插入测试维修项目
INSERT INTO maintenance_items (item_id, name, description, status, progress, cost, material_cost, labor_cost, create_time, car_id, reminder, score) VALUES 
(1, '发动机维修', '发动机异响需要检修', 'IN_PROGRESS', 60, 1200.00, 800.00, 400.00, '2024-01-15 10:00:00', 1, '请加急处理', NULL),
(2, '刹车片更换', '前后刹车片磨损严重', 'COMPLETED', 100, 800.00, 500.00, 300.00, '2024-01-10 09:00:00', 1, NULL, 5),
(3, '轮胎更换', '四个轮胎需要更换', 'PENDING', 0, 0.00, 0.00, 0.00, '2024-01-20 14:00:00', 2, NULL, NULL);
```

---

## API 测试用例

### 1. 用户注册

**POST** `/api/auth/users/register`

#### 测试用例 1.1: 成功注册
```json
{
  "username": "testuser1",
  "password": "password123",
  "phone": "13800138999",
  "name":"测试用户"
}
```

**期望响应**:
```json
{
    "code": 200,
    "data": {
        "phone": "13800138999",
        "userId": 10,
        "username": "testuser1"
    },
    "message": "success"
}
```

#### 测试用例 1.2: 用户名已存在（应失败）
```json
{
  "username": "testuser1",
  "password": "password123",
  "phone": "13800138999"
}
```

#### 测试用例 1.3: 缺少必填字段（应失败）
```json
{
  "username": "incomplete",
  "password": ""
}
```

---

### 2. 用户登录

**POST** `/api/auth/users/login`

#### 测试用例 2.1: 成功登录
```json
{
  "username": "testuser1",
  "password": "password123"
}
```

**期望响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "testuser1",
    "phone": "13800138001"
  }
}
```

#### 测试用例 2.2: 错误密码（应失败）
```json
{
  "username": "testuser1",
  "password": "wrongpassword"
}
```

#### 测试用例 2.3: 用户不存在（应失败）
```json
{
  "username": "nonexistent",
  "password": "password123"
}
```

---

### 3. 获取用户信息

**GET** `/api/auth/users/{userId}`

#### 测试用例 3.1: 获取存在的用户
```
GET /api/auth/users/1
```

**期望响应**:
```json
{
    "code": 200,
    "data": {
        "address": "北京市朝阳区建国路88号",
        "phone": "13800138001",
        "name": "张三",
        "userId": 1,
        "email": "zhangsan@example.com",
        "username": "testuser1_updated"
    },
    "message": "success"
}
```

#### 测试用例 3.2: 获取不存在的用户（应失败）
```
GET /api/auth/users/999
```

---

### 4. 更新用户信息

**PUT** `/api/auth/users/{userId}`

#### 测试用例 4.1: 更新用户信息（可以只更改一部分信息）
```json
{
        "address": "test",
        "phone": "13708459697",
        "name": "test",
        "email": "test@test.com",
        "username": "test"
}
```

#### 返回结果
{
    "code": 200,
    "data": {
        "address": "test",
        "phone": "13708459697",
        "name": "test",
        "userId": 1,
        "email": "test@test.com",
        "username": "test"
    },
    "message": "用户信息更新成功"
}

#### 测试用例 4.2: 更新为已存在的用户名（应失败）
```json
{
  "username": "testuser2"
}
```

---

### 5. 获取用户车辆列表

**GET** `/api/auth/users/{userId}/cars`

#### 测试用例 5.1: 获取用户车辆
```
GET /api/auth/users/1/cars
```

**期望响应**:
```json
{
    "code": 200,
    "data": [
        {
            "licensePlate": "京A12345",
            "model": "凯美瑞",
            "brand": "丰田",
            "user": {
                "phone": "13800138001",
                "name": "张三",
                "userId": 1,
                "email": "zhangsan@example.com",
                "username": "testuser1_updated"
            },
            "carId": 1
        },
        {
            "licensePlate": "京A98765",
            "model": "途观",
            "brand": "大众",
            "user": {
                "phone": "13800138001",
                "name": "张三",
                "userId": 1,
                "email": "zhangsan@example.com",
                "username": "testuser1_updated"
            },
            "carId": 9
        }
    ],
    "message": "success"
}
```

---

### 6. 添加车辆

**POST** `/api/auth/users/{userId}/cars`

#### 测试用例 6.1: 成功添加车辆
```json
{
  "brand": "梅赛德斯奔驰",
  "model": "C-Class",
  "licensePlate": "京C99999"
}
```

**期望响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "carId": 4,
    "brand": "梅赛德斯奔驰",
    "model": "C-Class",
    "licensePlate": "京C99999"
  }
}
```

#### 测试用例 6.2: 缺少必填字段（应失败）
```json
{
  "brand": "Tesla",
  "licensePlate": ""
}
```

---

### 正确的测试请求

**使用curl测试**:
```bash
curl -X POST "http://localhost:8080/api/auth/users/1/cars" \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "梅赛德斯奔驰",
    "model": "C-Class", 
    "licensePlate": "京C99999"
  }'
```

**使用Postman测试**:
- URL: `POST http://localhost:8080/api/auth/users/1/cars`
- Headers: `Content-Type: application/json`
- Body (raw JSON):
```json
{
  "brand": "梅赛德斯奔驰",
  "model": "C-Class",
  "licensePlate": "京C99999"
}
```

---

### 7. 获取用户维修记录

**GET** `/api/auth/users/{userId}/maintenance-records`

#### 测试用例 7.1: 获取用户所有维修记录
```
GET /api/auth/users/1/maintenance-records
```

**期望响应**:
```json
{
    "code": 200,
    "data": [
        {
            "itemId": 1,
            "name": "发动机保养",
            "description": "更换机油、机滤、空滤",
            "status": "COMPLETED",
            "progress": 100,
            "result": "保养完成，发动机运转正常",
            "reminder": "下次保养时间：2024-12-01",
            "score": 5,
            "createTime": "2024-05-15T17:00:00",
            "updateTime": "2024-05-15T19:30:00",
            "completeTime": "2024-05-15T19:30:00",
            "materialCost": 378.0,
            "laborCost": 160.0,
            "cost": 538.0
        }
    ],
    "message": "success"
}
```

---

### 8. 获取用户当前正在进行的维修项目

**GET** `/api/auth/users/{userId}/maintenance-records/current`

#### 测试用例 8.1: 获取正在进行的维修项目
```
GET /api/auth/users/1/maintenance-records/current
```

**期望响应**:
```json
{
    "code": 200,
    "data": [
        {
            "itemId": 3,
            "name": "轮胎更换",
            "description": "更换四条轮胎",
            "status": "IN_PROGRESS",
            "progress": 75,
            "result": null,
            "reminder": null,
            "score": null,
            "createTime": "2024-06-01T16:30:00",
            "updateTime": "2024-06-01T23:00:00",
            "completeTime": null,
            "materialCost": 2720.0,
            "laborCost": 200.0,
            "cost": 2920.0
        }
    ],
    "message": "success"
}
```

#### 测试用例 8.2: 用户无正在进行的维修项目
```
GET /api/auth/users/2/maintenance-records/current
```

**期望响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": []
}
```

---

### 9. 提交维修请求

**POST** `/api/auth/users/{userId}/maintenance-records`

#### 测试用例 9.1: 成功提交维修请求
```json
{
  "carId": 1,
  "name": "空调维修",
  "description": "空调制冷效果差，需要检查制冷剂"
}
```

**期望响应**:
```json
{
    "code": 200,
    "data": {
        "itemId": 9,
        "cost": 0.0,
        "createTime": "2025-06-04T21:47:05.8366608",
        "name": "空调维修",
        "description": "空调制冷效果差，需要检查制冷剂",
        "progress": 0,
        "status": "PENDING"
    },
    "message": "维修请求提交成功"
}
```

#### 测试用例 9.2: 车辆不属于用户（应失败）
```json
{
  "carId": 3,
  "name": "变速箱维修",
  "description": "变速箱异常"
}
```

---

### 10. 提交催单请求

**POST** `/api/auth/users/{userId}/maintenance-records/{itemId}/rush-order`

#### 测试用例 10.1: 成功提交催单
```json
{
  "reminderMessage": "请加急处理，明天急需用车"
}
```

**期望响应**:
```json
{
    "code": 200,
    "data": {
        "itemId": 9,
        "reminder": "请加急处理，明天急需用车",
        "name": "空调维修",
        "updateTime": "2025-06-04T21:51:18.6265936",
        "status": "PENDING"
    },
    "message": "催单提交成功"
}
```

#### 测试用例 10.2: 对已完成项目催单（应失败）
```
POST /api/auth/users/1/maintenance-records/2/rush-order
```
```json
{
  "reminderMessage": "这个已经完成了"
}
```

---

### 11. 提交服务评分

**POST** `/api/auth/users/{userId}/maintenance-records/{itemId}/rating`

#### 测试用例 11.1: 成功提交评分
```
POST /api/auth/users/1/maintenance-records/2/rating
```
```json
{
  "score": 4
}
```

**期望响应**:
```json
{
    "code": 200,
    "data": {
        "itemId": 1,
        "score": 5,
        "name": "发动机保养",
        "updateTime": "2025-06-04T21:52:42.6405402",
        "status": "COMPLETED"
    },
    "message": "评分提交成功"
}
```

#### 测试用例 11.2: 对未完成项目评分（应失败）
```
POST /api/auth/users/1/maintenance-records/1/rating
```
```json
{
  "score": 5
}
```

#### 测试用例 11.3: 无效评分（应失败）
```json
{
  "score": 6
}
```

---

### 12. 获取维修项目详情

**GET** `/api/auth/users/{userId}/maintenance-records/{itemId}`

#### 测试用例 12.1: 获取维修项目详情
```
GET /api/auth/users/1/maintenance-records/1
```

**期望响应**:
```json
{
    "code": 200,
    "data": {
        "cost": 538.0,
        "reminder": "下次保养时间：2024-12-01",
        "description": "更换机油、机滤、空滤",
        "completeTime": "2024-05-15T19:30:00",
        "updateTime": "2025-06-04T21:52:42.64054",
        "materialCost": 378.0,
        "result": "保养完成，发动机运转正常",
        "itemId": 1,
        "score": 5,
        "createTime": "2024-05-15T17:00:00",
        "car": {
            "licensePlate": "京A12345",
            "model": "凯美瑞",
            "brand": "丰田",
            "carId": 1
        },
        "name": "发动机保养",
        "progress": 100,
        "laborCost": 160.0,
        "status": "COMPLETED"
    },
    "message": "success"
}
```

#### 测试用例 12.2: 查看不属于自己的维修项目（应失败）
```
GET /api/auth/users/2/maintenance-records/1
```

---

### 13. 重置密码

**POST** `/api/auth/users/reset-password`

#### 测试用例 13.1: 成功重置密码
```json
{
  "phone": "13800138001",
  "code": "123456",
  "newPassword": "newpassword123"
}
```

**期望响应**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null
}
```

#### 测试用例 13.2: 手机号不存在（应失败）
```json
{
  "phone": "13999999999",
  "code": "123456",
  "newPassword": "newpassword123"
}
```

---

## 使用 cURL 命令测试示例

### 用户注册
```bash
curl -X POST "http://localhost:8080/api/auth/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser001",
    "password": "password123",
    "phone": "13800138888",
    "name": "新用户"
  }'
```

### 用户登录
```bash
curl -X POST "http://localhost:8080/api/auth/users/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser1",
    "password": "password123"
  }'
```

### 获取当前进行中的维修项目
```bash
curl -X GET "http://localhost:8080/api/auth/users/1/maintenance-records/current" \
  -H "Content-Type: application/json"
```

### 提交维修请求
```bash
curl -X POST "http://localhost:8080/api/auth/users/1/maintenance-records" \
  -H "Content-Type: application/json" \
  -d '{
    "carId": 1,
    "name": "空调维修",
    "description": "空调制冷效果差"
  }'
```

---

## 测试注意事项

1. **测试顺序**: 建议按照用户注册 → 登录 → 添加车辆 → 提交维修请求的顺序进行测试
2. **数据依赖**: 某些测试用例依赖于前面创建的数据，请注意测试顺序
3. **错误场景**: 每个API都包含了正常和异常场景的测试用例
4. **数据清理**: 测试完成后建议清理测试数据，避免影响后续测试

## 自动化测试脚本

可以使用Postman、Insomnia等工具导入这些测试用例，或者编写自动化测试脚本来批量执行这些测试。
