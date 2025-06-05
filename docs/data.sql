-- 清空现有数据（按依赖关系倒序删除）
DELETE FROM record_material;
DELETE FROM item_repairman;
DELETE FROM maintenance_records;
DELETE FROM maintenance_items;
DELETE FROM cars;
DELETE FROM users;
DELETE FROM repairmen;
DELETE FROM salaries;
DELETE FROM materials;
DELETE FROM admins;

-- 1. 填充管理员数据
INSERT INTO admins (username, password, name, email, last_login_time, is_active) VALUES
('admin', 'admin123', '系统管理员', 'admin@motify.com', '2024-06-01 09:00:00', true),
('superadmin', 'super123', '超级管理员', 'super@motify.com', '2024-06-01 08:30:00', true),
('manager', 'manager123', '维修部经理', 'manager@motify.com', '2024-06-02 10:15:00', true);

-- 2. 填充工资类型数据
INSERT INTO salaries (type, hourly_rate) VALUES
('MECHANIC', 80.0),
('ELECTRICIAN', 60.0),
('BODYWORKER', 45.0),
('PAINTER', 100.0),
('APPRENTICE', 35.0),
('INSPECTOR', 50.0),
('DIAGNOSER', 90.0);

-- 3. 填充维修人员数据
-- 确保 repairmen 表自增主键从1开始
ALTER TABLE repairmen AUTO_INCREMENT = 1;

INSERT INTO repairmen (username, password, name, phone, email, gender, type) VALUES
('repairman01', 'kb2iikDsLq3qS57109XC8G1VIRfjzfMrrr4vkv9fFvw=', '张师傅', '13800138001', 'zhang@motify.com', '男', 'MECHANIC'),
('repairman02', 'kb2iikDsLq3qS57109XC8G1VIRfjzfMrrr4vkv9fFvw=', '李师傅', '13800138002', 'li@motify.com', '男', 'ELECTRICIAN'),
('repairman03', 'kb2iikDsLq3qS57109XC8G1VIRfjzfMrrr4vkv9fFvw=', '王师傅', '13800138003', 'wang@motify.com', '男', 'BODYWORKER'),
('repairman04', 'kb2iikDsLq3qS57109XC8G1VIRfjzfMrrr4vkv9fFvw=', '赵师傅', '13800138004', 'zhao@motify.com', '男', 'PAINTER'),
('repairman05', 'kb2iikDsLq3qS57109XC8G1VIRfjzfMrrr4vkv9fFvw=', '刘师傅', '13800138005', 'liu@motify.com', '女', 'APPRENTICE'),
('repairman06', 'kb2iikDsLq3qS57109XC8G1VIRfjzfMrrr4vkv9fFvw=', '陈师傅', '13800138006', 'chen@motify.com', '男', 'INSPECTOR');

-- 重置 materials 表自增
ALTER TABLE materials AUTO_INCREMENT = 1;

-- 4. 填充材料数据
INSERT INTO materials (name, description, type, stock, price) VALUES
('美孚1号机油', '5W-30全合成机油', 'OIL', 50, 298.0),
('嘉实多机油', '0W-20全合成机油', 'OIL', 30, 268.0),
('空气滤芯', '高效空气滤清器', 'FILTER', 100, 45.0),
('机油滤芯', '原厂机油滤清器', 'FILTER', 80, 35.0),
('燃油滤芯', '燃油滤清器', 'FILTER', 60, 68.0),
('前刹车片', '陶瓷刹车片', 'BRAKE', 40, 180.0),
('后刹车片', '半金属刹车片', 'BRAKE', 35, 150.0),
('刹车盘', '通风刹车盘', 'BRAKE', 20, 320.0),
('米其林轮胎', '225/60R16', 'TIRE', 25, 680.0),
('普利司通轮胎', '205/55R16', 'TIRE', 30, 580.0),
('瓦尔塔电池', '12V 60Ah', 'BATTERY', 15, 480.0),
('博世电池', '12V 70Ah', 'BATTERY', 12, 580.0),
('火花塞', '铱金火花塞', 'ELECTRICAL', 100, 45.0),
('点火线圈', '高压点火线圈', 'ELECTRICAL', 50, 180.0),
('前保险杠', '原厂前保险杠', 'BODY', 10, 1200.0),
('后视镜', '电动后视镜', 'BODY', 20, 280.0),
('雨刮片', '无骨雨刮器', 'OTHER', 60, 35.0),
('防冻液', '长效防冻冷却液', 'OTHER', 40, 68.0);

-- 清空并重置自增
DELETE FROM users;
ALTER TABLE users AUTO_INCREMENT = 1;

-- 插入 users（不指定 user_id，自增）
INSERT INTO users (username, password, name, phone, email, address) VALUES
('user001', 'user123', '张三', '13900139001', 'zhangsan@example.com', '北京市朝阳区建国路88号'),
('user002', 'user123', '李四', '13900139002', 'lisi@example.com', '上海市浦东新区陆家嘴环路999号'),
('user003', 'user123', '王五', '13900139003', 'wangwu@example.com', '广州市天河区珠江路123号'),
('user004', 'user123', '赵六', '13900139004', 'zhaoliu@example.com', '深圳市南山区科技园南区'),
('user005', 'user123', '钱七', '13900139005', 'qianqi@example.com', '杭州市西湖区文三路258号'),
('user006', 'user123', '孙八', '13900139006', 'sunba@example.com', '成都市高新区天府大道666号'),
('user007', 'user123', '周九', '13900139007', 'zhoujiu@example.com', '武汉市洪山区光谷大道77号'),
('user008', 'user123', '吴十', '13900139008', 'wushi@example.com', '西安市雁塔区科技路168号');

-- 清空并重置 cars 表自增
ALTER TABLE cars AUTO_INCREMENT = 1;

