# 管理员查询API文档

## 概述

管理员查询功能允许系统管理员查询所有用户、维修人员、车辆和维修工单的信息，以及进行高级数据统计分析，用于系统监控和数据分析。

## API接口

### 注册登录接口

#### 1. 管理员注册

**接口地址**: `POST /api/admin/register`

**请求类型**: `application/json`

**请求参数**:
```json
{
    "username": "string",     // 用户名，必填，3-20个字符，唯一
    "password": "string",     // 密码，必填，8位以上
    "name": "string",         // 姓名，必填，不超过50个字符
    "email": "string"         // 邮箱，必填，有效邮箱格式
}
```

**响应示例**:
- **成功响应** (200):
```json
{
    "data": {
        "adminId": 16,
        "username": "鹤望兰号",
        "password": null,
        "name": "02",
        "email": "234567@test.com",
        "lastLoginTime": "2025-06-13T19:53:53.5970353",
        "active": true
    },
    "message": "管理员注册成功",
    "success": true
}
```

- **失败响应** (400):
```json
{
    "success": false,
    "message": "用户名已存在"
}
```

**可能的错误信息**:
- `用户名不能为空`
- `密码不能为空`
- `姓名不能为空`
- `邮箱不能为空`
- `用户名已存在`

#### 2. 管理员登录

**接口地址**: `POST /api/admin/login`

**请求类型**: `application/json`

**请求参数**:
```json
{
    "username": "string",     // 用户名，必填
    "password": "string"      // 密码，必填
}
```

**响应示例**:
- **成功响应** (200):
```json
{
    "data": {
        "adminId": 16,
        "username": "鹤望兰号",
        "password": null,
        "name": "02",
        "email": "234567@test.com",
        "lastLoginTime": "2025-06-13T19:54:58.193671",
        "active": true
    },
    "message": "登录成功",
    "success": true
}
```

- **失败响应** (400):
```json
{
    "success": false,
    "message": "用户名或密码错误"
}
```

**可能的错误信息**:
- `用户名不能为空`
- `密码不能为空`
- `用户名或密码错误`
- `账户已被禁用`

**注意事项**:
1. 所有响应中的密码字段均为 `null`，不返回实际密码以保护安全
2. 登录成功后会自动更新管理员的最后登录时间
3. 只有激活状态的管理员账户才能登录
4. 密码在数据库中以加密形式存储

### 基本查询接口

#### 1. 获取管理员信息

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

#### 2. 获取所有用户列表

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

#### 3. 获取所有维修人员列表

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

#### 4. 获取所有车辆列表

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

#### 5. 获取所有维修工单列表

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

#### 6. 获取所有历史维修记录

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

#### 7. 获取所有工时费发放记录

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

#### 8. 获取所有材料库存信息

**接口地址**: `GET /api/admin/materials`

**功能**: 获取系统中所有材料的库存信息

**响应示例**:
```json
{
    "success": true,
    "message": "获取材料库存列表成功",
    "count": 12,
    "data": [
        {
            "materialId": 1,
            "name": "5W-30全合成机油",
            "description": "高品质全合成机油，适用于大部分汽车",
            "type": "OIL",
            "stock": 50,
            "price": 89.0
        },
        {
            "materialId": 2,
            "name": "机油滤芯",
            "description": "高效过滤机油杂质",
            "type": "FILTER",
            "stock": 30,
            "price": 25.0
        },
        {
            "materialId": 3,
            "name": "空气滤芯",
            "description": "高效空气过滤器",
            "type": "FILTER",
            "stock": 8,
            "price": 45.0
        },
        {
            "materialId": 4,
            "name": "前刹车片",
            "description": "陶瓷刹车片，制动效果好",
            "type": "BRAKE",
            "stock": 15,
            "price": 180.0
        },
        {
            "materialId": 5,
            "name": "米其林轮胎",
            "description": "225/60R16规格轮胎",
            "type": "TIRE",
            "stock": 20,
            "price": 680.0
        }
    ]
}
```

**材料字段说明**:
- `materialId`: 材料唯一标识符
- `name`: 材料名称
- `description`: 材料描述
- `type`: 材料类型（OIL、FILTER、BRAKE、TIRE、BATTERY、ELECTRICAL、BODY、OTHER）
- `stock`: 当前库存数量
- `price`: 材料单价

