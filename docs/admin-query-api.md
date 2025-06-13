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
- `MECHANIC`: 机械维修师（发动机、变速箱等机械部件）
- `ELECTRICIAN`: 电工（电路、电子设备维修）
- `BODYWORKER`: 车身维修工（车身钣金、结构维修）
- `PAINTER`: 喷漆师（车身喷漆、美容）
- `APPRENTICE`: 学徒（初级维修人员）
- `INSPECTOR`: 检查员（质量检查、验收）
- `DIAGNOSER`: 诊断师（故障诊断、技术分析）

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
    "data": [
        {
            "itemId": 6,
            "name": "变速箱保养",
            "description": "更换变速箱油",
            "status": "IN_PROGRESS",
            "progress": 50,
            "result": null,
            "reminder": null,
            "score": null,
            "createTime": "2024-06-02T21:00:00",
            "updateTime": "2024-06-02T22:30:00",
            "completeTime": null,
            "materialCost": 280.0,
            "laborCost": 180.0,
            "cost": 460.0,
            "car": {
                "carId": 6,
                "brand": "奔驰",
                "model": "C级",
                "licensePlate": "川F86420"
            },
            "repairmenAcceptance": {
                "Repairman{repairmanId=1, username='repairman01', name='张师傅', type=MECHANIC}": true,
                "Repairman{repairmanId=5, username='repairman05', name='刘师傅', type=APPRENTICE}": true
            },
            "maintenanceRecords": [
                {
                    "recordId": 6,
                    "name": "变速箱油更换",
                    "description": "更换ATF变速箱油",
                    "cost": 460.0,
                    "repairManId": 1,
                    "workHours": 3,
                    "startTime": "2024-06-02T21:00:00"
                }
            ],
            "requiredTypes": [],
            "repairmen": [
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
                    "repairmanId": 5,
                    "username": "repairman05",
                    "password": null,
                    "name": "刘师傅",
                    "phone": "13800138005",
                    "email": "liu@motify.com",
                    "gender": "女",
                    "type": "APPRENTICE",
                    "hourlyRate": 35.0
                }
            ],
            "pendingRepairmen": [],
            "acceptedRepairmen": [
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
                    "repairmanId": 5,
                    "username": "repairman05",
                    "password": null,
                    "name": "刘师傅",
                    "phone": "13800138005",
                    "email": "liu@motify.com",
                    "gender": "女",
                    "type": "APPRENTICE",
                    "hourlyRate": 35.0
                }
            ]
        },
        {
            "itemId": 1,
            "name": "发动机保养",
            "description": "更换机油、机滤、空滤",
            "status": "COMPLETED",
            "progress": 100,
            "result": "空调压缩机已更换，制冷恢复正常",
            "reminder": "下次保养时间：2024-12-01",
            "score": 5,
            "createTime": "2024-05-15T17:00:00",
            "updateTime": "2025-06-05T20:46:40.075748",
            "completeTime": "2025-06-05T20:46:40.075728",
            "materialCost": 428.0,
            "laborCost": 200.0,
            "cost": 628.0,
            "car": {
                "carId": 1,
                "brand": "丰田",
                "model": "凯美瑞",
                "licensePlate": "京A12345"
            },
            "repairmenAcceptance": {
                "Repairman{repairmanId=1, username='repairman01', name='张师傅', type=MECHANIC}": true,
                "Repairman{repairmanId=2, username='repairman02', name='李师傅', type=ELECTRICIAN}": true
            },
            "maintenanceRecords": [
                {
                    "recordId": 1,
                    "name": "更换机油机滤",
                    "description": "更换5W-30机油和机油滤芯",
                    "cost": 378.0,
                    "repairManId": 1,
                    "workHours": 2,
                    "startTime": "2024-05-15T17:00:00"
                },
                {
                    "recordId": 2,
                    "name": "更换空气滤芯",
                    "description": "更换高效空气滤清器",
                    "cost": 45.0,
                    "repairManId": 2,
                    "workHours": 1,
                    "startTime": "2024-05-15T18:00:00"
                },
                {
                    "recordId": 8,
                    "name": "完成维修：发动机保养",
                    "description": "空调压缩机已更换，制冷恢复正常",
                    "cost": 0.0,
                    "repairManId": 1,
                    "workHours": 150,
                    "startTime": "2025-06-05T20:46:40.077057"
                }
            ],
            "requiredTypes": [],
            "repairmen": [
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
                }
            ],
            "pendingRepairmen": [],
            "acceptedRepairmen": [
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
                }
            ]
        }
    ],
    "count": 11,
    "message": "获取维修工单列表成功"
}
```

**维修工单字段说明**:

**基本信息**:
- `itemId`: 维修工单唯一标识符
- `name`: 维修项目名称
- `description`: 维修项目详细描述

**状态信息**:
- `status`: 维修工单状态（PENDING、IN_PROGRESS、COMPLETED、CANCELLED）
- `progress`: 维修进度（0-100的整数值）
- `result`: 维修结果描述（完成后填写）
- `reminder`: 催单信息或下次保养提醒
- `score`: 用户评分（1-5分，完成后用户可评分）

**时间信息**:
- `createTime`: 工单创建时间
- `updateTime`: 最后更新时间
- `completeTime`: 完成时间（未完成时为null）

**费用信息**:
- `materialCost`: 材料费用
- `laborCost`: 工时费用
- `cost`: 总费用（materialCost + laborCost）

**关联车辆信息**:
- `car`: 关联车辆信息对象
  - `carId`: 车辆ID
  - `brand`: 车辆品牌
  - `model`: 车辆型号
  - `licensePlate`: 车牌号

**维修人员相关信息**:
- `repairmenAcceptance`: 维修人员接受状态映射表
  - 键：维修人员对象字符串表示
  - 值：是否接受该工单（true/false）
- `requiredTypes`: 需要的维修人员类型列表
- `repairmen`: 分配到该工单的所有维修人员列表
- `pendingRepairmen`: 尚未接受工单的维修人员列表
- `acceptedRepairmen`: 已接受该工单的维修人员列表

**维修记录信息**:
- `maintenanceRecords`: 该工单下的所有维修记录列表（详见下方维修记录字段说明）

**维修记录字段说明**:
- `recordId`: 维修记录唯一标识符
- `name`: 维修记录名称
- `description`: 维修记录详细描述
- `cost`: 该维修记录的费用
- `repairManId`: 执行维修的维修人员ID
- `workHours`: 工作时长（分钟为单位）
- `startTime`: 维修开始时间

**维修人员对象字段说明**:
- `repairmanId`: 维修人员唯一标识符
- `username`: 维修人员用户名
- `password`: 密码（在API响应中已清除，显示为null）
- `name`: 维修人员姓名
- `phone`: 联系电话
- `email`: 邮箱地址
- `gender`: 性别
- `type`: 维修人员类型（MECHANIC、ELECTRICIAN、BODYWORKER、PAINTER、APPRENTICE、INSPECTOR、DIAGNOSER）
- `hourlyRate`: 时薪标准

**特殊说明**:
1. `repairmenAcceptance` 字段显示的键格式为维修人员对象的字符串表示，实际使用中建议通过 `acceptedRepairmen` 和 `pendingRepairmen` 字段获取具体的维修人员信息
2. 所有维修人员对象中的 `password` 字段在API响应中都会被清除为 `null`，以保护隐私
3. `workHours` 在维修记录中以分钟为单位存储，显示时可能需要转换为小时
4. 当工单状态为 `COMPLETED` 时，`result`、`completeTime` 和 `score` 字段才会有值

**维修状态说明**:
- `PENDING`: 待接受
- `IN_PROGRESS`: 进行中
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消

### 6. 获取所有历史维修记录

**接口地址**: `GET /api/admin/maintenance-records`

**功能**: 获取系统中所有历史维修记录的详细信息

**响应示例**:
```json
{
    "success": true,
    "message": "获取历史维修记录列表成功",
    "count": 8,
    "data": [
        {
            "recordId": 1,
            "name": "更换机油机滤",
            "description": "更换5W-30机油和机油滤芯",
            "cost": 378.0,
            "repairManId": 1,
            "workHours": 2,
            "startTime": "2024-05-15T17:00:00"
        },
        {
            "recordId": 2,
            "name": "更换空气滤芯",
            "description": "更换高效空气滤清器",
            "cost": 45.0,
            "repairManId": 2,
            "workHours": 1,
            "startTime": "2024-05-15T18:00:00"
        },
        {
            "recordId": 3,
            "name": "更换前刹车片",
            "description": "更换陶瓷前刹车片",
            "cost": 300.0,
            "repairManId": 3,
            "workHours": 2,
            "startTime": "2024-05-20T18:00:00"
        },
        {
            "recordId": 4,
            "name": "更换米其林轮胎",
            "description": "更换四条225/60R16轮胎",
            "cost": 2920.0,
            "repairManId": 1,
            "workHours": 4,
            "startTime": "2024-06-01T16:30:00"
        }
    ]
}
```

### 7. 获取所有工时费发放记录

**接口地址**: `GET /api/admin/wages`

**功能**: 获取系统中所有维修人员的工时费发放记录

**响应示例**:
```json
{
    "success": true,
    "message": "获取工时费发放记录列表成功",
    "count": 5,
    "data": [
        {
            "id": 1,
            "repairman": {
                "repairmanId": 1,
                "username": "repairman01",
                "password": "8CszGHrrogaOk/7vtsOFy7nqfcSzsGOJVbCzRYyIe5I=",
                "name": "张师傅",
                "phone": "13800138001",
                "email": "zhang@motify.com",
                "gender": "男",
                "type": "MECHANIC",
                "hourlyRate": 80.0
            },
            "repairmanId": 1,
            "year": 2024,
            "month": 5,
            "totalWorkHours": 0.03333333333333333,
            "totalIncome": 2.6666666666666665,
            "settlementDate": "2025-06-08T12:08:36.218609",
            "repairmanName": "张师傅",
            "repairmanType": "MECHANIC",
            "hourlyRate": 80.0
        },
        {
            "id": 2,
            "repairman": {
                "repairmanId": 2,
                "username": "repairman02",
                "password": "8CszGHrrogaOk/7vtsOFy7nqfcSzsGOJVbCzRYyIe5I=",
                "name": "李师傅",
                "phone": "13800138002",
                "email": "li@motify.com",
                "gender": "男",
                "type": "ELECTRICIAN",
                "hourlyRate": 60.0
            },
            "repairmanId": 2,
            "year": 2024,
            "month": 5,
            "totalWorkHours": 0.03333333333333333,
            "totalIncome": 2.0,
            "settlementDate": "2025-06-08T12:08:36.225619",
            "repairmanName": "李师傅",
            "repairmanType": "ELECTRICIAN",
            "hourlyRate": 60.0
        },
        {
            "id": 3,
            "repairman": {
                "repairmanId": 3,
                "username": "repairman03",
                "password": "8CszGHrrogaOk/7vtsOFy7nqfcSzsGOJVbCzRYyIe5I=",
                "name": "王师傅",
                "phone": "13800138003",
                "email": "wang@motify.com",
                "gender": "男",
                "type": "BODYWORKER",
                "hourlyRate": 45.0
            },
            "repairmanId": 3,
            "year": 2024,
            "month": 5,
            "totalWorkHours": 0.03333333333333333,
            "totalIncome": 1.5,
            "settlementDate": "2025-06-08T12:08:36.22655",
            "repairmanName": "王师傅",
            "repairmanType": "BODYWORKER",
            "hourlyRate": 45.0
        },
        {
            "id": 4,
            "repairman": {
                "repairmanId": 6,
                "username": "repairman06",
                "password": "8CszGHrrogaOk/7vtsOFy7nqfcSzsGOJVbCzRYyIe5I=",
                "name": "陈师傅",
                "phone": "13800138006",
                "email": "chen@motify.com",
                "gender": "男",
                "type": "INSPECTOR",
                "hourlyRate": 50.0
            },
            "repairmanId": 6,
            "year": 2024,
            "month": 5,
            "totalWorkHours": 0.016666666666666666,
            "totalIncome": 0.8333333333333334,
            "settlementDate": "2025-06-08T12:08:36.227334",
            "repairmanName": "陈师傅",
            "repairmanType": "INSPECTOR",
            "hourlyRate": 50.0
        },
        {
            "id": 5,
            "repairman": {
                "repairmanId": 1,
                "username": "repairman01",
                "password": "8CszGHrrogaOk/7vtsOFy7nqfcSzsGOJVbCzRYyIe5I=",
                "name": "张师傅",
                "phone": "13800138001",
                "email": "zhang@motify.com",
                "gender": "男",
                "type": "MECHANIC",
                "hourlyRate": 80.0
            },
            "repairmanId": 1,
            "year": 2024,
            "month": 6,
            "totalWorkHours": 0.11666666666666667,
            "totalIncome": 9.333333333333334,
            "settlementDate": "2025-06-08T12:08:36.241409",
            "repairmanName": "张师傅",
            "repairmanType": "MECHANIC",
            "hourlyRate": 80.0
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
