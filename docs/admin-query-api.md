# 管理员查询API文档

## 概述

管理员查询功能允许系统管理员查询所有用户、维修人员、车辆和维修工单的信息，用于系统监控和数据分析。

## API接口

### 1. 获取管理员信息

**接口地址**: `GET /api/admin/{adminId}`

**请求参数**:
- `adminId` (路径参数): 管理员ID

**响应示例**:
```json
{
    "success": true,
    "data": {
        "adminId": 1,
        "username": "admin",
        "name": "系统管理员",
        "email": "admin@example.com",
        "active": true,
        "lastLoginTime": "2025-06-12T21:00:00"
    }
}
```

### 2. 获取所有用户列表

**接口地址**: `GET /api/admin/users`

**功能**: 获取系统中所有注册用户的信息

**响应示例**:
```json
{
    "message": "获取用户列表成功",
    "count": 10,
    "data": [
        {
            "userId": 1,
            "username": "user001",
            "password": null,
            "name": "张三",
            "phone": "13900139001",
            "email": "zhangsan@example.com",
            "address": "北京市朝阳区建国路88号"
        },
        {
            "userId": 2,
            "username": "user002",
            "password": null,
            "name": "李四",
            "phone": "13900139002",
            "email": "lisi@example.com",
            "address": "上海市浦东新区陆家嘴环路999号"
        },
        {
            "userId": 3,
            "username": "user003",
            "password": null,
            "name": "王五",
            "phone": "13900139003",
            "email": "wangwu@example.com",
            "address": "广州市天河区珠江路123号"
        },
        {
            "userId": 4,
            "username": "user004",
            "password": null,
            "name": "赵六",
            "phone": "13900139004",
            "email": "zhaoliu@example.com",
            "address": "深圳市南山区科技园南区"
        },
        {
            "userId": 5,
            "username": "user005",
            "password": null,
            "name": "钱七",
            "phone": "13900139005",
            "email": "qianqi@example.com",
            "address": "杭州市西湖区文三路258号"
        }
    ],
    "success": true
}
```

**注意**: 响应中不包含用户密码字段，保护用户隐私。

### 3. 获取所有维修人员列表

**接口地址**: `GET /api/admin/repairmen`

**功能**: 获取系统中所有注册维修人员的信息

**响应示例**:
```json
{
    "message": "获取维修人员列表成功",
    "count": 7,
    "data": [
        {
            "repairmanId": 1,
            "username": "repairman01",
            "password": null,
            "name": "张师傅",
            "phone": "13800138001",
            "email": "zhang@motify.com",
            "gender": "男",
            "type": "MECHANIC",
            "hourlyRate": 80.0
        },
        {
            "repairmanId": 2,
            "username": "repairman02",
            "password": null,
            "name": "李师傅",
            "phone": "13800138002",
            "email": "li@motify.com",
            "gender": "男",
            "type": "ELECTRICIAN",
            "hourlyRate": 60.0
        },
        {
            "repairmanId": 3,
            "username": "repairman03",
            "password": null,
            "name": "王师傅",
            "phone": "13800138003",
            "email": "wang@motify.com",
            "gender": "男",
            "type": "BODYWORKER",
            "hourlyRate": 45.0
        },
        {
            "repairmanId": 4,
            "username": "repairman04",
            "password": null,
            "name": "赵师傅",
            "phone": "13800138004",
            "email": "zhao@motify.com",
            "gender": "男",
            "type": "PAINTER",
            "hourlyRate": 100.0
        }
    ],
    "success": true
}
```

**维修人员类型说明**:
- `MECHANIC`: 机械维修师
- `PAINTER`: 喷漆师
- `APPRENTICE`: 学徒

### 4. 获取所有车辆列表

**接口地址**: `GET /api/admin/cars`

**功能**: 获取系统中所有注册车辆的信息

**响应示例**:
```json
{
    "message": "获取车辆列表成功",
    "count": 11,
    "data": [
        {
            "carId": 1,
            "brand": "丰田",
            "model": "凯美瑞",
            "licensePlate": "京A12345"
        },
        {
            "carId": 2,
            "brand": "本田",
            "model": "雅阁",
            "licensePlate": "沪B67890"
        },
        {
            "carId": 3,
            "brand": "大众",
            "model": "帕萨特",
            "licensePlate": "粤C13579"
        },
        {
            "carId": 4,
            "brand": "奥迪",
            "model": "A4L",
            "licensePlate": "深D24680"
        }
    ],
    "success": true
}
```

### 5. 获取所有维修工单列表

**接口地址**: `GET /api/admin/maintenance-items`

**功能**: 获取系统中所有维修工单的信息