-- 插入 cars，user_id 用 1~8
INSERT INTO cars (brand, model, license_plate, user_id) VALUES
('丰田', '凯美瑞', '京A12345', 1),
('本田', '雅阁', '沪B67890', 2),
('大众', '帕萨特', '粤C13579', 3),
('奥迪', 'A4L', '深D24680', 4),
('宝马', '3系', '浙E97531', 5),
('奔驰', 'C级', '川F86420', 6),
('丰田', '汉兰达', '鄂G13691', 7),
('本田', 'CR-V', '陕H75319', 8),
('大众', '途观', '京A98765', 1),
('奥迪', 'Q5', '沪B54321', 2);

-- 重置 maintenance_items 表自增
ALTER TABLE maintenance_items AUTO_INCREMENT = 1;

-- 7. 填充维修项目数据
INSERT INTO maintenance_items (name, description, status, progress, result, reminder, score, create_time, update_time, complete_time, material_cost, labor_cost, cost, car_id) VALUES
('发动机保养', '更换机油、机滤、空滤', 'COMPLETED', 100, '保养完成，发动机运转正常', '下次保养时间：2024-12-01', 5, '2024-05-15 09:00:00', '2024-05-15 11:30:00', '2024-05-15 11:30:00', 378.0, 160.0, 538.0, 1),
('刹车系统维修', '更换前刹车片', 'COMPLETED', 100, '刹车片更换完成，制动性能良好', '建议10000公里后检查', 4, '2024-05-20 10:00:00', '2024-05-20 12:00:00', '2024-05-20 12:00:00', 180.0, 120.0, 300.0, 2),
('轮胎更换', '更换四条轮胎', 'IN_PROGRESS', 75, NULL, NULL, NULL, '2024-06-01 08:30:00', '2024-06-01 15:00:00', NULL, 2720.0, 200.0, 2920.0, 3),
('电池更换', '更换汽车电池', 'COMPLETED', 100, '电池更换完成，启动正常', '电池保修2年', 5, '2024-05-25 14:00:00', '2024-05-25 15:00:00', '2024-05-25 15:00:00', 480.0, 60.0, 540.0, 4),
('空调维修', '空调制冷系统检修', 'PENDING', 0, NULL, NULL, NULL, '2024-06-03 16:00:00', NULL, NULL, NULL, NULL, 0.0, 5),
('变速箱保养', '更换变速箱油', 'IN_PROGRESS', 50, NULL, NULL, NULL, '2024-06-02 13:00:00', '2024-06-02 14:30:00', NULL, 280.0, 180.0, 460.0, 6),
('大灯维修', '更换大灯灯泡', 'COMPLETED', 100, '大灯更换完成，照明正常', NULL, 4, '2024-05-28 11:00:00', '2024-05-28 11:45:00', '2024-05-28 11:45:00', 90.0, 45.0, 135.0, 7),
('底盘检查', '全面底盘检查维护', 'CANCELLED', 0, '客户取消维修', NULL, NULL, '2024-05-30 09:00:00', '2024-05-30 09:30:00', NULL, NULL, NULL, 0.0, 8);

-- 8. 填充维修人员-维修项目关联数据
INSERT INTO item_repairman (item_id, repairman_id) VALUES
(1, 1), (1, 2),  -- 发动机保养：张师傅+李师傅
(2, 3),          -- 刹车维修：王师傅
(3, 1), (3, 4),  -- 轮胎更换：张师傅+赵师傅
(4, 2),          -- 电池更换：李师傅
(5, 4),          -- 空调维修：赵师傅
(6, 1), (6, 5),  -- 变速箱保养：张师傅+刘师傅
(7, 6),          -- 大灯维修：陈师傅
(8, 3);          -- 底盘检查：王师傅

-- 重置 maintenance_records 表自增
ALTER TABLE maintenance_records AUTO_INCREMENT = 1;

-- 9. 填充维修记录数据
INSERT INTO maintenance_records (name, description, cost, repair_man_id, work_hours, item_id, start_time) VALUES
('更换机油机滤', '更换5W-30机油和机油滤芯', 378.0, 1, 2, 1, '2024-05-15 09:00:00'),
('更换空气滤芯', '更换高效空气滤清器', 45.0, 2, 1, 1, '2024-05-15 10:00:00'),
('更换前刹车片', '更换陶瓷前刹车片', 300.0, 3, 2, 2, '2024-05-20 10:00:00'),
('更换米其林轮胎', '更换四条225/60R16轮胎', 2920.0, 1, 4, 3, '2024-06-01 08:30:00'),
('更换汽车电池', '更换瓦尔塔12V 60Ah电池', 540.0, 2, 1, 4, '2024-05-25 14:00:00'),
('变速箱油更换', '更换ATF变速箱油', 460.0, 1, 3, 6, '2024-06-02 13:00:00'),
('更换大灯灯泡', '更换LED大灯灯泡', 135.0, 6, 1, 7, '2024-05-28 11:00:00');

-- 10. 填充维修记录-材料关联数据
INSERT INTO record_material (id, record_id, material_id, amount) VALUES
-- 更换机油机滤记录
(1, 1, 1, 1),  -- 美孚1号机油 1桶
(2, 1, 4, 1),  -- 机油滤芯 1个
-- 更换空气滤芯记录
(3, 2, 3, 1),  -- 空气滤芯 1个
-- 更换刹车片记录
(4, 3, 6, 1),  -- 前刹车片 1套
-- 更换轮胎记录
(5, 4, 9, 4),  -- 米其林轮胎 4条
-- 更换电池记录
(6, 5, 11, 1), -- 瓦尔塔电池 1个
-- 更换大灯记录（使用火花塞代替大灯灯泡）
(7, 7, 13, 2); -- 火花塞 2个
