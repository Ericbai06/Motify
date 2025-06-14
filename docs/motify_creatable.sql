SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 管理员表 (admins) - 存储系统管理员信息
-- ----------------------------
CREATE TABLE `admins`  (
  `admin_id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID，主键，自增',
  `is_active` bit(1) NOT NULL COMMENT '账户是否激活，1=激活，0=禁用',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员邮箱地址，用于登录和通知',
  `last_login_time` datetime(6) NOT NULL COMMENT '最后登录时间，记录管理员活跃度',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员真实姓名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码，已加密存储',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员登录用户名，必须唯一',
  PRIMARY KEY (`admin_id`) USING BTREE COMMENT '主键索引，基于管理员ID',
  UNIQUE INDEX `uk_admin_username`(`username` ASC) USING BTREE COMMENT '唯一索引，确保用户名不重复，支持快速登录查询'
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '管理员表：存储系统管理员的基本信息和登录凭据';

-- ----------------------------
-- 汽车表 (cars) - 存储用户车辆信息
-- ----------------------------
CREATE TABLE `cars`  (
  `car_id` bigint NOT NULL AUTO_INCREMENT COMMENT '汽车ID，主键，自增',
  `brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '汽车品牌，如：丰田、本田、奔驰等',
  `license_plate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '车牌号码，唯一标识，如：京A12345',
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '汽车型号，如：凯美瑞、雅阁、C200等',
  `user_id` bigint NOT NULL COMMENT '车主用户ID，外键关联users表',
  PRIMARY KEY (`car_id`) USING BTREE COMMENT '主键索引，基于汽车ID',
  UNIQUE INDEX `uk_car_license_plate`(`license_plate` ASC) USING BTREE COMMENT '唯一索引，确保车牌号不重复，支持快速车辆查询',
  INDEX `idx_car_user_id`(`user_id` ASC) USING BTREE COMMENT '普通索引，优化根据用户查询车辆的性能',
  CONSTRAINT `fk_car_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保每辆车都有有效的车主'
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '汽车表：存储用户的车辆信息，支持一个用户拥有多辆车';

-- ----------------------------
-- 维修项目-维修人员关联表 (item_repairman) - 多对多关系表，记录维修项目分配给哪些维修人员
-- ----------------------------
CREATE TABLE `item_repairman`  (
  `item_id` bigint NOT NULL COMMENT '维修项目ID，外键关联maintenance_items表',
  `repairman_id` bigint NOT NULL COMMENT '维修人员ID，外键关联repairmen表',
  `is_accepted` tinyint(1) NULL DEFAULT 0 COMMENT '维修人员是否接受此工单，0=未接受，1=已接受',
  INDEX `idx_item_repairman_repairman_id`(`repairman_id` ASC) USING BTREE COMMENT '索引，优化根据维修人员查询分配工单的性能',
  INDEX `idx_item_repairman_item_id`(`item_id` ASC) USING BTREE COMMENT '索引，优化根据维修项目查询分配人员的性能',
  CONSTRAINT `fk_item_repairman_item` FOREIGN KEY (`item_id`) REFERENCES `maintenance_items` (`item_id`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保关联的维修项目存在',
  CONSTRAINT `fk_item_repairman_repairman` FOREIGN KEY (`repairman_id`) REFERENCES `repairmen` (`repairman_id`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保关联的维修人员存在'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '维修项目-维修人员关联表：记录维修工单的人员分配和接受状态';

-- ----------------------------
-- 维修项目表 (maintenance_items) - 存储汽车维修工单的主要信息
-- ---------------------------
CREATE TABLE `maintenance_items`  (
  `item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '维修项目ID，主键，自增',
  `complete_time` datetime(6) NULL DEFAULT NULL COMMENT '维修完成时间，完成后自动设置',
  `cost` double NOT NULL COMMENT '维修总费用，由工时费和材料费汇总计算',
  `create_time` datetime(6) NOT NULL COMMENT '维修工单创建时间',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '维修项目详细描述，用户填写的故障描述',
  `labor_cost` double NULL DEFAULT NULL COMMENT '工时费用，根据维修记录自动计算',
  `material_cost` double NULL DEFAULT NULL COMMENT '材料费用，根据使用材料自动计算',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '维修项目名称，如：更换机油、修理刹车等',
  `progress` int NULL DEFAULT NULL COMMENT '维修进度百分比，0-100',
  `reminder` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '催单信息，用户催促时的备注',
  `result` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修结果描述，完成后填写',
  `score` int NULL DEFAULT NULL COMMENT '用户评分，1-5分',
  `status` enum('ACCEPTED','CANCELLED','COMPLETED','IN_PROGRESS','PENDING') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '维修状态：PENDING=待处理，ACCEPTED=已接收，CANCELLED=已取消，IN_PROGRESS=维修中，COMPLETED=已完成',
  `update_time` datetime(6) NULL DEFAULT NULL COMMENT '最后更新时间',
  `car_id` bigint NOT NULL COMMENT '关联的汽车ID，外键关联cars表',
  PRIMARY KEY (`item_id`) USING BTREE COMMENT '主键索引，基于维修项目ID',
  INDEX `idx_maintenance_item_car_id`(`car_id` ASC) USING BTREE COMMENT '索引，优化根据汽车查询维修记录的性能',
  CONSTRAINT `fk_maintenance_item_car` FOREIGN KEY (`car_id`) REFERENCES `cars` (`car_id`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保关联的汽车存在'
) ENGINE = InnoDB AUTO_INCREMENT = 63 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '维修项目表：存储汽车维修工单的完整生命周期信息';

-- ----------------------------
-- 维修记录表 (maintenance_records) - 记录具体维修人员的工作详情
-- ----------------------------
CREATE TABLE `maintenance_records`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '维修记录ID，主键，自增',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修工作描述，记录具体做了什么',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '维修记录名称，简要描述本次工作',
  `repair_man_id` bigint NOT NULL COMMENT '执行维修的人员ID，外键关联repairmen表',
  `work_hours` bigint NOT NULL COMMENT '工作时长，单位：小时',
  `item_id` bigint NOT NULL COMMENT '所属维修项目ID，外键关联maintenance_items表',
  `start_time` datetime(6) NULL DEFAULT NULL COMMENT '开始维修时间',
  `cost` double NOT NULL COMMENT '本次维修产生的费用',
  PRIMARY KEY (`record_id`) USING BTREE COMMENT '主键索引，基于维修记录ID',
  INDEX `idx_maintenance_record_item_id`(`item_id` ASC) USING BTREE COMMENT '索引，优化根据维修项目查询记录的性能',
  CONSTRAINT `fk_maintenance_record_item` FOREIGN KEY (`item_id`) REFERENCES `maintenance_items` (`item_id`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保关联的维修项目存在'
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '维修记录表：记录每个维修人员在每个工单上的具体工作内容和时长';

-- ----------------------------
-- 材料表 (materials) - 存储维修所需的各种材料和配件信息
-- ----------------------------
CREATE TABLE `materials`  (
  `material_id` bigint NOT NULL AUTO_INCREMENT COMMENT '材料ID，主键，自增',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '材料详细描述，包括规格、型号等信息',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '材料名称，如：机油、刹车片、轮胎等',
  `price` double NOT NULL COMMENT '材料单价，用于计算维修费用',
  `stock` int NULL DEFAULT NULL COMMENT '库存数量，用于库存管理',
  `type` enum('BATTERY','BODY','BRAKE','ELECTRICAL','FILTER','OIL','OTHER','TIRE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '材料类型：BATTERY=电瓶，BODY=车身，BRAKE=刹车，ELECTRICAL=电气，FILTER=滤清器，OIL=油品，OTHER=其他，TIRE=轮胎',
  PRIMARY KEY (`material_id`) USING BTREE COMMENT '主键索引，基于材料ID'
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '材料表：存储维修用材料和配件的基本信息，支持库存管理';

-- ----------------------------
-- 维修记录-材料关联表 (record_material) - 记录每次维修使用了哪些材料及数量
-- ----------------------------
CREATE TABLE `record_material`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键，自增',
  `record_id` bigint NOT NULL COMMENT '维修记录ID，外键关联maintenance_records表',
  `material_id` bigint NOT NULL COMMENT '材料ID，外键关联materials表',
  `amount` int NOT NULL COMMENT '使用数量，用于计算材料费用',
  PRIMARY KEY (`id` DESC) USING BTREE COMMENT '主键索引，基于记录ID，降序排列',
  INDEX `record_id`(`record_id` ASC) USING BTREE COMMENT '索引，优化根据维修记录查询使用材料的性能',
  INDEX `material_id`(`material_id` ASC) USING BTREE COMMENT '索引，优化根据材料查询使用记录的性能',
  CONSTRAINT `record_material_ibfk_1` FOREIGN KEY (`record_id`) REFERENCES `maintenance_records` (`record_id`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保关联的维修记录存在',
  CONSTRAINT `record_material_ibfk_2` FOREIGN KEY (`material_id`) REFERENCES `materials` (`material_id`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保关联的材料存在'
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '维修记录-材料关联表：记录每次维修工作中使用的材料及其数量';

-- ----------------------------
-- 维修人员历史记录表 (repairman_history) - 记录维修人员信息的变更历史
-- ----------------------------
CREATE TABLE `repairman_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '历史记录ID，主键，自增',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员邮箱，变更前的值',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员姓名，变更前的值',
  `operation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作类型，如：CREATE、UPDATE、DELETE',
  `operation_time` datetime(6) NULL DEFAULT NULL COMMENT '操作时间，记录何时进行的变更',
  `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人员，记录是谁进行的变更',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员密码，变更前的值（已加密）',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员电话，变更前的值',
  `repairman_id` bigint NULL DEFAULT NULL COMMENT '维修人员ID，关联原始记录',
  `type` tinyint NULL DEFAULT NULL COMMENT '维修人员类型，0-6对应不同工种类型',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员用户名，变更前的值',
  PRIMARY KEY (`id`) USING BTREE COMMENT '主键索引，基于历史记录ID',
  CONSTRAINT `repairman_history_chk_1` CHECK (`type` between 0 and 6) COMMENT '检查约束，确保类型值在有效范围内'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '维修人员历史记录表：用于审计和追踪维修人员信息的所有变更操作';

-- ----------------------------
-- 维修人员表 (repairmen) - 存储维修人员的基本信息和技能类型
-- ----------------------------
CREATE TABLE `repairmen`  (
  `repairman_id` bigint NOT NULL AUTO_INCREMENT COMMENT '维修人员ID，主键，自增',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员邮箱地址，用于通知和联系',
  `gender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '性别，男/女',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '维修人员真实姓名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码，已加密存储',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `type` enum('APPRENTICE','BODYWORKER','DIAGNOSER','ELECTRICIAN','INSPECTOR','MECHANIC','PAINTER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员类型：APPRENTICE=学徒，BODYWORKER=钣金工，DIAGNOSER=诊断师，ELECTRICIAN=电工，INSPECTOR=检查员，MECHANIC=机械师，PAINTER=喷漆工',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名，必须唯一',
  PRIMARY KEY (`repairman_id`) USING BTREE COMMENT '主键索引，基于维修人员ID',
  UNIQUE INDEX `uk_repairman_username`(`username` ASC) USING BTREE COMMENT '唯一索引，确保维修人员用户名不重复，支持快速登录查询',
  INDEX `idx_repairman_type`(`type` ASC) USING BTREE COMMENT '索引，优化根据维修人员类型查询的性能，便于按技能分配工单',
  CONSTRAINT `fk_repairman_salary_type` FOREIGN KEY (`type`) REFERENCES `salaries` (`type`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保维修人员类型在薪资标准表中存在，维护工资计算的准确性'
) ENGINE = InnoDB AUTO_INCREMENT = 74 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '维修人员表：存储维修人员的基本信息、技能类型和登录凭据';

-- ----------------------------
-- 所需维修人员类型表 (required_repairman_types) - 定义每个维修项目需要哪些类型的维修人员及数量
-- ----------------------------
CREATE TABLE `required_repairman_types`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键，自增',
  `assigned` int NULL DEFAULT NULL COMMENT '已分配的人数，实际分配给该维修项目的特定类型人员数量',
  `required` int NULL DEFAULT NULL COMMENT '需要的人数，该维修项目要求的特定类型人员数量',
  `type` enum('APPRENTICE','BODYWORKER','DIAGNOSER','ELECTRICIAN','INSPECTOR','MECHANIC','PAINTER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '需要的维修人员类型，与repairmen表的type字段对应',
  `item_id` bigint NULL DEFAULT NULL COMMENT '关联的维修项目ID，外键关联maintenance_items表',
  PRIMARY KEY (`id`) USING BTREE COMMENT '主键索引，基于记录ID',
  INDEX `idx_required_type_item_id`(`item_id` ASC) USING BTREE COMMENT '索引，优化根据维修项目查询所需人员类型的性能，支持工单人员配置查询',
  CONSTRAINT `fk_required_type_maintenance_item` FOREIGN KEY (`item_id`) REFERENCES `maintenance_items` (`item_id`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保关联的维修项目存在，维护人员需求配置的完整性'
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '所需维修人员类型表：定义维修项目的人员需求配置，支持按技能类型进行人员分配管理';

-- ----------------------------
-- 薪资标准表 (salaries) - 定义不同类型维修人员的时薪标准
-- ----------------------------
CREATE TABLE `salaries`  (
  `type` enum('APPRENTICE','BODYWORKER','DIAGNOSER','ELECTRICIAN','INSPECTOR','MECHANIC','PAINTER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '维修人员类型，主键，与repairmen表的type字段对应',
  `hourly_rate` float NOT NULL COMMENT '时薪标准，单位：元/小时，用于计算工时费用',
  PRIMARY KEY (`type`) USING BTREE COMMENT '主键索引，基于维修人员类型，确保每种类型只有一个薪资标准'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '薪资标准表：定义不同技能等级维修人员的时薪标准，用于自动计算工时费用';

-- ----------------------------
-- 用户历史记录表 (user_history) - 记录用户信息的变更历史，用于审计
-- ----------------------------
CREATE TABLE `user_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '历史记录ID，主键，自增',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户地址，变更前的值',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户邮箱，变更前的值',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户姓名，变更前的值',
  `operation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作类型，如：CREATE=创建、UPDATE=更新、DELETE=删除',
  `operation_time` datetime(6) NULL DEFAULT NULL COMMENT '操作时间，记录何时进行的变更',
  `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人员，记录是谁进行的变更操作',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户电话，变更前的值',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID，关联原始用户记录',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名，变更前的值',
  PRIMARY KEY (`id`) USING BTREE COMMENT '主键索引，基于历史记录ID'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '用户历史记录表：用于审计和追踪用户信息的所有变更操作，确保数据变更的可追溯性';

-- ----------------------------
-- 用户表 (users) - 存储系统用户（车主）的基本信息
-- ----------------------------
CREATE TABLE `users`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键，自增',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户地址，用于车辆送修时的联系',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户邮箱地址，用于通知和联系',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户真实姓名，车主姓名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码，已加密存储',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户联系电话，紧急联系时使用',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录用户名，必须唯一',
  PRIMARY KEY (`user_id`) USING BTREE COMMENT '主键索引，基于用户ID',
  UNIQUE INDEX `uk_user_username`(`username` ASC) USING BTREE COMMENT '唯一索引，确保用户名不重复，支持快速登录查询'
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '用户表：存储系统用户（车主）的基本信息和登录凭据';

-- ----------------------------
-- 工资结算表 (wages) - 记录维修人员的月度工资结算信息
-- ----------------------------
CREATE TABLE `wages`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工资记录ID，主键，自增',
  `hourly_rate` double NULL DEFAULT NULL COMMENT '时薪标准，记录结算时的时薪，避免后续薪资调整影响历史记录',
  `month` int NOT NULL COMMENT '结算月份，1-12月',
  `repairman_id` bigint NULL DEFAULT NULL COMMENT '维修人员ID，外键关联repairmen表',
  `repairman_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员姓名，冗余存储便于历史查询',
  `repairman_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '维修人员类型，冗余存储便于统计分析',
  `settlement_date` datetime(6) NOT NULL COMMENT '结算日期，记录工资计算和发放的时间',
  `total_income` double NOT NULL COMMENT '总收入，本月应发工资总额',
  `total_work_hours` double NOT NULL COMMENT '总工作时长，本月累计工作小时数',
  `year` int NOT NULL COMMENT '结算年份，与month字段配合确定结算周期',
  PRIMARY KEY (`id`) USING BTREE COMMENT '主键索引，基于工资记录ID',
  INDEX `idx_wage_repairman_id`(`repairman_id` ASC) USING BTREE COMMENT '索引，优化根据维修人员查询工资记录的性能',
  CONSTRAINT `fk_wage_repairman` FOREIGN KEY (`repairman_id`) REFERENCES `repairmen` (`repairman_id`) ON DELETE RESTRICT ON UPDATE RESTRICT COMMENT '外键约束，确保关联的维修人员存在'
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic COMMENT = '工资结算表：记录维修人员的月度工资结算明细，支持工资统计和历史查询';

-- ----------------------------
-- 维修项目表触发器 (maintenance_items) - 自动计算维修费用
-- 功能：当维修状态变更为"已完成"时，自动汇总工时费、材料费并计算总费用
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_maintenanceitem_completed`;
delimiter ;;
CREATE TRIGGER `trg_maintenanceitem_completed` AFTER UPDATE ON `maintenance_items` FOR EACH ROW 
BEGIN
    -- 检查是否从非完成状态变更为完成状态
    IF NEW.status = 'COMPLETED' AND OLD.status <> 'COMPLETED' THEN
        
        -- 1. 汇总工时费用：根据维修记录计算总工时费
        -- 公式：SUM(工作时长 × 维修人员时薪)
        UPDATE maintenance_items mi
        SET mi.labor_cost = (
            SELECT IFNULL(SUM(mr.work_hours * s.hourly_rate), 0)
            FROM maintenance_records mr
            JOIN repairmen r ON mr.repair_man_id = r.repairman_id
            JOIN salaries s ON r.type = s.type
            WHERE mr.item_id = NEW.item_id
        ),
        
        -- 2. 汇总材料费用：根据使用的材料计算总材料费
        -- 公式：SUM(材料单价 × 使用数量)
        mi.material_cost = (
            SELECT IFNULL(SUM(m.price * rm.amount), 0)
            FROM maintenance_records mr
            JOIN record_material rm ON mr.record_id = rm.record_id
            JOIN materials m ON rm.material_id = m.material_id
            WHERE mr.item_id = NEW.item_id
        ),
        
        -- 3. 计算总费用：工时费 + 材料费
        mi.cost = (
            -- 工时费部分
            SELECT IFNULL(SUM(mr.work_hours * s.hourly_rate), 0)
            FROM maintenance_records mr
            JOIN repairmen r ON mr.repair_man_id = r.repairman_id
            JOIN salaries s ON r.type = s.type
            WHERE mr.item_id = NEW.item_id
        ) + (
            -- 材料费部分
            SELECT IFNULL(SUM(m.price * rm.amount), 0)
            FROM maintenance_records mr
            JOIN record_material rm ON mr.record_id = rm.record_id
            JOIN materials m ON rm.material_id = m.material_id
            WHERE mr.item_id = NEW.item_id
        ),
        
        -- 4. 设置完成时间
        mi.complete_time = NOW(6)
        
        WHERE mi.item_id = NEW.item_id;
    END IF;
END
;;
delimiter ;

-- ----------------------------
-- 触发器结构 - required_repairman_types表
-- 功能：自动管理维修工单状态
-- 说明：当维修工种需求表更新后，自动检查是否满足工单开始维修的条件
-- ----------------------------

-- 删除已存在的触发器（如果存在）
DROP TRIGGER IF EXISTS `trg_required_repairman_type_updated`;
delimiter ;;

-- 创建触发器：在required_repairman_types表UPDATE操作后执行
CREATE TRIGGER `motify`.`trg_required_repairman_type_updated` 
AFTER UPDATE ON `required_repairman_types` 
FOR EACH ROW 
BEGIN
    -- 声明局部变量
    DECLARE v_item_id BIGINT;          -- 当前维修工单ID
    DECLARE v_total_types INT;         -- 该工单需要的工种总数
    DECLARE v_fulfilled_types INT;     -- 已满足分配要求的工种数量
    DECLARE v_current_status VARCHAR(50); -- 当前工单状态
    
    -- 获取当前更新行对应的维修工单ID
    SET v_item_id = NEW.item_id;
    
    -- 查询当前工单的状态
    SELECT status INTO v_current_status 
    FROM maintenance_items 
    WHERE item_id = v_item_id;
    
    -- 状态检查：只有当工单状态为PENDING（待处理）或ACCEPTED（已接受）时才进行检查
    -- 避免对已完成、取消或正在进行中的工单进行不必要的状态变更
    IF v_current_status IN ('PENDING', 'ACCEPTED') THEN
        
        -- 计算该工单所有需要的工种数量（required > 0表示确实需要该工种）
        SELECT COUNT(*) INTO v_total_types
        FROM required_repairman_types
        WHERE item_id = v_item_id AND required > 0;
        
        -- 计算已满足分配要求的工种数量
        -- assigned >= required 表示该工种的已分配维修工数量达到或超过需求数量
        SELECT COUNT(*) INTO v_fulfilled_types
        FROM required_repairman_types
        WHERE item_id = v_item_id AND required > 0 AND assigned >= required;
        
        -- 状态转换逻辑：如果所有需要的工种都已分配足够的维修工，则工单可以开始执行
        -- 条件：v_total_types > 0 确保工单确实需要维修工
        --      v_total_types = v_fulfilled_types 确保所有工种需求都已满足
        IF v_total_types > 0 AND v_total_types = v_fulfilled_types THEN
            -- 更新维修工单状态为IN_PROGRESS（进行中）
            UPDATE maintenance_items
            SET status = 'IN_PROGRESS'
            WHERE item_id = v_item_id;
        END IF;
        
    END IF;
    
END
;;

-- 恢复默认的SQL语句分隔符
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
