# Motify 系统实体关系文档

## 1. 核心实体关系

### 1.1 用户(User)相关
- User (用户)
  - 主键: userId (Long)
  - 基本属性: username, password, phone
  - 关系:
    - 一对多: 拥有多个车辆(Car)
    - 一对多: 拥有多个维修记录(MaintenanceRecord)

### 1.2 管理员(Admin)相关
- Admin (管理员)
  - 主键: adminId (Long)
  - 基本属性: username, password, name, phone
  - 关系:
    - 无直接关联关系

### 1.3 维修人员(Repairman)相关
- Repairman (维修人员)
  - 主键: repairmanId (Long)
  - 基本属性: username, password, name, phone, specialty, type
  - 关系:
    - 多对多: 参与多个维修记录(MaintenanceRecord)
    - 一对一: 拥有一个薪资信息(Salary)

### 1.4 车辆(Car)相关
- Car (车辆)
  - 主键: carId (Long)
  - 基本属性: brand, model, licensePlate
  - 关系:
    - 多对一: 属于一个用户(User)
    - 一对多: 拥有多个维修记录(MaintenanceRecord)

### 1.5 维修记录(MaintenanceRecord)相关
- MaintenanceRecord (维修记录)
  - 主键: recordId (Long)
  - 基本属性: description, status, progress, result, reminder, score
  - 关系:
    - 多对一: 属于一辆车(Car)
    - 多对多: 关联多个维修人员(Repairman)
    - 一对多: 包含多个维修项目(MaintenanceItem)
    - 一对一: 拥有一个记录信息(RecordInfo)
    - 一对多: 使用多个材料(Material)

### 1.6 维修项目(MaintenanceItem)相关
- MaintenanceItem (维修项目)
  - 主键: itemId (Long)
  - 基本属性: name, description, cost
  - 关系:
    - 多对一: 属于一个维修记录(MaintenanceRecord)
    - 多对多: 使用多个材料(Material)

### 1.7 材料(Material)相关
- Material (材料)
  - 主键: materialId (Long)
  - 基本属性: name, description, type, stockQuantity, minimumStock, quantity
  - 关系:
    - 多对多: 被多个维修项目使用(MaintenanceItem)
    - 一对一: 拥有一个价格信息(MaterialPrice)
    - 多对一: 属于一个维修记录(MaintenanceRecord)

### 1.8 材料类型(MaterialType)相关
- MaterialType (材料类型)
  - 枚举类型
  - 值: SPARE_PART, CONSUMABLE, TOOL, OTHER

### 1.9 记录信息(RecordInfo)相关
- RecordInfo (记录信息)
  - 主键: infoId (Long)
  - 基本属性: createTime, updateTime, totalAmount
  - 关系:
    - 一对一: 属于一个维修记录(MaintenanceRecord)

### 1.10 薪资(Salary)相关
- Salary (薪资)
  - 主键: salaryId (Long)
  - 基本属性: hourlyWage, monthlyWage
  - 关系:
    - 一对一: 属于一个维修人员(Repairman)

### 1.11 材料价格(MaterialPrice)相关
- MaterialPrice (材料价格)
  - 主键: priceId (Long)
  - 基本属性: unitPrice, updateTime
  - 关系:
    - 一对一: 属于一个材料(Material)

## 2. 数据库表关系

### 2.1 主要表
1. users
   - user_id (PK)
   - username
   - password
   - phone

2. admins
   - admin_id (PK)
   - username
   - password
   - name
   - phone

3. repairmen
   - repairman_id (PK)
   - username
   - password
   - name
   - phone
   - specialty
   - type

4. cars
   - car_id (PK)
   - user_id (FK)
   - brand
   - model
   - license_plate

5. maintenance_records
   - record_id (PK)
   - car_id (FK)
   - description
   - status
   - progress
   - result
   - reminder
   - score

6. maintenance_items
   - item_id (PK)
   - record_id (FK)
   - name
   - description
   - cost

7. materials
   - material_id (PK)
   - name
   - description
   - type
   - stock_quantity
   - minimum_stock
   - quantity
   - maintenance_record_id (FK)
   - material_price_id (FK)

8. record_infos
   - info_id (PK)
   - record_id (FK)
   - create_time
   - update_time
   - total_amount

9. salaries
   - salary_id (PK)
   - repairman_id (FK)
   - hourly_wage
   - monthly_wage

10. material_prices
    - price_id (PK)
    - material_id (FK)
    - unit_price
    - update_time

### 2.2 关联表
1. record_repairman
   - record_id (FK)
   - repairman_id (FK)

2. maintenance_item_material
   - item_id (FK)
   - material_id (FK)

## 3. 业务关系说明

1. 用户管理
   - 用户可以注册、登录、修改个人信息
   - 用户可以查看自己的车辆和维修记录
   - 用户可以提交维修请求

2. 维修人员管理
   - 维修人员可以注册、登录、修改个人信息
   - 维修人员可以查看和更新维修记录
   - 维修人员可以记录维修进度和结果

3. 管理员功能
   - 管理用户和维修人员
   - 查看所有维修记录
   - 统计系统数据
   - 管理材料和价格

4. 维修流程
   - 用户提交维修请求
   - 系统分配维修人员
   - 维修人员更新维修进度
   - 记录维修项目和材料使用
   - 计算维修费用
   - 完成维修记录

## 4. 多工种维修人员自动分配系统

### 4.1 工种需求(RequiredRepairmanType)相关
- RequiredRepairmanType (工种需求)
  - 主键: id (Long)
  - 基本属性: required, assigned, type
  - 关系:
    - 多对一: 属于一个维修工单(MaintenanceItem)

### 4.2 多工种分配关系
- MaintenanceItem与Repairman
  - 多对多关系通过item_repairman表实现
  - 每个工单可以同时分配给多个不同工种的维修人员
  - 维修人员可以接受或拒绝分配的工单
  - 记录工单需要的各种工种及数量，以及已分配的数量

### 4.3 数据库表扩展
1. required_repairman_types
   - id (PK)
   - item_id (FK)
   - type (ENUM)
   - required (INT)
   - assigned (INT)

2. item_repairman (扩展字段)
   - item_id (FK)
   - repairman_id (FK)
   - is_accepted (BOOLEAN) - 新增字段，表示维修人员是否接受工单

### 4.4 业务关系说明
1. 工单分配流程
   - 系统根据工单需要的工种类型和数量自动分配合适的维修人员
   - 分配优先级基于维修人员当前工作量，工作量较少的优先分配
   - 维修人员可以接受或拒绝分配的工单
   - 当维修人员拒绝工单时，系统自动寻找下一个合适的维修人员重新分配

2. 工种类型管理
   - 每个维修人员有一个特定工种类型(RepairmanType)
   - 工单可以同时需要多种不同工种的维修人员
   - 系统确保分配足够数量的每种类型维修人员 