**材料类型说明**:
- `OIL`: 机油
- `FILTER`: 滤芯
- `BRAKE`: 刹车系统
- `TIRE`: 轮胎
- `BATTERY`: 电池
- `ELECTRICAL`: 电气系统
- `BODY`: 车身部件
- `OTHER`: 其他

#### 9. 根据材料类型获取库存

**接口地址**: `GET /api/admin/materials/by-type?type={type}`

**请求参数**:
- `type` (必填): 材料类型（OIL、FILTER、BRAKE、TIRE、BATTERY、ELECTRICAL、BODY、OTHER）

**功能**: 获取指定类型的所有材料库存信息

**响应示例**:
```json
{
    "success": true,
    "message": "获取指定类型材料库存成功",
    "count": 2,
    "data": [
        {
            "materialId": 2,
            "name": "机油滤芯",
            "description": "高效过滤机油杂质",
            "type": "FILTER",
            "stock": 30,
            "price": 25.0
        },
        {
            "materialId": 3,
            "name": "空气滤芯",
            "description": "高效空气过滤器",
            "type": "FILTER",
            "stock": 8,
            "price": 45.0
        }
    ]
}
```

#### 10. 根据库存范围获取材料

**接口地址**: `GET /api/admin/materials/by-stock-range?minStock={minStock}&maxStock={maxStock}`

**请求参数**:
- `minStock` (必填): 最小库存数量
- `maxStock` (必填): 最大库存数量

**功能**: 获取库存数量在指定范围内的材料

**响应示例**:
```json
{
    "success": true,
    "message": "获取指定库存范围材料成功",
    "count": 2,
    "data": [
        {
            "materialId": 3,
            "name": "空气滤芯",
            "description": "高效空气过滤器",
            "type": "FILTER",
            "stock": 8,
            "price": 45.0
        },
        {
            "materialId": 7,
            "name": "蓄电池",
            "description": "60Ah免维护蓄电池",
            "type": "BATTERY",
            "stock": 5,
            "price": 450.0
        }
    ]
}
```

#### 11. 获取低库存材料

**接口地址**: `GET /api/admin/materials/low-stock`

**功能**: 获取库存数量较低（≤10）的材料，用于库存预警

**响应示例**:
```json
{
    "success": true,
    "message": "获取低库存材料成功",
    "count": 3,
    "data": [
        {
            "materialId": 3,
            "name": "空气滤芯",
            "description": "高效空气过滤器",
            "type": "FILTER",
            "stock": 8,
            "price": 45.0
        },
        {
            "materialId": 7,
            "name": "蓄电池",
            "description": "60Ah免维护蓄电池",
            "type": "BATTERY",
            "stock": 5,
            "price": 450.0
        },
        {
            "materialId": 9,
            "name": "补胎胶",
            "description": "轮胎修补专用胶",
            "type": "TIRE",
            "stock": 3,
            "price": 15.0
        }
    ]
}
```

**库存管理建议**:
- 定期检查低库存材料，及时补充
- 根据使用频率和维修需求调整库存水平
- 对于关键材料建议保持较高库存水平

## 数据统计查询接口

### 1. 统计各车型的维修次数与平均维修费用

**接口地址**: `GET /api/admin/statistics/car-model-repairs`

**功能**: 统计各车型的维修次数、平均维修费用和总费用

**响应示例**:
```json
{
    "message": "获取车型维修统计成功",
    "count": 3,
    "data": [
        {
            "model": "雅阁",
            "repairCount": 2,
            "avgCost": 464.0,
            "brand": "本田",
            "totalCost": 928.0
        },
        {
            "model": "A4L",
            "repairCount": 1,
            "avgCost": 540.0,
            "brand": "奥迪",
            "totalCost": 540.0
        },
        {
            "model": "汉兰达",
            "repairCount": 1,
            "avgCost": 135.0,
            "brand": "丰田",
            "totalCost": 135.0
        }
    ],
    "success": true
}
```

**字段说明**:
- `brand`: 车辆品牌
- `model`: 车辆型号
- `repairCount`: 维修次数
- `avgCost`: 平均维修费用
- `totalCost`: 总维修费用

### 2. 统计特定车型最常出现的故障类型

**接口地址**: `GET /api/admin/statistics/car-model-faults?brand={}&model={}`

**请求参数**:
- `brand` (必填): 车辆品牌
- `model` (必填): 车辆型号

