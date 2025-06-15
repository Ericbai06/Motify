# Motify 维修人员端 API 测试文档

> 详细接口定义、参数、请求/响应示例请参见实际后端返回和实体定义

---

# 1. 账号与信息管理

## 1.1 注册
- POST `/api/repairman/register`
- Body:
  ```json
  {
    "username": "mechanic1",
    "password": "password",
    "name": "机械师1",
    "gender": "男",
    "type": "MECHANIC"
  }
  ```
- 响应:
  ```json
  { "code": 200, "message": "success", "data": { ...维修人员对象... } }
  ```

## 1.2 登录
- POST `/api/repairman/login`
- Body:
  ```json
  { "username": "mechanic1", "password": "password" }
  ```
- 响应: 同上

## 1.3 获取个人信息
- POST `/api/repairman/info`
- Body:
  ```json
  { "repairmanId": 1 }
  ```
- 响应: 同上

## 1.4 更新个人信息
- POST `/api/repairman/update`
- Body: 维修人员对象
- 响应: 同上

---

# 2. 工单与维修业务

## 2.1 查询所有工单
- POST `/api/repairman/maintenance-items/list`
- Body:
  ```json
  { "repairmanId": 1 }
  ```
- 响应:
  ```json
  { "code": 200, "message": "success", "data": [ ...工单列表... ] }
  ```

## 2.2 查询当前工单
- POST `/api/repairman/current-records`
- Body: `{ "repairmanId": 1 }`
- 响应: 同上

## 2.3 查询已完成工单
- POST `/api/repairman/completed-records`
- Body: `{ "repairmanId": 1 }`
- 响应: 同上

## 2.4 查询已拒绝工单
- POST `/api/repairman/rejected-items`
- Body: `{ "repairmanId": 1 }`
- 响应: 同上

## 2.5 接收工单
- POST `/api/repairman/maintenance-items/accept`
- Body:
  ```json
  { "repairmanId": 1, "itemId": 2 }
  ```
- 响应: 同上
- 或路径版 POST `/api/repairman/{repairmanId}/maintenance-items/{itemId}/accept`

## 2.6 拒绝工单
- POST `/api/repairman/{repairmanId}/maintenance-items/{itemId}/reject`
- Body:
  ```json
  { "reason": "工作量过大" }
  ```
- 响应: 同上

## 2.7 更新工单进度
- PUT `/api/repairman/{repairmanId}/maintenance-items/{itemId}/progress`
- Body:
  ```json
  { "progress": 50, "description": "已完成一半" }
  ```
- 响应: 同上

## 2.8 完成工单
- POST `/api/repairman/{repairmanId}/maintenance-items/{itemId}/complete`
- Body:
  ```json
  {
    "result": "维修完成",
    "workingHours": 2.5,
    "materials": [ ...材料用量... ]
  }
  ```
- 响应: 同上

## 2.9 添加维修记录
- POST `/api/repairman/maintenance-records/add`
- Body:
  ```json
  {
    "maintenanceItemId": 3,
    "repairmanId": 1,
    "name": "更换机油",
    "description": "使用5W-30机油",
    "workHours": 1,
    "startTime": "2024-06-02T10:00:00",
    "materials": [ { "materialId": 9, "amount": 1 } ]
  }
  ```
- 响应: 同上

---

# 3. 工资与收入统计

## 3.1 查询工资历史
- GET `/api/wages/repairman/{repairmanId}/history`
- 响应:
  ```json
  { "code": 200, "message": "success", "data": [ ...工资记录... ] }
  ```

## 3.2 查询指定月份工资
- GET `/api/wages/my/{year}/{month}?repairmanId=1`
- 响应:
  ```json
  { "code": 200, "message": "success", "data": { ...工资详情... } }
  ```

## 3.3 查询年度工资统计
- GET `/api/wages/my/yearly-stats?repairmanId=1&year=2024`
- 响应:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "year": 2024,
      "totalIncome": 12345.67,
      "totalWorkHours": 200,
      "averageMonthlyIncome": 1028.8,
      "averageHourlyRate": 61.7,
      "highestMonth": 5,
      "highestMonthIncome": 2000,
      "workingMonths": 12,
      "monthlyDetails": [ ... ]
    }
  }
  ```

## 3.4 查询工资摘要
- GET `/api/wages/my/summary?repairmanId=1`
- 响应:
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "totalIncome": 12345.67,
      "totalWorkHours": 200,
      "totalMonths": 12,
      "averageMonthlyIncome": 1028.8,
      "recent3MonthsIncome": 3000,
      "highestMonth": "2024年5月",
      "highestMonthIncome": 2000,
      "lowestMonth": "2024年2月",
      "lowestMonthIncome": 500,
      "yearlyTrend": { "2023": 8000, "2024": 12345.67 }
    }
  }
  ```

---

# 4. 典型异常流程

- 登录失败：返回404
- 接单/拒单/完成工单失败：返回400，message中有错误原因
- 查询无数据：data为[]或{}，message提示无记录

---

如需更详细的字段说明和响应结构，请参考实际API返回和实体定义。 