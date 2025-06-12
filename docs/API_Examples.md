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

# 工资结算系统 API 文档

## 概述
工资结算系统为维修人员提供工资计算、查询和统计功能，主要包括：
1. **工时结算**：系统根据维修记录自动计算维修人员工时和收入
2. **月度自动结算**：每月自动汇总并生成工资记录
3. **历史数据查询**：提供多维度的工资历史查询
4. **统计分析**：提供年度工资、趋势分析等统计功能

## API 端点

### 1. 管理员接口：获取指定月份的工资记录

**GET** `/api/wages/{year}/{month}`

#### 请求参数
- `year` (路径参数): 年份
- `month` (路径参数): 月份(1-12)

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "repairmanId": 1,
      "repairmanName": "张师傅",
      "repairmanType": "MECHANIC",
      "year": 2023,
      "month": 6,
      "totalWorkHours": 160.5,
      "totalIncome": 12840.0,
      "hourlyRate": 80.0,
      "settlementDate": "2023-07-01T00:01:00"
    },
    {
      "id": 2,
      "repairmanId": 2,
      "repairmanName": "李师傅",
      "repairmanType": "PAINTER",
      "year": 2023,
      "month": 6,
      "totalWorkHours": 145.0,
      "totalIncome": 14500.0,
      "hourlyRate": 100.0,
      "settlementDate": "2023-07-01T00:01:00"
    }
  ]
}
```

### 2. 管理员接口：获取指定维修人员的工资记录

**GET** `/api/wages/repairman/{repairmanId}`

#### 请求参数
- `repairmanId` (路径参数): 维修人员ID

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "repairmanId": 1,
      "repairmanName": "张师傅",
      "repairmanType": "MECHANIC",
      "year": 2023,
      "month": 6,
      "totalWorkHours": 160.5,
      "totalIncome": 12840.0,
      "hourlyRate": 80.0,
      "settlementDate": "2023-07-01T00:01:00"
    },
    {
      "id": 3,
      "repairmanId": 1,
      "repairmanName": "张师傅",
      "repairmanType": "MECHANIC",
      "year": 2023,
      "month": 5,
      "totalWorkHours": 168.0,
      "totalIncome": 13440.0,
      "hourlyRate": 80.0,
      "settlementDate": "2023-06-01T00:01:00"
    }
  ]
}
```

### 3. 管理员接口：手动触发月度工资结算

**POST** `/api/wages/calculate`

#### 请求体
```json
{
  "year": 2023,
  "month": 6
}
```
注：年份和月份参数可选，默认为上个月

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "message": "2023年6月的工资已计算完成"
  }
}
```

#### 业务规则
- 系统会基于指定月份的维修记录计算每个维修人员的工时和收入
- 如果指定月份的工资记录已存在，系统会先删除再重新计算
- 只有对有工作记录的维修人员才会生成工资记录

### 4. 维修人员接口：获取个人工资历史

**GET** `/api/wages/my/history`

#### 请求参数
- 无（基于当前登录用户）

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "repairmanId": 1,
      "repairmanName": "张师傅",
      "repairmanType": "MECHANIC",
      "year": 2023,
      "month": 6,
      "totalWorkHours": 160.5,
      "totalIncome": 12840.0,
      "hourlyRate": 80.0,
      "settlementDate": "2023-07-01T00:01:00"
    },
    {
      "id": 3,
      "repairmanId": 1,
      "repairmanName": "张师傅",
      "repairmanType": "MECHANIC",
      "year": 2023,
      "month": 5,
      "totalWorkHours": 168.0,
      "totalIncome": 13440.0,
      "hourlyRate": 80.0,
      "settlementDate": "2023-06-01T00:01:00"
    }
  ]
}
```

### 5. 维修人员接口：获取指定月份工资详情

**GET** `/api/wages/my/{year}/{month}`

