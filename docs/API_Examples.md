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

# 多工种维修人员自动分配 API 文档

## 概述
多工种维修人员自动分配功能允许系统根据工单需要的工种类型和数量，自动分配合适的维修人员，主要包括：
1. **多工种需求定义**：工单可同时需要多种不同工种的维修人员
2. **自动分配算法**：基于工作量的维修人员自动分配
3. **拒绝与重新分配**：维修人员可以拒绝工单，系统自动重新分配
4. **工单状态管理**：跟踪维修人员接受状态

## API 端点

### 1. 创建需要多工种的维修工单

**POST** `/api/repair/submit`

#### 请求体
```json
{
  "userId": 1,
  "carId": 1,
  "name": "多工种维修工单",
  "description": "需要多个工种协作维修",
  "requiredTypes": {
    "MECHANIC": 2,
    "PAINTER": 1,
    "APPRENTICE": 1
  }
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "itemId": 48,
    "name": "多工种维修工单",
    "description": "需要多个工种协作维修",
    "status": "PENDING",
    "progress": 0,
    "createTime": "2025-06-06T16:41:01.248297",
    "updateTime": "2025-06-06T16:41:01.248316",
    "cost": 0.0,
    "car": {
      "carId": 1,
      "brand": "丰田",
      "model": "凯美瑞",
      "licensePlate": "京A12345"
    },
    "repairmenAcceptance": {
      "Repairman{repairmanId=1, username='repairman01', name='张师傅', type=MECHANIC}": false,
      "Repairman{repairmanId=4, username='repairman04', name='赵师傅', type=PAINTER}": false,
      "Repairman{repairmanId=5, username='repairman05', name='刘师傅', type=APPRENTICE}": false
    },
    "requiredTypes": [],
    "repairmen": [
      {"repairmanId": 1, "name": "张师傅", "type": "MECHANIC", "hourlyRate": 80.0},
      {"repairmanId": 4, "name": "赵师傅", "type": "PAINTER", "hourlyRate": 100.0},
      {"repairmanId": 5, "name": "刘师傅", "type": "APPRENTICE", "hourlyRate": 35.0}
    ]
  }
}
```

#### 业务规则
- 工单会自动分配给合适工种类型的维修人员
- 分配遵循基于工作量的优先级规则，当前工作量低的维修人员优先被分配
- 系统确保为每种工种分配足够数量的维修人员
- 工单创建时维修人员接受状态为false，表示待确认

---

### 2. 维修人员拒绝工单

**PUT** `/api/repairman/{repairmanId}/reject/{itemId}`

#### 请求参数
- `repairmanId` (路径参数): 维修人员ID
- `itemId` (路径参数): 工单ID

#### 请求体
```json
{
  "reason": "太忙了"
}
```

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "itemId": 48,
    "name": "多工种维修工单",
    "description": "需要多个工种协作维修",
    "status": "PENDING",
    "requiredTypes": [
      {"id": 31, "type": "APPRENTICE", "required": 1, "assigned": 1},
      {"id": 32, "type": "MECHANIC", "required": 2, "assigned": 1},
      {"id": 33, "type": "PAINTER", "required": 1, "assigned": 1}
    ],
    "repairmen": [
      {"repairmanId": 2, "name": "李师傅", "type": "MECHANIC", "hourlyRate": 80.0},
      {"repairmanId": 4, "name": "赵师傅", "type": "PAINTER", "hourlyRate": 100.0},
      {"repairmanId": 5, "name": "刘师傅", "type": "APPRENTICE", "hourlyRate": 35.0}
    ]
  }
}
```

#### 业务规则
- 维修人员可以拒绝分配的工单，并提供拒绝原因
- 当维修人员拒绝工单后，系统会自动找到下一个合适的维修人员进行分配
- 如果无法找到合适的替代维修人员，工单的required工种数量将大于assigned数量
- 系统会根据工种类型进行匹配，确保新分配的维修人员工种类型符合需求

---

## 使用流程示例

### 场景：创建多工种协作工单并处理拒绝

1. 管理员或用户创建需要多工种协作的维修工单
   ```
   POST /api/repair/submit
   {
     "userId": 1,
     "carId": 1,
     "name": "车辆综合大修",
     "description": "需要机械师修理发动机，喷漆工重新喷漆，学徒协助",
     "requiredTypes": {
       "MECHANIC": 2,
       "PAINTER": 1,
       "APPRENTICE": 1
     }
   }
   ```

2. 系统自动分配合适的维修人员，返回工单详情

3. 某维修人员拒绝工单
   ```
   PUT /api/repairman/1/reject/48
   {
     "reason": "太忙了"
   }
   ```

4. 系统自动找到下一个合适的维修人员进行重新分配，返回更新后的工单信息
