# 用户反馈功能 API 文档

## 概述
新增的用户反馈功能允许用户对维修进度和结果进行反馈，包括：
1. **催单功能**：用户可以对进行中的维修项目提交催单请求
2. **服务评分**：用户可以对已完成的维修项目进行评分（1-5分）
3. **维修项目详情查看**：用户可以查看完整的维修项目信息，包括催单和评分
4. **当前进行中项目查询**：用户可以查看当前正在进行的维修项目

## API 端点

### 1. 获取用户当前正在进行的维修项目

**GET** `/api/auth/users/{userId}/maintenance-records/current`

#### 请求参数
- `userId` (路径参数): 用户ID

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "itemId": 1,
      "name": "发动机维修",
      "description": "发动机需要检修",
      "status": "IN_PROGRESS",
      "progress": 60,
      "result": null,
      "reminder": "请加急处理",
      "score": null,
      "createTime": "2024-01-15T10:00:00",
      "updateTime": "2024-01-15T14:30:00",
      "completeTime": null,
      "cost": 1200.0,
      "materialCost": 800.0,
      "laborCost": 400.0,
      "car": {
        "carId": 1,
        "brand": "Toyota",
        "model": "Camry",
        "licensePlate": "京A12345"
      }
    }
  ]
}
```

#### 业务规则
- 只返回状态为 `IN_PROGRESS` 的维修项目
- 用户只能查看自己车辆的维修项目
- 返回完整的维修项目信息，包括进度、催单信息等

#### 错误情况
- 用户不存在：404 Not Found

---

### 2. 提交催单请求

**POST** `/api/auth/users/{userId}/maintenance-records/{itemId}/rush-order`

#### 请求参数
- `userId` (路径参数): 用户ID
- `itemId` (路径参数): 维修项目ID

#### 请求体
```json
{
  "reminderMessage": "请加急处理，明天需要用车"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "催单提交成功",
  "data": {
    "itemId": 1,
    "name": "刹车片更换",
    "status": "IN_PROGRESS",
    "reminder": "请加急处理，明天需要用车",
    "updateTime": "2024-01-15T14:30:00"
  }
}
```

#### 业务规则
- 只有进行中(`IN_PROGRESS`)和待处理(`PENDING`)状态的维修项目可以催单
- 已完成(`COMPLETED`)或已取消(`CANCELLED`)的项目无法催单
- 用户只能对自己车辆的维修项目进行催单

#### 错误情况
- 用户不存在：404 Not Found
- 维修项目不存在：404 Not Found
- 无权限操作：400 Bad Request
- 状态不允许催单：400 Bad Request
- 催单信息为空：400 Bad Request

---

### 3. 提交服务评分

**POST** `/api/auth/users/{userId}/maintenance-records/{itemId}/rating`

#### 请求参数
- `userId` (路径参数): 用户ID
- `itemId` (路径参数): 维修项目ID

#### 请求体
```json
{
  "score": 5
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "评分提交成功",
  "data": {
    "itemId": 1,
    "name": "刹车片更换",
    "status": "COMPLETED",
    "score": 5,
    "updateTime": "2024-01-15T14:30:00"
  }
}
```

#### 业务规则
- 只有已完成(`COMPLETED`)状态的维修项目可以评分
- 评分范围：1-5分（整数）
- 每个维修项目只能评分一次，不可重复评分
- 用户只能对自己车辆的维修项目进行评分

#### 错误情况
- 用户不存在：404 Not Found
- 维修项目不存在：404 Not Found
- 无权限操作：400 Bad Request
- 状态不允许评分：400 Bad Request
- 评分范围无效：400 Bad Request
- 重复评分：400 Bad Request

---

### 4. 获取维修项目详情

**GET** `/api/auth/users/{userId}/maintenance-records/{itemId}`

#### 请求参数
- `userId` (路径参数): 用户ID
- `itemId` (路径参数): 维修项目ID

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "itemId": 1,
    "name": "刹车片更换",
    "description": "更换前后刹车片，检查刹车系统",
    "status": "COMPLETED",
    "progress": 100,
    "result": "刹车片更换完成，刹车系统正常",
    "reminder": "请加急处理，明天需要用车",
    "score": 5,
    "createTime": "2024-01-15T10:00:00",
    "updateTime": "2024-01-15T14:30:00",
    "completeTime": "2024-01-15T16:00:00",
    "cost": 450.0,
    "materialCost": 300.0,
    "laborCost": 150.0,
    "car": {
      "carId": 1,
      "brand": "Toyota",
      "model": "Camry",
      "licensePlate": "京A12345"
    }
  }
}
```

#### 业务规则
- 用户只能查看自己车辆的维修项目详情
- 返回完整的维修项目信息，包括催单和评分信息

#### 错误情况
- 用户不存在：404 Not Found
- 维修项目不存在：404 Not Found
- 无权限查看：400 Bad Request

---

## 使用流程示例

### 场景1：用户提交催单
1. 用户查看维修记录列表：`GET /api/auth/users/1/maintenance-records`
2. 选择一个进行中的维修项目
3. 提交催单：`POST /api/auth/users/1/maintenance-records/1/rush-order`
4. 查看更新后的项目详情：`GET /api/auth/users/1/maintenance-records/1`

### 场景2：用户对完成的维修进行评分
1. 用户查看维修记录列表，发现有已完成的项目
2. 查看项目详情：`GET /api/auth/users/1/maintenance-records/1`
3. 确认项目已完成且未评分
4. 提交评分：`POST /api/auth/users/1/maintenance-records/1/rating`

---

## 数据库字段说明

### MaintenanceItem 表相关字段
- `reminder` (VARCHAR): 存储催单信息，用户提交催单时更新此字段
- `score` (INT): 存储用户评分（1-5），初始为NULL，评分后不可修改
- `updateTime` (DATETIME): 每次更新催单或评分时自动更新

---

## 注意事项

1. **权限控制**：所有操作都需要验证用户对维修项目的所有权
2. **状态检查**：催单和评分有严格的状态限制
3. **数据验证**：评分必须在1-5范围内，催单信息不能为空
4. **业务逻辑**：
   - 催单信息会覆盖之前的催单内容
   - 评分只能提交一次，不可修改
   - 每次操作都会更新`updateTime`字段
