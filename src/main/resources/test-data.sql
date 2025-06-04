-- Motify汽车维修管理系统测试数据
-- 创建日期: 2025年6月4日
-- 说明: 模拟真实的汽车维修业务数据

-- 清空现有数据（按依赖关系逆序删除）
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM record_material;
DELETE FROM item_repairman;
DELETE FROM record_infos;
DELETE FROM maintenance_records;
DELETE FROM maintenance_items;
DELETE FROM cars;
DELETE FROM users;
DELETE FROM repairmen;
DELETE FROM salaries;
DELETE FROM materials;
DELETE FROM admins;
SET FOREIGN_KEY_CHECKS = 1;

-- 重置自增ID
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE cars AUTO_INCREMENT = 1;
ALTER TABLE maintenance_items AUTO_INCREMENT = 1;
ALTER TABLE maintenance_records AUTO_INCREMENT = 1;
ALTER TABLE materials AUTO_INCREMENT = 1;
ALTER TABLE record_infos AUTO_INCREMENT = 1;
ALTER TABLE repairmen AUTO_INCREMENT = 1;
ALTER TABLE salaries AUTO_INCREMENT = 1;
ALTER TABLE admins AUTO_INCREMENT = 1;

-- 1. 插入管理员数据
INSERT INTO admins (username, password, name, email, last_login_time, is_active) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '系统管理员', 'admin@motify.com', NOW(), true),
('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '业务经理', 'manager@motify.com', NOW(), true),
('supervisor', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '维修主管', 'supervisor@motify.com', NOW(), true);

-- 2. 插入工资类型数据
INSERT INTO salaries (hourly_rate, type, hourly_wage) VALUES
(120.0, 'SENIOR_MECHANIC', 120.0),
(100.0, 'MECHANIC', 100.0),
(80.0, 'JUNIOR_MECHANIC', 80.0),
(90.0, 'ELECTRICIAN', 90.0),
(85.0, 'BODYWORK_SPECIALIST', 85.0);

-- 3. 插入维修人员数据
INSERT INTO repairmen (username, password, name, phone, email, specialty, gender, type, salary_id) VALUES
('zhang_master', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '张师傅', '13800138001', 'zhang@motify.com', '发动机维修', '男', 'SENIOR_MECHANIC', 1),
('li_tech', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '李技师', '13800138002', 'li@motify.com', '变速箱维修', '男', 'MECHANIC', 2),
('wang_expert', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '王师傅', '13800138003', 'wang@motify.com', '制动系统', '女', 'MECHANIC', 2),
('liu_electric', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '刘电工', '13800138004', 'liu@motify.com', '电路维修', '男', 'ELECTRICIAN', 4),
('chen_body', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '陈师傅', '13800138005', 'chen@motify.com', '车身钣金', '男', 'BODYWORK_SPECIALIST', 5),
('zhao_junior', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '赵学徒', '13800138006', 'zhao@motify.com', '基础维修', '男', 'JUNIOR_MECHANIC', 3);

-- 4. 插入用户数据
INSERT INTO users (username, password, name, phone, email, address) VALUES
('user001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '张三', '13901234567', 'zhangsan@email.com', '北京市朝阳区建国路1号'),
('user002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '李四', '13901234568', 'lisi@email.com', '上海市浦东新区陆家嘴1号'),
('user003', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '王五', '13901234569', 'wangwu@email.com', '广州市天河区珠江新城1号'),
('user004', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '赵六', '13901234570', 'zhaoliu@email.com', '深圳市南山区科技园1号'),
('user005', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '孙七', '13901234571', 'sunqi@email.com', '成都市高新区天府大道1号'),
('user006', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '周八', '13901234572', 'zhouba@email.com', '杭州市西湖区文三路1号'),
('user007', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '吴九', '13901234573', 'wujiu@email.com', '南京市江宁区胜太东路1号'),
('user008', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '郑十', '13901234574', 'zhengshi@email.com', '武汉市洪山区珞喻路1号');

-- 5. 插入车辆数据
INSERT INTO cars (brand, model, license_plate, user_id) VALUES
-- 张三的车辆
('奔驰', 'C200L', '京A88888', 1),
('宝马', 'X3', '京B99999', 1),
-- 李四的车辆
('奥迪', 'A4L', '沪A12345', 2),
-- 王五的车辆
('丰田', '凯美瑞', '粤A66666', 3),
('本田', '雅阁', '粤B77777', 3),
-- 赵六的车辆
('大众', '帕萨特', '深A33333', 4),
-- 孙七的车辆
('福特', '蒙迪欧', '川A44444', 5),
('现代', '索纳塔', '川B55555', 5),
-- 周八的车辆
('日产', '天籁', '浙A11111', 6),
-- 吴九的车辆
('别克', '君威', '苏A22222', 7),
-- 郑十的车辆
('雪佛兰', '迈锐宝', '鄂A99999', 8);

-- 6. 插入材料数据 (修正版本)
INSERT INTO materials (name, description, type, stock, price) VALUES
-- 滤清器类 - 使用 FILTER
('机油滤清器', '高品质机油滤清器，适用于多种车型', 'FILTER', 150, 45.00),
('空气滤清器', '高效空气过滤器，保护发动机', 'FILTER', 200, 35.00),
('空调滤清器', '活性炭空调滤芯', 'FILTER', 180, 55.00),

-- 发动机电气件 - 使用 ELECTRICAL
('火花塞', '铱金火花塞，点火性能优异', 'ELECTRICAL', 300, 25.00),
('发电机', '120A发电机，输出稳定', 'ELECTRICAL', 25, 800.00),
('起动机', '高扭矩起动机', 'ELECTRICAL', 30, 650.00),
('大灯', 'LED大灯总成', 'ELECTRICAL', 50, 520.00),

-- 油液类 - 使用 OIL
('发动机油', '全合成机油 5W-30', 'OIL', 500, 80.00),
('冷却液', '防冻冷却液，四季通用', 'OIL', 300, 50.00),
('刹车油', 'DOT4刹车油，高性能制动液', 'OIL', 200, 40.00),

-- 制动系统 - 使用 BRAKE
('刹车片', '陶瓷刹车片，制动性能佳', 'BRAKE', 120, 180.00),
('刹车盘', '通风刹车盘，散热性能好', 'BRAKE', 60, 350.00),

-- 电池 - 使用 BATTERY
('电瓶', '12V免维护蓄电池', 'BATTERY', 60, 380.00),

-- 轮胎相关 - 使用 TIRE
('轮胎', '245/45R18高性能轮胎', 'TIRE', 100, 680.00),
('轮毂', '18寸铝合金轮毂', 'TIRE', 40, 1200.00),

-- 车身件 - 使用 BODY
('保险杠', '前保险杠总成', 'BODY', 20, 800.00),
('车门把手', '外门把手', 'BODY', 40, 120.00),
('后视镜', '电动后视镜总成', 'BODY', 30, 350.00),

-- 其他配件 - 使用 OTHER
('正时皮带', '耐磨正时皮带，使用寿命长', 'OTHER', 80, 120.00),
('减震器', '原厂品质减震器', 'OTHER', 40, 450.00),
('弹簧', '螺旋弹簧，承载能力强', 'OTHER', 80, 280.00),
('雨刷器', '无骨雨刷片', 'OTHER', 200, 35.00),
('密封胶', '高温密封胶', 'OTHER', 300, 25.00),
('螺栓套装', '不锈钢螺栓组合', 'OTHER', 500, 15.00);

-- 7. 插入维修项目数据
INSERT INTO maintenance_items (name, description, status, progress, result, reminder, score, cost, work_hours, car_id) VALUES
-- 已完成的维修项目
('发动机大保养', '更换机油、机滤、空滤，检查发动机各项指标', 'COMPLETED', 100, '保养完成，发动机运行正常', '下次保养时间：3个月后', 5, 680.00, 3.5, 1),
('刹车系统检修', '更换前后刹车片，检查刹车油', 'COMPLETED', 100, '制动系统恢复正常', '刹车片预计使用2万公里', 4, 920.00, 4.0, 2),
('轮胎更换', '更换四条轮胎', 'COMPLETED', 100, '轮胎更换完成，四轮定位正常', '轮胎气压定期检查', 5, 2720.00, 2.0, 3),

-- 进行中的维修项目
('变速箱维修', '变速箱异响，需要拆解检查', 'IN_PROGRESS', 60, '已拆解完成，发现离合器片磨损', '等待配件到货', null, 1500.00, 8.0, 4),
('电路故障排查', '车辆无法启动，怀疑电路问题', 'IN_PROGRESS', 30, '初步检查发现起动机故障', '已订购新起动机', null, 650.00, 5.0, 5),
('空调系统维修', '空调不制冷，需要检查制冷系统', 'IN_PROGRESS', 80, '压缩机需要更换', '配件已到货，准备安装', null, 1200.00, 6.0, 6),

-- 待处理的维修项目
('例行保养', '常规保养检查', 'PENDING', 0, null, null, null, 450.00, 2.0, 7),
('大灯不亮', '左前大灯不亮，需要检查', 'PENDING', 0, null, null, null, 520.00, 1.5, 8),
('异响检查', '行驶时有异响，需要全面检查', 'PENDING', 0, null, null, null, 300.00, 3.0, 9),
('油耗异常', '油耗突然增加，需要检查发动机', 'PENDING', 0, null, null, null, 800.00, 4.5, 10),

-- 取消的维修项目
('座椅翻新', '真皮座椅翻新', 'CANCELLED', 0, '客户取消服务', null, null, 0.00, 0.0, 11);

-- 8. 插入维修记录详情
INSERT INTO record_infos (create_time, update_time, complete_time, total_amount, material_cost, labor_cost, item_id) VALUES
-- 已完成项目的记录
('2024-05-15 09:00:00', '2024-05-15 12:30:00', '2024-05-15 12:30:00', 680.00, 280.00, 400.00, 1),
('2024-05-18 14:00:00', '2024-05-18 18:00:00', '2024-05-18 18:00:00', 920.00, 520.00, 400.00, 2),
('2024-05-20 10:00:00', '2024-05-20 12:00:00', '2024-05-20 12:00:00', 2720.00, 2720.00, 240.00, 3),

-- 进行中项目的记录
('2024-05-25 08:00:00', '2024-06-03 17:00:00', null, 1500.00, 800.00, 700.00, 4),
('2024-06-01 09:30:00', '2024-06-03 16:00:00', null, 650.00, 650.00, 250.00, 5),
('2024-06-02 13:00:00', '2024-06-04 11:00:00', null, 1200.00, 1000.00, 540.00, 6),

-- 待处理项目的记录
('2024-06-04 08:00:00', '2024-06-04 08:00:00', null, 450.00, 280.00, 160.00, 7),
('2024-06-04 09:00:00', '2024-06-04 09:00:00', null, 520.00, 520.00, 120.00, 8),
('2024-06-04 10:00:00', '2024-06-04 10:00:00', null, 300.00, 100.00, 240.00, 9),
('2024-06-04 11:00:00', '2024-06-04 11:00:00', null, 800.00, 200.00, 360.00, 10);

-- 9. 插入维修记录数据
INSERT INTO maintenance_records (name, description, cost, item_id) VALUES
-- 发动机大保养的详细记录
('机油更换', '更换全合成机油5L', 400.00, 1),
('滤清器更换', '更换机油滤清器和空气滤清器', 80.00, 1),
('系统检查', '检查发动机各项参数', 200.00, 1),

-- 刹车系统检修的详细记录
('前刹车片更换', '更换前轮刹车片', 360.00, 2),
('后刹车片更换', '更换后轮刹车片', 360.00, 2),
('刹车油更换', '更换DOT4刹车油', 80.00, 2),
('制动系统检查', '检查制动系统各部件', 120.00, 2),

-- 轮胎更换的详细记录
('四轮轮胎更换', '更换四条高性能轮胎', 2720.00, 3),

-- 变速箱维修的详细记录
('变速箱拆解', '拆解变速箱进行检查', 800.00, 4),
('离合器检查', '检查离合器片磨损情况', 400.00, 4),
('配件更换', '更换磨损的离合器片', 300.00, 4),

-- 电路故障排查的详细记录
('电路检测', '全车电路系统检测', 150.00, 5),
('起动机更换', '更换故障起动机', 650.00, 5),

-- 空调系统维修的详细记录
('空调系统检测', '检测空调制冷系统', 200.00, 6),
('压缩机更换', '更换空调压缩机', 1000.00, 6);

-- 10. 插入维修人员分配关系
INSERT INTO item_repairman (item_id, repairman_id) VALUES
-- 发动机大保养 - 张师傅
(1, 1),
-- 刹车系统检修 - 王师傅
(2, 3),
-- 轮胎更换 - 赵学徒
(3, 6),
-- 变速箱维修 - 李技师
(4, 2),
-- 电路故障排查 - 刘电工
(5, 4),
-- 空调系统维修 - 张师傅 + 刘电工
(6, 1),
(6, 4),
-- 例行保养 - 赵学徒
(7, 6),
-- 大灯不亮 - 刘电工
(8, 4),
-- 异响检查 - 张师傅
(9, 1),
-- 油耗异常 - 李技师
(10, 2);

-- 11. 插入材料使用记录 (record_material表)
INSERT INTO record_material (record_id, material_id, amount) VALUES
-- 机油更换使用的材料
(1, 5, 5),  -- 发动机油 5L
-- 滤清器更换使用的材料
(2, 1, 1),  -- 机油滤清器 1个
(2, 2, 1),  -- 空气滤清器 1个
-- 前刹车片更换使用的材料
(4, 7, 1),  -- 刹车片 1套
-- 后刹车片更换使用的材料
(5, 7, 1),  -- 刹车片 1套
-- 刹车油更换使用的材料
(6, 9, 2),  -- 刹车油 2瓶
-- 四轮轮胎更换使用的材料
(8, 15, 4), -- 轮胎 4条
-- 起动机更换使用的材料
(10, 14, 1), -- 起动机 1个
-- 空调系统维修使用的材料
(12, 19, 2); -- 密封胶 2支

-- 查询统计信息
SELECT '=== 数据插入完成统计 ===' as message;

SELECT 
    '管理员' as type, 
    COUNT(*) as count 
FROM admins
UNION ALL
SELECT 
    '维修人员' as type, 
    COUNT(*) as count 
FROM repairmen
UNION ALL
SELECT 
    '用户' as type, 
    COUNT(*) as count 
FROM users
UNION ALL
SELECT 
    '车辆' as type, 
    COUNT(*) as count 
FROM cars
UNION ALL
SELECT 
    '维修项目' as type, 
    COUNT(*) as count 
FROM maintenance_items
UNION ALL
SELECT 
    '材料' as type, 
    COUNT(*) as count 
FROM materials
UNION ALL
SELECT 
    '维修记录' as type, 
    COUNT(*) as count 
FROM maintenance_records;

-- 显示各状态的维修项目统计
SELECT 
    status as '维修状态',
    COUNT(*) as '项目数量',
    ROUND(AVG(cost), 2) as '平均费用',
    ROUND(SUM(cost), 2) as '总费用'
FROM maintenance_items 
GROUP BY status;