#### 请求参数
- `year` (路径参数): 年份
- `month` (路径参数): 月份(1-12)

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "repairmanId": 1,
    "repairmanName": "张师傅",
    "repairmanType": "MECHANIC",
    "year": 2023,
    "month": 6,
    "totalWorkHours": 160.5,
    "totalIncome": 12840.0,
    "hourlyRate": 80.0,
    "settlementDate": "2023-07-01T00:01:00"
  }
}
```

#### 无记录响应示例
```json
{
  "code": 200,
  "message": "2023年7月没有工资记录",
  "data": {}
}
```

### 6. 维修人员接口：获取年度工资统计

**GET** `/api/wages/my/yearly-stats`

#### 请求参数
- `year` (查询参数，可选): 年份，默认为当前年份

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "year": 2023,
    "totalIncome": 75420.0,
    "totalWorkHours": 942.75,
    "averageMonthlyIncome": 12570.0,
    "averageHourlyRate": 80.0,
    "highestMonth": 5,
    "highestMonthIncome": 13440.0,
    "workingMonths": 6,
    "monthlyDetails": [
      {
        "id": 3,
        "repairmanId": 1,
        "repairmanName": "张师傅",
        "repairmanType": "MECHANIC",
        "year": 2023,
        "month": 1,
        "totalWorkHours": 152.25,
        "totalIncome": 12180.0,
        "hourlyRate": 80.0,
        "settlementDate": "2023-02-01T00:01:00"
      },
      // ... 其他月份数据
    ]
  }
}
```

#### 无记录响应示例
```json
{
  "code": 200,
  "message": "2024年没有工资记录",
  "data": {}
}
```

### 7. 维修人员接口：获取工资统计摘要

**GET** `/api/wages/my/summary`

#### 请求参数
- 无（基于当前登录用户）

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalIncome": 128640.0,
    "totalWorkHours": 1608.0,
    "totalMonths": 12,
    "averageMonthlyIncome": 10720.0,
    "recent3MonthsIncome": 38760.0,
    "highestMonth": "2023年5月",
    "highestMonthIncome": 13440.0,
    "lowestMonth": "2023年2月",
    "lowestMonthIncome": 9600.0,
    "yearlyTrend": {
      "2022": 53040.0,
      "2023": 75600.0
    }
  }
}
```

#### 无记录响应示例
```json
{
  "code": 200,
  "message": "没有工资记录",
  "data": {}
}
```

## 自动结算机制

### 月度自动结算
系统会在每月1日自动结算上个月的工资记录。该过程由定时任务自动执行，不需要手动干预。

### 历史数据初始化
系统在启动时会自动检查并结算历史维修记录的工资数据，确保数据完整性。

## 数据库字段说明

### Wage 表字段
- `id` (BIGINT): 主键ID
- `repairman_id` (BIGINT): 维修人员ID，外键关联repairmen表
- `year` (INT): 年份
- `month` (INT): 月份
- `total_work_hours` (DOUBLE): 总工作时长（小时）
- `total_income` (DOUBLE): 总收入
- `settlement_date` (DATETIME): 结算日期
- `repairman_name` (VARCHAR): 维修人员姓名（冗余存储）
- `repairman_type` (VARCHAR): 维修人员类型（冗余存储）
- `hourly_rate` (DOUBLE): 小时工资率（冗余存储）

## 使用流程示例

### 场景1：管理员查看并重新计算工资
1. 查看特定月份的工资记录：`GET /api/wages/2023/6`
2. 发现数据不准确，重新计算：`POST /api/wages/calculate` (body: `{"year": 2023, "month": 6}`)
3. 再次查看更新后的工资记录：`GET /api/wages/2023/6`

### 场景2：维修人员查看个人工资数据
1. 查看工资总体情况：`GET /api/wages/my/summary`
2. 查看年度详细数据：`GET /api/wages/my/yearly-stats?year=2023`
3. 查看特定月份详情：`GET /api/wages/my/2023/6`

## 注意事项

1. **权限控制**：
   - 管理员可以查看所有维修人员的工资
   - 维修人员只能查看自己的工资信息
   - 需要正确的认证和授权才能访问API

2. **数据计算规则**：
   - 工作时长基于维修记录中的工时（分钟）转换为小时
   - 收入 = 工作时长 × 小时工资率
   - 统计数据自动计算，无需手动干预

3. **数据完整性**：
   - 系统启动时自动检查并补充缺失的历史工资记录
   - 每月自动结算确保数据及时更新