**注意**: 这两个参数都是必填的，必须作为查询参数提供

**功能**: 统计特定车型最常出现的故障类型及其维修成本

请求示例：`get http://localhost:8080/api/admin/statistics/car-model-faults?brand=本田&model=雅阁`
**响应示例**:
```json
{
    "brand": "本田",
    "data": [
        {
            "faultType": "发动机保养",
            "occurrenceCount": 1,
            "avgRepairCost": 628.0
        },
        {
            "faultType": "刹车系统维修",
            "occurrenceCount": 1,
            "avgRepairCost": 300.0
        }
    ],
    "model": "雅阁",
    "count": 2,
    "message": "获取车型故障统计成功",
    "success": true
}
```

**字段说明**:
- `faultType`: 故障类型（维修项目名称）
- `occurrenceCount`: 出现次数
- `avgRepairCost`: 平均维修成本

### 3. 按月份统计维修费用构成

**接口地址**: `GET /api/admin/statistics/monthly-cost-analysis?startDate={}&endDate={}`

**请求参数**:
- `startDate` (必填): 开始日期 (格式: YYYY-MM-DD)
- `endDate` (必填): 结束日期 (格式: YYYY-MM-DD)

**功能**: 按月份统计维修费用构成，基于维修记录表准确计算材料费和工时费

**注意**: 
- 总费用(totalCost)基于实际计算的材料费和工时费之和
- 材料费通过record_material表和materials表计算得出
- 工时费通过维修记录的工作时长和维修人员时薪计算得出
- 不再依赖maintenance_records.cost字段，确保数据准确性

**响应示例**:
```json
{
    "data": [
        {
            "totalRecords": 1,
            "month": 6,
            "year": 2025,
            "totalMaterialCost": 428.0,
            "laborCostPercentage": 31.85,
            "materialCostPercentage": 68.15,
            "totalCost": 628.0,
            "totalLaborCost": 200.0
        },
        {
            "totalRecords": 2,
            "month": 6,
            "year": 2024,
            "totalMaterialCost": 2720.0,
            "laborCostPercentage": 50.96,
            "materialCostPercentage": 49.04,
            "totalCost": 2933.33,
            "totalLaborCost": 213.33
        },
        {
            "totalRecords": 5,
            "month": 5,
            "year": 2024,
            "totalMaterialCost": 1128.0,
            "laborCostPercentage": 45.75,
            "materialCostPercentage": 54.25,
            "totalCost": 1844.67,
            "totalLaborCost": 716.67
        }
    ],
    "count": 3,
    "message": "获取月度费用分析成功",
    "success": true
}
```

**字段说明**:
- `year`: 年份
- `month`: 月份
- `totalRecords`: 总维修记录数
- `totalCost`: 总费用
- `totalMaterialCost`: 总材料费
- `totalLaborCost`: 总工时费
- `materialCostPercentage`: 材料费比例
- `laborCostPercentage`: 工时费比例

### 4. 按季度统计维修费用构成

**接口地址**: `GET /api/admin/statistics/quarterly-cost-analysis?startDate={}&endDate={}`

**请求参数**:
- `startDate` (必填): 开始日期 (格式: YYYY-MM-DD)
- `endDate` (必填): 结束日期 (格式: YYYY-MM-DD)

**功能**: 按季度统计维修费用构成，基于维修记录表准确计算材料费和工时费

**注意**: 与月度分析相同，总费用基于实际计算的材料费和工时费之和

**响应示例**:
```json
{
    "data": [
        {
            "totalRecords": 1,
            "year": 2025,
            "totalMaterialCost": 428.0,
            "laborCostPercentage": 31.85,
            "materialCostPercentage": 68.15,
            "totalCost": 628.0,
            "totalLaborCost": 200.0,
            "quarter": 2
        },
        {
            "totalRecords": 7,
            "year": 2024,
            "totalMaterialCost": 3848.0,
            "laborCostPercentage": 47.24,
            "materialCostPercentage": 52.76,
            "totalCost": 4778.0,
            "totalLaborCost": 930.0,
            "quarter": 2
        }
    ],
    "count": 2,
    "message": "获取季度费用分析成功",
    "success": true
}
```

**字段说明**:
- `year`: 年份
- `quarter`: 季度 (1-4)
- `totalRecords`: 总维修记录数
- 其他字段与月度分析相同

