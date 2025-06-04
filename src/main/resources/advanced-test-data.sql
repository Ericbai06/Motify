-- Motify汽车维修管理系统 - 扩展测试数据（豪华车型和复杂维修场景）

-- 添加更多用户和豪华车型
INSERT INTO users (username, password, name, phone, email, address) VALUES
('vip001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '林总', '13800138013', 'lin.vip@gmail.com', '北京市朝阳区CBD中央商务区'),
('vip002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '张董', '13800138014', 'zhang.vip@gmail.com', '北京市西城区金融街国际大厦'),
('vip003', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '王总', '13800138015', 'wang.vip@gmail.com', '北京市海淀区中关村软件园');

-- 添加豪华车型
INSERT INTO cars (brand, model, license_plate, user_id) VALUES
-- 保时捷
('Porsche', '911 Carrera', '京AA·8888', 13),
('Porsche', 'Macan', '京BB·6666', 14),
-- 玛莎拉蒂
('Maserati', 'Ghibli', '京CC·9999', 15),
-- 更多特斯拉
('Tesla', 'Model S', '京DD·1234', 13),
-- 沃尔沃
('Volvo', 'XC90', '京EE·5678', 14),
-- 捷豹
('Jaguar', 'XF', '京FF·9012', 15);

-- 添加高端维修材料
INSERT INTO materials (name, description, type, stock, price) VALUES
-- 高端机油和滤芯
('顶级全合成机油0W-20', '超高性能全合成机油，适用于豪华车', 'OIL', 50, 680.00),
('原厂机油滤清器', '原厂品质机油滤清器', 'FILTER', 80, 150.00),
('高性能空气滤清器', '高流量空气滤清器', 'FILTER', 60, 180.00),
-- 高端刹车系统
('碳纤维陶瓷刹车片', '超高性能陶瓷刹车片', 'BRAKE', 30, 1200.00),
('打孔通风刹车盘', '高性能打孔刹车盘', 'BRAKE', 25, 1500.00),
('赛车级刹车油DOT5.1', '超高温刹车油', 'BRAKE', 20, 180.00),
-- 高端轮胎
('轮胎245/40R19', '超高性能运动轮胎', 'TIRE', 20, 1500.00),
('轮胎255/35R20', '顶级跑车轮胎', 'TIRE', 15, 2200.00),
-- 高端电气件
('自适应LED大灯', '智能自适应LED大灯系统', 'ELECTRICAL', 8, 5800.00),
('激光大灯', '激光大灯总成', 'ELECTRICAL', 5, 12000.00),
('高级音响系统', 'BOSE音响系统', 'ELECTRICAL', 10, 8500.00),
-- 碳纤维部件
('碳纤维前保险杠', '碳纤维运动前保险杠', 'BODY', 5, 8800.00),
('碳纤维后扰流板', '碳纤维空气动力学尾翼', 'BODY', 8, 6800.00),
('碳纤维后视镜', '碳纤维电动后视镜', 'BODY', 12, 2800.00),
-- 特殊材料
('陶瓷涂层', '纳米陶瓷车身保护涂层', 'OTHER', 15, 3500.00),
('高性能冷却液', '赛车级冷却液', 'OTHER', 30, 280.00),
('合成齿轮油', '高性能合成齿轮油', 'OTHER', 25, 380.00);

-- 添加复杂维修项目
INSERT INTO maintenance_items (name, description, status, progress, result, reminder, score, cost, work_hours, car_id) VALUES
-- 保时捷911维修项目
('发动机大修', '发动机活塞环老化，需要大修', 'IN_PROGRESS', 30, '已拆解发动机，检查缸体磨损情况', '预计需要1周时间完成', null, 25000.00, 40.0, 21),
('刹车系统升级', '升级为碳纤维陶瓷刹车系统', 'COMPLETED', 100, '刹车系统升级完成，制动效果显著提升', '适应期内注意制动力度', 5, 8500.00, 6.0, 21),

-- 玛莎拉蒂维修项目
('变速箱故障', '双离合变速箱换挡异常', 'IN_PROGRESS', 50, '发现离合器片磨损，正在更换', '使用原厂配件，质量有保证', null, 18000.00, 25.0, 23),
('空气悬挂调试', '空气悬挂高度调节异常', 'PENDING', 0, null, null, null, 12000.00, 8.0, 23),

-- 特斯拉Model S维修项目
('电池组检测', '续航里程下降，需要检测电池组', 'COMPLETED', 100, '发现部分电芯老化，已更换电池模组', '新电池模组质保8年', 4, 35000.00, 12.0, 24),
('自动驾驶系统标定', 'Autopilot摄像头需要重新标定', 'COMPLETED', 100, '自动驾驶系统标定完成，功能正常', '避免强光照射传感器', 5, 2800.00, 4.0, 24),

-- 沃尔沃XC90维修项目
('四驱系统维修', '四驱系统故障灯亮', 'IN_PROGRESS', 70, '发现传动轴异响，正在更换', '使用原厂配件', null, 15000.00, 15.0, 25),
('安全系统检测', '城市安全系统需要重新标定', 'PENDING', 0, null, null, null, 3500.00, 3.0, 25),

-- 捷豹XF维修项目
('发动机增压器', '涡轮增压器漏油', 'PENDING', 0, null, null, null, 22000.00, 20.0, 26),
('内饰翻新', '真皮座椅磨损翻新', 'COMPLETED', 100, '座椅翻新完成，恢复原厂品质', '保养期内避免尖锐物品', 4, 8800.00, 8.0, 26);

-- 添加复杂维修记录详情
INSERT INTO maintenance_records (name, description, cost, item_id) VALUES
-- 保时捷发动机大修详情
('发动机拆解', '完全拆解发动机，检查所有部件', 5000.00, 27),
('缸体检测', '缸体磨损检测和镗缸', 8000.00, 27),
('活塞更换', '更换活塞和活塞环', 12000.00, 27),

-- 保时捷刹车升级详情
('前刹车系统', '升级前轮碳纤维陶瓷刹车系统', 4500.00, 28),
('后刹车系统', '升级后轮碳纤维陶瓷刹车系统', 4000.00, 28),

-- 玛莎拉蒂变速箱维修详情
('变速箱拆解', '拆解双离合变速箱', 3000.00, 29),
('离合器更换', '更换双离合器片', 15000.00, 29),

-- 特斯拉电池检测详情
('电池诊断', '全面电池系统诊断', 5000.00, 31),
('电池模组更换', '更换老化电池模组', 30000.00, 31),

-- 特斯拉自动驾驶标定详情
('传感器清洁', '清洁所有自动驾驶传感器', 800.00, 32),
('系统标定', '重新标定自动驾驶系统', 2000.00, 32),

-- 沃尔沃四驱维修详情
('故障诊断', '四驱系统故障诊断', 2000.00, 33),
('传动轴更换', '更换后传动轴', 13000.00, 33),

-- 捷豹内饰翻新详情
('座椅拆卸', '拆卸前后排座椅', 1000.00, 36),
('皮革翻新', '真皮座椅专业翻新', 7800.00, 36);

-- 添加维修人员分配
INSERT INTO item_repairman (item_id, repairman_id) VALUES
-- 保时捷发动机大修 - 张大师+孙师傅(发动机专家)
(27, 1), (27, 7),
-- 保时捷刹车升级 - 赵技师(机修)
(28, 6),
-- 玛莎拉蒂变速箱 - 李师傅(变速箱专家)+张大师
(29, 2), (29, 1),
-- 玛莎拉蒂空气悬挂 - 赵技师(机修)
(30, 6),
-- 特斯拉电池检测 - 王电工(电工)+张大师
(31, 3), (31, 1),
-- 特斯拉自动驾驶标定 - 王电工(电工)
(32, 3),
-- 沃尔沃四驱维修 - 李师傅+赵技师
(33, 2), (33, 6),
-- 沃尔沃安全系统 - 王电工(电工)
(34, 3),
-- 捷豹发动机增压器 - 孙师傅(发动机专家)
(35, 7),
-- 捷豹内饰翻新 - 陈师傅(钣金)+刘师傅(喷漆)
(36, 4), (36, 5);

-- 添加高端材料使用记录
INSERT INTO record_material (record_id, material_id, amount) VALUES
-- 保时捷刹车升级材料
(26, 32, 1), -- 前刹车系统 - 碳纤维陶瓷刹车片
(26, 33, 1), -- 前刹车系统 - 打孔通风刹车盘
(27, 32, 1), -- 后刹车系统 - 碳纤维陶瓷刹车片
(27, 33, 1), -- 后刹车系统 - 打孔通风刹车盘

-- 特斯拉自动驾驶标定材料
(30, 25, 1), -- 传感器清洁 - 清洁剂

-- 捷豹内饰翻新材料
(32, 41, 1); -- 皮革翻新 - 陶瓷涂层（保护涂层）

-- 添加高端维修记录信息
INSERT INTO record_infos (create_time, update_time, complete_time, total_amount, material_cost, labor_cost, item_id) VALUES
-- 保时捷维修记录
('2024-02-01 08:00:00', '2024-02-15 17:00:00', null, 25000.00, 15000.00, 6000.00, 27),
('2024-01-20 09:00:00', '2024-01-22 16:00:00', '2024-01-22 16:00:00', 8500.00, 6000.00, 900.00, 28),

-- 玛莎拉蒂维修记录
('2024-02-10 10:00:00', '2024-02-20 15:00:00', null, 18000.00, 15000.00, 3125.00, 29),
('2024-02-25 11:00:00', '2024-02-25 11:00:00', null, 0.00, 0.00, 0.00, 30),

-- 特斯拉维修记录
('2024-01-25 13:00:00', '2024-01-30 17:00:00', '2024-01-30 17:00:00', 35000.00, 30000.00, 1800.00, 31),
('2024-02-05 14:00:00', '2024-02-06 12:00:00', '2024-02-06 12:00:00', 2800.00, 800.00, 520.00, 32),

-- 沃尔沃维修记录
('2024-02-15 09:00:00', '2024-02-22 16:00:00', null, 15000.00, 13000.00, 1875.00, 33),
('2024-03-01 10:00:00', '2024-03-01 10:00:00', null, 0.00, 0.00, 0.00, 34),

-- 捷豹维修记录
('2024-03-05 11:00:00', '2024-03-05 11:00:00', null, 0.00, 0.00, 0.00, 35),
('2024-02-12 08:00:00', '2024-02-15 17:00:00', '2024-02-15 17:00:00', 8800.00, 3500.00, 1100.00, 36);

-- 高级业务分析查询

-- 查询豪华车品牌维修成本分析
SELECT 
    c.brand,
    COUNT(mi.item_id) as total_services,
    AVG(CASE WHEN ri.total_amount > 0 THEN ri.total_amount ELSE mi.cost END) as avg_service_cost,
    MAX(CASE WHEN ri.total_amount > 0 THEN ri.total_amount ELSE mi.cost END) as max_service_cost,
    SUM(CASE WHEN ri.total_amount > 0 THEN ri.total_amount ELSE mi.cost END) as total_revenue,
    CASE 
        WHEN c.brand IN ('Porsche', 'Maserati', 'Tesla') THEN '豪华品牌'
        WHEN c.brand IN ('Mercedes-Benz', 'BMW', 'Audi', 'Lexus') THEN '高端品牌'
        ELSE '普通品牌'
    END as brand_category
FROM cars c 
LEFT JOIN maintenance_items mi ON c.car_id = mi.car_id
LEFT JOIN record_infos ri ON mi.item_id = ri.item_id
GROUP BY c.brand
ORDER BY avg_service_cost DESC;

-- 查询复杂维修项目分析（工时超过10小时的项目）
SELECT 
    mi.name,
    mi.description,
    mi.status,
    mi.work_hours,
    c.brand,
    c.model,
    CASE WHEN ri.total_amount > 0 THEN ri.total_amount ELSE mi.cost END as service_cost,
    GROUP_CONCAT(r.name SEPARATOR ', ') as assigned_technicians,
    GROUP_CONCAT(r.specialty SEPARATOR ', ') as specialties
FROM maintenance_items mi
JOIN cars c ON mi.car_id = c.car_id
LEFT JOIN record_infos ri ON mi.item_id = ri.item_id
LEFT JOIN item_repairman ir ON mi.item_id = ir.item_id
LEFT JOIN repairmen r ON ir.repairman_id = r.repairman_id
WHERE mi.work_hours > 10.0
GROUP BY mi.item_id
ORDER BY mi.work_hours DESC;

-- 查询高价值维修项目排行（成本超过5000元）
SELECT 
    mi.name,
    c.brand,
    c.model,
    c.license_plate,
    u.name as customer_name,
    CASE WHEN ri.total_amount > 0 THEN ri.total_amount ELSE mi.cost END as service_cost,
    mi.status,
    ri.material_cost,
    ri.labor_cost,
    mi.score
FROM maintenance_items mi
JOIN cars c ON mi.car_id = c.car_id
JOIN users u ON c.user_id = u.user_id
LEFT JOIN record_infos ri ON mi.item_id = ri.item_id
WHERE (CASE WHEN ri.total_amount > 0 THEN ri.total_amount ELSE mi.cost END) > 5000
ORDER BY service_cost DESC;

-- 查询技师专业技能和高端维修经验
SELECT 
    r.name,
    r.specialty,
    r.type,
    s.hourly_wage,
    COUNT(CASE WHEN mi.cost > 5000 THEN 1 END) as high_value_services,
    COUNT(mi.item_id) as total_services,
    AVG(mi.cost) as avg_service_value,
    SUM(mi.work_hours) as total_work_hours,
    SUM(mi.work_hours * s.hourly_wage) as estimated_earnings
FROM repairmen r
JOIN salaries s ON r.salary_id = s.salary_id
LEFT JOIN item_repairman ir ON r.repairman_id = ir.repairman_id
LEFT JOIN maintenance_items mi ON ir.item_id = mi.item_id
GROUP BY r.repairman_id
ORDER BY high_value_services DESC, avg_service_value DESC;

-- 查询材料成本分析（按材料类型）
SELECT 
    m.type,
    COUNT(DISTINCT m.material_id) as material_varieties,
    AVG(m.price) as avg_material_price,
    SUM(COALESCE(rm.amount, 0)) as total_usage,
    SUM(COALESCE(rm.amount, 0) * m.price) as total_material_cost,
    CASE 
        WHEN m.type IN ('ELECTRICAL', 'BODY') THEN '高价值配件'
        WHEN m.type IN ('OIL', 'FILTER') THEN '消耗品'
        WHEN m.type IN ('BRAKE', 'TIRE') THEN '安全部件'
        ELSE '其他'
    END as category
FROM materials m
LEFT JOIN record_material rm ON m.material_id = rm.material_id
GROUP BY m.type
ORDER BY total_material_cost DESC;
