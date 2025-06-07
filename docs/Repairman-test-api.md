# Motify 系统维修人员API测试文档

本文件基于 `docs/data.sql` 的初始数据和已实现的Controller，提供维修人员相关API的标准测试示例。

---

## 1. 维修人员注册

**POST** `/api/repairman/register`

```json
{
  "username": "repairman01",
  "password": "repair124",
  "name": "张师傅",
  "gender": "男",
  "type": "MECHANIC",
  "phone": "13800138001",
  "email": "zhang@motify.com"
}
```

---

## 2. 维修人员登录

**POST** `/api/repairman/login`

```json
{
  "username": "repairman01",
  "password": "repair124"
}
```

---

## 3. 获取维修人员信息

**POST** `/api/repairman/info`

```json
{
  "repairmanId": 1
}
```

---

## 4. 更新维修人员信息

**POST** `/api/repairman/update`

```json
{
  "repairmanId": 1,
  "username": "repairman01",
  "password": "repair124",
  "name": "张师傅",
  "gender": "男",
  "type": "MECHANIC",
  "phone": "13800138001",
  "email": "zhang_updated@motify.com"
}
```

---

## 5. 获取维修人员的维修项目列表

**POST** `/api/repairman/maintenance-items/list`

```json
{
  "repairmanId": 1
}
```

---

## 6. 接收维修工单

### JSON请求体方式

**POST** `/api/repairman/maintenance-items/accept`

```json
{
  "repairmanId": 1,
  "itemId": 5
}
```

### 路径参数方式

**POST** `/api/repairman/1/maintenance-items/5/accept`

---

## 7. 拒绝维修工单

**POST** `/api/repairman/1/maintenance-items/5/reject`

```json
{
  "reason": "当前任务繁忙，无法接单"
}
```

---

## 8. 获取当前进行中工单

**POST** `/api/repairman/current-records`

```json
{
  "repairmanId": 1
}
```

---

## 9. 获取已完成工单

**POST** `/api/repairman/completed-records`

```json
{
  "repairmanId": 1
}
```

---

## 10. 更新工单进度

**PUT** `/api/repairman/1/maintenance-items/5/progress`

```json
{
  "progress": 50,
  "description": "已完成系统检测，准备更换零件"
}
```

---

## 11. 完成维修工单

**POST** `/api/repairman/1/maintenance-items/5/complete`

```json
{
  "result": "空调压缩机已更换，制冷恢复正常",
  "workingHours": 2.5,
  "materials": [
    {
      "materialId": 18,
      "quantity": 1
    },
    {
      "materialId": 14, 
      "quantity": 2
    }
  ]
}
```

---

## 12. 查询收入统计

**GET** `/api/repairman/1/income?startDate=2024-05-01&endDate=2024-06-30`

---

## 13. 保存维修项目

**POST** `/api/repairman/maintenance-items`

```json
{
  "name": "空调维修",
  "description": "更换空调压缩机",
  "car": {
    "carId": 1
  },
  "repairmen": [
    {
      "repairmanId": 1
    }
  ],
  "progress": 0,
  "status":"IN_PROGRESS",
  "cost": 0.0
}
```

---

## 14. 添加维修记录

**POST** `/api/repairman/maintenance-records/add`

```json
{
  "maintenanceItemId": 3,
  "description": "补胎",
  "repairmanId": 1,
  "workHours": 1,
  "startTime": "2024-06-02T10:00:00",
  "name": "补胎记录", // 可选
  "materials": [
    {"materialId": 9, "amount": 1},
    {"materialId": 10, "amount": 1}
  ]
}
```

- <b>说明：</b> 用于维修人员在每次维修后，记录本次维修的内容、工时、开始时间及所用材料。
- <b>参数：</b>
  - maintenanceItemId：维修项目ID（必填）
  - description：维修内容（必填）
  - repairmanId：维修人员ID（必填）
  - workHours：工时（单位：小时，必填）
  - startTime：维修开始时间（必填，格式：yyyy-MM-ddTHH:mm:ss）
  - name：维修记录名称（可选，未传则后端自动生成）
  - materials：材料及用量列表（每项含 materialId 和 amount，必填）
- <b>返回：</b> 新增的维修记录对象

---

## 15. 自动分配多工种维修工单

**POST** `/api/repair/submit`

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

- <b>说明：</b> 创建需要多个工种协作的维修工单，系统自动分配合适的维修人员。
- <b>参数：</b>
  - userId：用户ID（必填）
  - carId：车辆ID（必填）
  - name：工单名称（必填）
  - description：工单描述（必填）
  - requiredTypes：需要的工种及数量（必填，格式为工种类型:数量）
- <b>返回：</b> 新创建的工单对象，包含已分配的维修人员信息

---

## 16. 拒绝工单并自动重新分配

**PUT** `/api/repairman/{repairmanId}/reject/{itemId}`

```json
{
  "reason": "当前工作量过大，无法承接"
}
```

