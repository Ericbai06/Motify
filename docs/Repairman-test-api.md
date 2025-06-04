# Motify 系统 Postman 测试用例示例

本文件基于 `docs/data.sql` 的初始数据，提供常用接口的 Postman 测试示例，便于开发和联调。

---

## 1. 维修人员登录

### 张师傅登录

**POST** `/api/repairman/login`

```json
{
  "username": "repairman01",
  "password": "repair123"
}
```

---

## 2. 获取维修人员信息

### 获取 repairmanId=1（张师傅）信息

**GET** `/api/repairman/1`

---

## 3. 维修人员接收工单

### 张师傅接收"空调维修"工单（itemId=5）

**POST** `/api/repairman/1/maintenance-items/5/accept`

---

## 4. 维修人员拒绝工单

### 张师傅拒绝"空调维修"工单（itemId=5）

**POST** `/api/repairman/1/maintenance-items/5/reject`

```json
{
  "reason": "当前任务繁忙，无法接单"
}
```

---

## 5. 用户登录

### 张三登录

**POST** `/api/auth/users/login`

```json
{
  "username": "user001",
  "password": "user123"
}
```

---

## 6. 获取用户车辆列表

### 张三（userId=1）车辆列表

**GET** `/api/auth/users/1/cars`

---

## 7. 用户提交维修请求

### 张三为"丰田 凯美瑞"提交维修请求

**POST** `/api/auth/users/1/maintenance-records`

```json
{
  "carId": 1,
  "name": "发动机异响检查",
  "description": "发动机有异响，请检查"
}
```

---

## 8. 用户催单

### 张三对"空调维修"工单（itemId=5）催单

**POST** `/api/auth/users/1/maintenance-records/5/rush-order`

```json
{
  "reminderMessage": "请尽快修好空调，天气太热了"
}
```

---

## 9. 用户对已完成工单评分

### 张三对"发动机保养"工单（itemId=1）评分

**POST** `/api/auth/users/1/maintenance-records/1/rating`

```json
{
  "score": 5
}
```

---

## 10. 获取维修项目详情

### 获取"发动机保养"工单详情

**GET** `/api/auth/users/1/maintenance-records/1`

---

## 11. 维修人员查询自己所有工单

### 张师傅（repairmanId=1）查询工单

**GET** `/api/repairman/1/maintenance-items`

---

## 12. 维修人员查询当前进行中工单

### 张师傅（repairmanId=1）查询当前工单

**GET** `/api/repairman/1/current-records`

---

## 13. 维修人员查询已完成工单

### 张师傅（repairmanId=1）查询已完成工单

**GET** `/api/repairman/1/completed-records`

---

## 14. 维修人员更新工单进度

### 张师傅更新"空调维修"工单进度

**PUT** `/api/repairman/1/maintenance-items/5/progress`

```json
{
  "progress": 50,
  "description": "已完成系统检测，准备更换零件"
}
```

---

## 15. 维修人员提交维修结果

### 张师傅提交"空调维修"工单结果

**POST** `/api/repairman/1/maintenance-items/5/complete`

```json
{
  "result": "空调压缩机已更换，制冷恢复正常",
  "materials": [
    {
      "materialId": 18,
      "quantity": 1,
      "price": 68.0
    }
  ],
  "workingHours": 2.5
}
```

---

## 16. 维修人员查询收入统计

### 张师傅（repairmanId=1）查询收入

**GET** `/api/repairman/1/income?startDate=2024-05-01&endDate=2024-06-30`

---

# 完整业务流程测试设计

## 1. 维修人员端典型业务流

1. **登录**
   - POST `/api/repairman/login`
   - body: `{ "username": "repairman01", "password": "repair123" }`
   - 预期：返回 200，包含 repairman 信息

2. **获取个人信息**
   - GET `/api/repairman/1`
   - 预期：返回 200，包含 repairman 详细信息

3. **查询所有工单**
   - GET `/api/repairman/1/maintenance-items`
   - 预期：返回 200，工单列表

4. **查询当前工单**
   - GET `/api/repairman/1/current-records`
   - 预期：返回 200，当前工单列表

5. **查询已完成工单**
   - GET `/api/repairman/1/completed-records`
   - 预期：返回 200，已完成工单列表

6. **接收新工单**
   - POST `/api/repairman/1/maintenance-items/5/accept`
   - 预期：返回 200，工单状态变为"已接单"

7. **拒绝工单**
   - POST `/api/repairman/1/maintenance-items/5/reject`
   - body: `{ "reason": "当前任务繁忙，无法接单" }`
   - 预期：返回 200，工单状态变为"已拒绝"

8. **更新工单进度**
   - PUT `/api/repairman/1/maintenance-items/5/progress`
   - body: `{ "progress": 50, "description": "已完成系统检测，准备更换零件" }`
   - 预期：返回 200，工单进度更新

9. **完成工单**
   - POST `/api/repairman/1/maintenance-items/5/complete`
   - body: `{ "result": "...", "materials": [...], "workingHours": 2.5 }`
   - 预期：返回 200，工单状态变为"已完成"

10. **查询收入统计**
    - GET `/api/repairman/1/income?startDate=2024-05-01&endDate=2024-06-30`
    - 预期：返回 200，收入数据

---

## 2. 典型异常流程

- **登录失败**
  - POST `/api/repairman/login`
  - body: `{ "username": "repairman01", "password": "wrongpass" }`
  - 预期：返回 400/401，认证失败

- **接单失败**
  - POST `/api/repairman/1/maintenance-items/5/accept`
  - 预期：返回 400/409，提示"工单已被接收"

- **更新进度失败**
  - PUT `/api/repairman/1/maintenance-items/5/progress`
  - body: `{ "progress": 110, "description": "无效进度" }`
  - 预期：返回 400，进度超出范围

---

> **说明：**
>
> - 所有接口均需设置 `Content-Type: application/json`。
> - 需要登录的接口请先获取并带上相应的认证信息（如有Token机制）。
> - 可根据实际数据调整ID和参数。

---
