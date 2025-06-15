# Motify 工资管理 API 文档

## 概述

本API用于工资记录的查询、统计和结算，支持维修人员工资历史、单月工资、年度统计、工资摘要等功能。

---

## 端点列表

### 1. 获取指定月份的工资记录
- **GET** `/api/wages/month/{year}/{month}?repairmanId=1`
- **参数：**
  - `year` (路径参数, int): 年份
  - `month` (路径参数, int): 月份 (1-12)
  - `repairmanId` (查询参数, long): 维修人员ID（必需）
- **响应：**
  - 有记录：
    ```json
    {
      "code": 200,
      "message": "success",
      "data": { ...Wage对象... }
    }
    ```
  - 无记录：
    ```json
    {
      "code": 200,
      "message": "2024年5月没有工资记录",
      "data": {}
    }
    ```

### 2. 获取指定维修人员的工资记录
- **GET** `/api/wages/repairman/{repairmanId}`
- **参数：**
  - `repairmanId` (路径参数, long): 维修人员ID
- **响应：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [ { ...Wage对象... } ]
  }
  ```

### 3. 手动触发月度工资结算
- **POST** `/api/wages/calculate`
- **Body：**
  ```json
  {
    "year": 2024,   // 可选，默认为上个月
    "month": 5      // 可选，默认为上个月
  }
  ```
- **响应：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": {
      "message": "2024年5月的工资已计算完成"
    }
  }
  ```

### 4. 获取维修人员工资历史
- **GET** `/api/wages/repairman/{repairmanId}/history`
- **参数：**
  - `repairmanId` (路径参数, long): 维修人员ID
- **响应：**
  ```json
  {
    "code": 200,
    "message": "success",
    "data": [ { ...Wage对象... } ]
  }
  ```

### 5. 获取年度工资统计
- **GET** `/api/wages/yearly-stats?repairmanId=1&year=2024`
- **参数：**
  - `repairmanId` (查询参数, long): 维修人员ID（必需）
  - `year` (查询参数, int, 可选): 年份，默认为当前年
- **响应：**
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
      "monthlyDetails": [ { ...Wage对象... } ]
    }
  }
  ```

### 6. 获取工资统计摘要
- **GET** `/api/wages/summary?repairmanId=1`
- **参数：**
  - `repairmanId` (查询参数, long): 维修人员ID（必需）
- **响应：**
  - 有记录：
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
  - 无记录：
    ```json
    {
      "code": 200,
      "message": "没有工资记录",
      "data": {}
    }
    ```

---

## Wage 实体字段说明
- `id`: Long
- `repairmanId`: Long
- `year`: Integer
- `month`: Integer
- `totalIncome`: Double
- `totalWorkHours`: Double
- `createdDate`: LocalDate
- 其他字段请参考实际实体定义

---

## 错误处理
- `400 Bad Request`: 请求参数无效
- `401 Unauthorized`: 未认证或认证失败
- `500 Internal Server Error`: 服务器内部错误

---

**注意：** 本文档基于最新Controller实现自动生成，具体字段和业务逻辑请以实际代码为准。
