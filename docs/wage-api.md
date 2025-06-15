# 工资相关 API 文档

## 1. 获取指定月份所有维修人员工资记录
- **请求方式**：GET
- **路径**：`/api/wages/{year}/{month}`
- **参数**：
  - `year`（路径参数）：年份（如 2024）
  - `month`（路径参数）：月份（1-12）
- **返回示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "wageId": 1,
      "repairman": { ... },
      "year": 2024,
      "month": 6,
      "totalWorkHours": 120.5,
      "totalIncome": 5000.0
    }
  ]
}
```

---

## 2. 获取指定维修人员的所有工资记录
- **请求方式**：GET
- **路径**：`/api/wages/repairman/{repairmanId}`
- **参数**：
  - `repairmanId`（路径参数）：维修人员ID
- **返回示例**：同上，data 为工资记录数组

---

## 3. 手动触发月度工资结算
- **请求方式**：POST
- **路径**：`/api/wages/calculate`
- **请求体**（JSON）：
```json
{
  "year": 2024,
  "month": 6
}
```
- **返回示例**：
```json
{
  "code": 200,
  "message": "2024年6月的工资已计算完成",
  "data": { "message": "2024年6月的工资已计算完成" }
}
```

---

## 4. 获取维修人员工资历史
- **请求方式**：GET
- **路径**：`/api/wages/repairman/{repairmanId}/history`
- **参数**：
  - `repairmanId`（路径参数）：维修人员ID
- **返回示例**：同上

---

## 5. 获取当前维修人员某月工资
- **请求方式**：GET
- **路径**：`/api/wages/my/{year}/{month}?repairmanId={repairmanId}`
- **参数**：
  - `year`、`month`（路径参数）
  - `repairmanId`（查询参数）
- **返回示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "wageId": 1,
    "year": 2024,
    "month": 6
  }
}
```

---

## 6. 获取当前维修人员年度工资统计
- **请求方式**：GET
- **路径**：`/api/wages/my/yearly-stats?repairmanId={repairmanId}&year={year}`
- **参数**：
  - `repairmanId`（查询参数，必填）
  - `year`（查询参数，选填，默认当前年）
- **返回示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "year": 2024,
    "totalIncome": 60000,
    "totalWorkHours": 1200,
    "averageMonthlyIncome": 5000,
    "averageHourlyRate": 50,
    "highestMonth": 5,
    "highestMonthIncome": 8000,
    "workingMonths": 12,
    "monthlyDetails": [ ... ]
  }
}
```

---

## 7. 获取当前维修人员工资摘要
- **请求方式**：GET
- **路径**：`/api/wages/my/summary?repairmanId={repairmanId}`
- **参数**：
  - `repairmanId`（查询参数）
- **返回示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalIncome": 60000,
    "totalWorkHours": 1200,
    "totalMonths": 12,
    "averageMonthlyIncome": 5000,
    "recent3MonthsIncome": 15000,
    "highestMonth": "2024年5月",
    "highestMonthIncome": 8000,
    "lowestMonth": "2024年2月",
    "lowestMonthIncome": 3000,
    "yearlyTrend": {
      "2023": 40000,
      "2024": 60000
    }
  }
}
```

---

## 通用返回结构
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
``` 