**响应示例**:
```json
{
    "success": true,
    "message": "获取维修工单列表成功",
    "count": 2,
    "data": [
        {
            "itemId": 1,
            "name": "发动机维修",
            "description": "发动机异响需要检修",
            "status": "IN_PROGRESS",
            "progress": 50,
            "cost": 800.0,
            "createTime": "2025-06-10T09:00:00",
            "updateTime": "2025-06-11T14:30:00",
            "car": {
                "carId": 1,
                "brand": "Toyota",
                "model": "Camry",
                "licensePlate": "京A12345"
            },
            "repairmen": [
                {
                    "repairmanId": 1,
                    "name": "王师傅",
                    "type": "MECHANIC"
                }
            ]
        },
        {
            "itemId": 2,
            "name": "车身喷漆",
            "description": "车门刮蹭需要重新喷漆",
            "status": "COMPLETED",
            "progress": 100,
            "cost": 1200.0,
            "createTime": "2025-06-08T10:00:00",
            "updateTime": "2025-06-12T16:00:00",
            "car": {
                "carId": 2,
                "brand": "Honda", 
                "model": "Accord",
                "licensePlate": "京B67890"
            },
            "repairmen": [
                {
                    "repairmanId": 2,
                    "name": "赵师傅",
                    "type": "PAINTER"
                }
            ]
        }
    ]
}
```

**维修状态说明**:
- `PENDING`: 待接受
- `IN_PROGRESS`: 进行中
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消

### 5. 获取所有历史维修记录

**接口地址**: `GET /api/admin/maintenance-records`

**功能**: 获取系统中所有历史维修记录的详细信息

**响应示例**:
```json
{
    "success": true,
    "message": "获取历史维修记录列表成功",
    "count": 3,
    "data": [
        {
            "recordId": 1,
            "name": "发动机维修记录",
            "description": "更换了机油和机滤",
            "cost": 500.0,
            "repairManId": 2,
            "workHours": 120,
            "startTime": "2025-06-10T09:00:00",
            "maintenanceItem": {
                "itemId": 1,
                "name": "发动机维修",
                "status": "COMPLETED"
            }
        },
        {
            "recordId": 2,
            "name": "刹车系统检修记录",
            "description": "更换刹车片和刹车油",
            "cost": 800.0,
            "repairManId": 3,
            "workHours": 180,
            "startTime": "2025-06-11T14:00:00",
            "maintenanceItem": {
                "itemId": 2,
                "name": "刹车系统检修",
                "status": "COMPLETED"
            }
        }
    ]
}
```

### 6. 获取所有工时费发放记录

**接口地址**: `GET /api/admin/wages`

**功能**: 获取系统中所有维修人员的工时费发放记录

**响应示例**:
```json
{
    "success": true,
    "message": "获取工时费发放记录列表成功",
    "count": 4,
    "data": [
        {
            "id": 1,
            "repairmanId": 2,
            "year": 2024,
            "month": 12,
            "totalWorkHours": 40.0,
            "totalIncome": 3200.0,
            "settlementDate": "2025-01-01T00:01:00",
            "repairmanName": "李师傅",
            "repairmanType": "MECHANIC",
            "hourlyRate": 80.0
        },
        {
            "id": 2,
            "repairmanId": 3,
            "year": 2024,
            "month": 12,
            "totalWorkHours": 35.0,
            "totalIncome": 3500.0,
            "settlementDate": "2025-01-01T00:01:00",
            "repairmanName": "赵师傅",
            "repairmanType": "PAINTER",
            "hourlyRate": 100.0
        }
    ]
}
```

**工时费记录说明**:
- `totalWorkHours`: 该月总工作时长（小时）
- `totalIncome`: 该月总收入
- `settlementDate`: 工资结算日期
- `repairmanType`: 维修人员类型（MECHANIC、PAINTER、ELECTRICIAN等）
- `hourlyRate`: 时薪标准

## 错误响应

当请求失败时，API返回以下格式的错误响应：

```json
{
    "success": false,
    "message": "错误描述信息"
}
```

常见错误码：
- `404`: 资源不存在
- `500`: 服务器内部错误

## 安全说明

1. 所有接口都需要管理员权限
2. 用户和维修人员密码字段在响应中被自动清除
3. 建议在生产环境中添加适当的访问控制和日志记录

## 使用示例

### 使用curl获取用户列表
```bash
curl -X GET "http://localhost:8080/api/admin/users" \
     -H "Content-Type: application/json"
```

### 使用curl获取维修工单列表
```bash
curl -X GET "http://localhost:8080/api/admin/maintenance-items" \
     -H "Content-Type: application/json"
```

## 性能优化建议

1. **分页支持**: 对于大量数据，建议添加分页参数
2. **过滤条件**: 可以添加日期范围、状态等过滤条件
3. **字段选择**: 允许客户端指定需要返回的字段
4. **缓存策略**: 对于不频繁变化的数据可以考虑缓存

## 扩展功能

未来可以考虑添加的功能：
1. 数据导出（CSV、Excel格式）
2. 统计分析接口
3. 实时数据推送
4. 数据备份和恢复