### 5. 筛选负面反馈工单及涉及的员工

**接口地址**: `GET /api/admin/statistics/negative-feedback?maxScore={}`

**请求参数**:
- `maxScore` (可选): 最大评分，默认为2

**功能**: 筛选评分较低的工单及相关维修人员信息

**响应示例**:
```json
{
    "maxScore": 4,
    "success": true,
    "data": [
        {
            "result": "大灯更换完成，照明正常",
            "repairmanId": 6,
            "itemId": 7,
            "score": 4,
            "repairmanName": "陈师傅",
            "itemName": "大灯维修",
            "carBrand": "丰田",
            "licensePlate": "鄂G13691",
            "repairmanType": "INSPECTOR",
            "carModel": "汉兰达"
        },
        {
            "result": "刹车片更换完成，制动性能良好",
            "repairmanId": 3,
            "itemId": 2,
            "score": 4,
            "repairmanName": "王师傅",
            "itemName": "刹车系统维修",
            "carBrand": "本田",
            "licensePlate": "云A12345",
            "repairmanType": "BODYWORKER",
            "carModel": "雅阁"
        }
    ],
    "count": 2,
    "message": "获取负面反馈工单统计成功"
}
```

**字段说明**:
- `itemId`: 工单ID
- `itemName`: 维修项目名称
- `score`: 用户评分
- `result`: 维修结果
- `carBrand`: 车辆品牌
- `carModel`: 车辆型号
- `licensePlate`: 车牌号
- `repairmanId`: 维修人员ID
- `repairmanName`: 维修人员姓名
- `repairmanType`: 维修人员类型

### 6. 统计不同工种任务数量及完成率

**接口地址**: `GET /api/admin/statistics/repairman-type-tasks`

**请求参数**:
- `startDate` (必填): 开始日期 (格式: YYYY-MM-DD)
- `endDate` (必填): 结束日期 (格式: YYYY-MM-DD)

**功能**: 统计不同工种在指定时间段内接受和完成的任务数量及占比

**响应示例**:
```json
{
    "data": [
        {
            "completedPercentage": 25.0,
            "completedTasks": 1,
            "acceptedTasks": 3,
            "acceptedPercentage": 37.5,
            "repairmanType": "MECHANIC"
        },
        {
            "completedPercentage": 25.0,
            "completedTasks": 1,
            "acceptedTasks": 2,
            "acceptedPercentage": 25.0,
            "repairmanType": "BODYWORKER"
        },
        {
            "completedPercentage": 50.0,
            "completedTasks": 2,
            "acceptedTasks": 2,
            "acceptedPercentage": 25.0,
            "repairmanType": "ELECTRICIAN"
        },
        {
            "completedPercentage": 0.0,
            "completedTasks": 0,
            "acceptedTasks": 2,
            "acceptedPercentage": 25.0,
            "repairmanType": "PAINTER"
        },
        {
            "completedPercentage": 0.0,
            "completedTasks": 0,
            "acceptedTasks": 1,
            "acceptedPercentage": 12.5,
            "repairmanType": "APPRENTICE"
        },
        {
            "completedPercentage": 25.0,
            "completedTasks": 1,
            "acceptedTasks": 1,
            "acceptedPercentage": 12.5,
            "repairmanType": "INSPECTOR"
        }
    ],
    "count": 6,
    "message": "获取工种任务统计成功",
    "success": true
}
```

**字段说明**:
- `repairmanType`: 维修人员类型
- `acceptedTasks`: 接受的任务数量
- `completedTasks`: 完成的任务数量
- `acceptedPercentage`: 接受任务占所有接受任务的百分比
- `completedPercentage`: 完成任务占所有完成任务的百分比

### 7. 统计未完成任务概览

**接口地址**: `GET /api/admin/statistics/uncompleted-tasks-overview`

**功能**: 按状态统计未完成任务的数量和平均等待天数

**响应示例**:
```json
{
    "data": [
        {
            "taskCount": 4,
            "avgDaysPending": 99,
            "status": "PENDING"
        },
        {
            "taskCount": 2,
            "avgDaysPending": 377,
            "status": "IN_PROGRESS"
        }
    ],
    "count": 2,
    "message": "获取未完成任务概览成功",
    "success": true
}
```

**字段说明**:
- `status`: 任务状态 (PENDING: 待接受, IN_PROGRESS: 进行中)
- `taskCount`: 任务数量
- `avgDaysPending`: 平均等待天数