- <b>说明：</b> 维修人员拒绝分配的工单，系统会自动找到下一个合适的维修人员进行分配。
- <b>参数：</b>
  - repairmanId：维修人员ID（路径参数）
  - itemId：工单ID（路径参数）
  - reason：拒绝原因（选填）
- <b>返回：</b> 更新后的工单对象，包含新分配的维修人员信息

---

# 完整业务流程测试设计

## 1. 维修人员端典型业务流

1. **注册新账号**
   - POST `/api/repairman/register`
   - body: `{ "username": "newrepairman", "password": "repair124", "name": "新维修工", "gender": "男", "type": "MECHANIC", "phone": "13800138099", "email": "new@motify.com" }`
   - 预期：返回 200，包含新创建的 repairman 信息

2. **登录**
   - POST `/api/repairman/login`
   - body: `{ "username": "repairman01", "password": "repair124" }`
   - 预期：返回 200，包含 repairman 信息

3. **获取个人信息**
   - POST `/api/repairman/info`
   - body: `{ "repairmanId": 1 }`
   - 预期：返回 200，包含 repairman 详细信息

4. **查询所有工单**
   - POST `/api/repairman/maintenance-items/list`
   - body: `{ "repairmanId": 1 }`
   - 预期：返回 200，工单列表

5. **查询当前工单**
   - POST `/api/repairman/current-records`
   - body: `{ "repairmanId": 1 }`
   - 预期：返回 200，当前工单列表

6. **查询已完成工单**
   - POST `/api/repairman/completed-records`
   - body: `{ "repairmanId": 1 }`
   - 预期：返回 200，已完成工单列表

7. **接收新工单**
   - POST `/api/repairman/1/maintenance-items/5/accept`
   - 预期：返回 200，包含工单信息和成功状态码

8. **拒绝工单**
   - POST `/api/repairman/1/maintenance-items/5/reject`
   - body: `{ "reason": "当前任务繁忙，无法接单" }`
   - 预期：返回 200，包含工单信息和成功状态码

9. **更新工单进度**
   - PUT `/api/repairman/1/maintenance-items/5/progress`
   - body: `{ "progress": 50, "description": "已完成系统检测，准备更换零件" }`
   - 预期：返回 200，包含更新后的工单信息

10. **完成工单**
    - POST `/api/repairman/1/maintenance-items/5/complete`
    - body: `{ "result": "空调压缩机已更换，制冷恢复正常", "workingHours": 2.5, "materials": [{ "materialId": 18, "quantity": 1 }] }`
    - 预期：返回 200，包含完成后的工单信息

11. **查询收入统计**
    - GET `/api/repairman/1/income?startDate=2024-05-01&endDate=2024-06-30`
    - 预期：返回 200，包含收入统计数据

12. **添加维修记录**
    - POST `/api/repairman/maintenance-records/add`
    - body: `{ "maintenanceItemId": 3, "description": "补胎", "repairmanId": 1, "workHours": 1, "startTime": "2024-06-02T10:00:00", "materials": [{ "materialId": 9, "amount": 1 }] }`
    - 预期：返回 200，包含新建的维修记录信息

---

## 2. 典型异常流程

- **登录失败**
  - POST `/api/repairman/login`
  - body: `{ "username": "repairman01", "password": "wrongpass" }`
  - 预期：返回 400，认证失败

- **接单失败**
  - POST `/api/repairman/1/maintenance-items/5/accept`
  - 预期：返回 400，错误信息"工单已被其他维修人员接收"

- **更新进度失败**
  - PUT `/api/repairman/1/maintenance-items/5/progress`
  - body: `{ "progress": 110, "description": "无效进度" }`
  - 预期：返回 400，错误信息"维修进度必须在0-100之间"

- **完成不属于自己的工单**
  - POST `/api/repairman/1/maintenance-items/6/complete`
  - 预期：返回 400，错误信息"该工单不属于此维修人员"

---

> **说明：**
>
> - 所有接口均需设置 `Content-Type: application/json`
> - 大部分API采用JSON body传参风格
> - 返回格式格式统一为`{ "code": 状态码, "message": "状态信息", "data": 返回数据 }`
> - 实际密码为SHA256加密后的哈希值
> - 可根据实际数据调整ID和参数

---

# 多工种自动分配业务流程测试

## 1. 多工种维修工单流程

1. **创建多工种维修工单**
   - POST `/api/repair/submit`
   - body: `{ "userId": 1, "carId": 1, "name": "多工种维修工单", "description": "需要多个工种协作维修", "requiredTypes": { "MECHANIC": 2, "PAINTER": 1, "APPRENTICE": 1 } }`
   - 预期：返回 200，包含自动分配的多个不同工种维修人员

2. **维修人员拒绝工单**
   - PUT `/api/repairman/1/reject/5`
   - body: `{ "reason": "当前工作量过大，无法承接" }`
   - 预期：返回 200，包含更新后的工单信息，原维修人员被移除，系统自动分配新维修人员