### 8. 按工种统计未完成任务

**接口地址**: `GET /api/admin/statistics/uncompleted-tasks-by-type`

**功能**: 按维修人员工种统计未完成任务数量

**响应示例**:
```json
{
    "data": [
        {
            "avgDaysPending": 377,
            "repairmanType": "MECHANIC",
            "uncompletedTasks": 2
        },
        {
            "avgDaysPending": 376,
            "repairmanType": "PAINTER",
            "uncompletedTasks": 2
        },
        {
            "avgDaysPending": 376,
            "repairmanType": "APPRENTICE",
            "uncompletedTasks": 1
        }
    ],
    "count": 3,
    "message": "获取工种未完成任务统计成功",
    "success": true
}
```

### 9. 按维修人员统计未完成任务

**接口地址**: `GET /api/admin/statistics/uncompleted-tasks-by-repairman`

**功能**: 按个人统计维修人员的未完成任务数量

**响应示例**:
```json
{
    "data": [
        {
            "repairmanId": 1,
            "repairmanName": "张师傅",
            "avgDaysPending": 377,
            "repairmanType": "MECHANIC",
            "uncompletedTasks": 2
        },
        {
            "repairmanId": 4,
            "repairmanName": "赵师傅",
            "avgDaysPending": 376,
            "repairmanType": "PAINTER",
            "uncompletedTasks": 2
        },
        {
            "repairmanId": 5,
            "repairmanName": "刘师傅",
            "avgDaysPending": 376,
            "repairmanType": "APPRENTICE",
            "uncompletedTasks": 1
        }
    ],
    "count": 3,
    "message": "获取维修人员未完成任务统计成功",
    "success": true
}
```

### 10. 按车辆统计未完成任务

**接口地址**: `GET /api/admin/statistics/uncompleted-tasks-by-car`

**功能**: 按车辆统计未完成的维修任务数量

**响应示例**:
```json
{
    "data": [
        {
            "licensePlate": "测试车牌",
            "model": "测试型号",
            "avgDaysPending": 7,
            "brand": "测试品牌",
            "carId": 27,
            "uncompletedTasks": 2
        },
        {
            "licensePlate": "粤C13579",
            "model": "帕萨特",
            "avgDaysPending": 377,
            "brand": "大众",
            "carId": 3,
            "uncompletedTasks": 1
        },
        {
            "licensePlate": "浙E97531",
            "model": "3系",
            "avgDaysPending": 375,
            "brand": "宝马",
            "carId": 5,
            "uncompletedTasks": 1
        },
        {
            "licensePlate": "川F86420",
            "model": "C级",
            "avgDaysPending": 376,
            "brand": "奔驰",
            "carId": 6,
            "uncompletedTasks": 1
        },
        {
            "licensePlate": "京A12345",
            "model": "凯美瑞",
            "avgDaysPending": 7,
            "brand": "丰田",
            "carId": 1,
            "uncompletedTasks": 1
        }
    ],
    "count": 5,
    "message": "获取车辆未完成任务统计成功",
    "success": true
}
```

## 统计API使用说明

### 权限要求
所有统计API都需要管理员权限访问。

### 日期格式
所有日期参数使用标准格式: `YYYY-MM-DD`，例如: `2024-01-01`

### 数据来源
- 所有统计基于已完成的维修工单（status = 'COMPLETED'）
- 未完成任务统计基于状态为 'PENDING' 或 'IN_PROGRESS' 的工单
- 负面反馈基于用户评分不为空且评分较低的工单

### 性能考虑
- 统计查询使用原生SQL实现，性能较好
- 建议在数据量大时适当限制查询时间范围
- 复杂统计查询可能需要较长执行时间

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



## 性能优化建议

1. **分页支持**: 对于大量数据，建议添加分页参数
2. **过滤条件**: 可以添加日期范围、状态等过滤条件
3. **字段选择**: 允许客户端指定需要返回的字段
4. **缓存策略**: 对于不频繁变化的数据可以考虑缓存

## 扩展功能

未来可以考虑添加的功能：
1. 数据导出（CSV、Excel格式）
2. 更多维度的统计分析接口
3. 实时数据推送
4. 数据备份和恢复
5. 统计数据缓存机制
6. 自定义统计报表
7. 统计数据可视化图表
