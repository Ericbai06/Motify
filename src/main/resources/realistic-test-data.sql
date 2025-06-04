-- Motify汽车维修管理系统 - 真实测试数据
-- 清理现有数据（按照外键依赖顺序）
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM record_material;
DELETE FROM item_repairman;
DELETE FROM maintenance_records;
DELETE FROM record_infos;
DELETE FROM maintenance_items;
DELETE FROM materials;
DELETE FROM cars;
DELETE FROM users;
DELETE FROM repairmen;
DELETE FROM salaries;
DELETE FROM admins;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. 插入工资类型数据
INSERT INTO salaries (hourly_rate, type, hourly_wage) VALUES
(150.0, 'SENIOR_MECHANIC', 150.0),
(120.0, 'MECHANIC', 120.0),
(90.0, 'JUNIOR_MECHANIC', 90.0),
(130.0, 'ELECTRICIAN', 130.0),
(110.0, 'BODYWORK_SPECIALIST', 110.0),
(140.0, 'ENGINE_SPECIALIST', 140.0),
(100.0, 'PAINTER', 100.0),
(125.0, 'TRANSMISSION_SPECIALIST', 125.0);

-- 2. 插入管理员数据
INSERT INTO admins (username, password, name, email, last_login_time, is_active) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '系统管理员', 'admin@motify.com', NOW(), true),
('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '业务经理', 'manager@motify.com', NOW(), true),
('supervisor', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '维修主管', 'supervisor@motify.com', NOW(), true);

-- 3. 插入维修人员数据
INSERT INTO repairmen (username, password, name, phone, email, specialty, gender, type, salary_id) VALUES
('zhang_master', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '张大师', '13800138001', 'zhang@motify.com', '发动机维修', '男', 'SENIOR_MECHANIC', 1),
('li_tech', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '李师傅', '13800138002', 'li@motify.com', '变速箱维修', '男', 'TRANSMISSION_SPECIALIST', 8),
('wang_electric', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '王电工', '13800138003', 'wang@motify.com', '电路维修', '男', 'ELECTRICIAN', 4),
('chen_body', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '陈师傅', '13800138004', 'chen@motify.com', '钣金修复', '男', 'BODYWORK_SPECIALIST', 5),
('liu_painter', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '刘师傅', '13800138005', 'liu@motify.com', '车身喷漆', '男', 'PAINTER', 7),
('zhao_mech', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '赵技师', '13800138006', 'zhao@motify.com', '综合维修', '男', 'MECHANIC', 2),
('sun_engine', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '孙师傅', '13800138007', 'sun@motify.com', '发动机专家', '男', 'ENGINE_SPECIALIST', 6),
('wu_junior', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '吴学徒', '13800138008', 'wu@motify.com', '基础维修', '男', 'JUNIOR_MECHANIC', 3);

-- 4. 插入用户数据
INSERT INTO users (username, password, name, phone, email, address) VALUES
('user001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '张先生', '13900139001', 'zhang.user@gmail.com', '北京市朝阳区建国路88号'),
('user002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '王女士', '13900139002', 'wang.user@gmail.com', '北京市海淀区中关村大街1号'),
('user003', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '李先生', '13900139003', 'li.user@gmail.com', '北京市西城区金融街35号'),
('user004', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '陈女士', '13900139004', 'chen.user@gmail.com', '北京市东城区王府井大街138号'),
('user005', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '刘先生', '13900139005', 'liu.user@gmail.com', '北京市丰台区方庄芳星园二区'),
('user006', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '赵女士', '13900139006', 'zhao.user@gmail.com', '北京市石景山区万达广场'),
('user007', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '孙先生', '13900139007', 'sun.user@gmail.com', '北京市通州区万达广场B座'),
('user008', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '周女士', '13900139008', 'zhou.user@gmail.com', '北京市昌平区回龙观龙博苑'),
('user009', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '吴先生', '13900139009', 'wu.user@gmail.com', '北京市大兴区亦庄经济开发区'),
('user010', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '郑女士', '13900139010', 'zheng.user@gmail.com', '北京市房山区良乡大学城'),
('user011', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '马先生', '13900139011', 'ma.user@gmail.com', '北京市门头沟区永定镇'),
('user012', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '何女士', '13900139012', 'he.user@gmail.com', '北京市顺义区后沙峪镇');

-- 5. 插入车辆数据（各种品牌和车型）
INSERT INTO cars (brand, model, license_plate, user_id) VALUES
-- 奔驰车系
('Mercedes-Benz', 'C200L', '京A·88888', 1),
('Mercedes-Benz', 'E300L', '京B·66666', 2),
('Mercedes-Benz', 'S450L', '京C·99999', 3),
-- 宝马车系
('BMW', '320Li', '京D·77777', 4),
('BMW', '525Li', '京E·55555', 5),
('BMW', 'X3', '京F·11111', 6),
-- 奥迪车系
('Audi', 'A4L', '京G·22222', 7),
('Audi', 'Q5L', '京H·33333', 8),
('Audi', 'A6L', '京J·44444', 9),
-- 特斯拉
('Tesla', 'Model 3', '京K·12345', 10),
('Tesla', 'Model Y', '京L·67890', 11),
-- 丰田
('Toyota', 'Camry', '京M·13579', 12),
('Toyota', 'Highlander', '京N·24680', 1),
-- 大众
('Volkswagen', 'Passat', '京P·97531', 2),
('Volkswagen', 'Magotan', '京Q·86420', 3),
-- 本田
('Honda', 'Accord', '京R·14725', 4),
('Honda', 'CR-V', '京S·36985', 5),
-- 日产
('Nissan', 'Teana', '京T·25814', 6),
('Nissan', 'X-Trail', '京U·74129', 7),
-- 雷克萨斯
('Lexus', 'ES300h', '京V·95173', 8);

-- 6. 插入材料数据
INSERT INTO materials (name, description, type, stock, price) VALUES
-- 机油类
('全合成机油5W-30', '顶级全合成机油，适用于高端车型', 'OIL', 200, 380.00),
('半合成机油5W-40', '高性能半合成机油', 'OIL', 150, 220.00),
('矿物机油10W-40', '经济型矿物机油', 'OIL', 100, 120.00),
-- 滤芯类
('机油滤清器', '高品质机油滤清器', 'FILTER', 300, 65.00),
('空气滤清器', '高效空气滤清器', 'FILTER', 250, 85.00),
('空调滤清器', '活性炭空调滤清器', 'FILTER', 200, 95.00),
('汽油滤清器', '燃油系统滤清器', 'FILTER', 180, 120.00),
-- 刹车系统
('前刹车片', '陶瓷前刹车片', 'BRAKE', 100, 280.00),
('后刹车片', '半金属后刹车片', 'BRAKE', 120, 220.00),
('刹车盘', '通风刹车盘', 'BRAKE', 80, 480.00),
('刹车油DOT4', '高性能刹车油', 'BRAKE', 50, 85.00),
-- 轮胎
('轮胎215/60R16', '舒适型轮胎', 'TIRE', 60, 580.00),
('轮胎225/55R17', '运动型轮胎', 'TIRE', 40, 780.00),
('轮胎235/50R18', '高性能轮胎', 'TIRE', 30, 980.00),
-- 电池
('免维护蓄电池12V 60Ah', '长寿命蓄电池', 'BATTERY', 25, 480.00),
('免维护蓄电池12V 80Ah', '大容量蓄电池', 'BATTERY', 20, 680.00),
-- 电气系统
('大灯总成', 'LED大灯总成', 'ELECTRICAL', 15, 1200.00),
('尾灯总成', 'LED尾灯总成', 'ELECTRICAL', 20, 680.00),
('点火线圈', '高性能点火线圈', 'ELECTRICAL', 50, 280.00),
('火花塞', '铱金火花塞', 'ELECTRICAL', 100, 85.00),
-- 车身部件
('前保险杠', '原厂前保险杠', 'BODY', 8, 1580.00),
('后保险杠', '原厂后保险杠', 'BODY', 10, 1380.00),
('车门把手', '电镀车门把手', 'BODY', 30, 180.00),
('后视镜', '电动加热后视镜', 'BODY', 25, 380.00),
-- 其他
('防冻液', '长效防冻液', 'OTHER', 80, 65.00),
('玻璃水', '四季通用玻璃水', 'OTHER', 120, 25.00),
('空调制冷剂R134a', '环保制冷剂', 'OTHER', 40, 120.00),
('发动机清洗剂', '强效发动机清洗剂', 'OTHER', 60, 45.00),
('传动皮带', '原厂传动皮带', 'OTHER', 35, 180.00),
('减震器', '充气减震器', 'OTHER', 20, 680.00),
('密封胶', '车身密封胶', 'OTHER', 45, 35.00);

-- 7. 插入维修项目数据（包含不同状态的项目）
INSERT INTO maintenance_items (name, description, status, progress, result, reminder, score, cost, work_hours, car_id) VALUES
-- 已完成的维修项目
('发动机保养', '全合成机油更换+三滤更换+发动机清洗', 'COMPLETED', 100, '保养完成，发动机运行平稳，动力输出正常', '下次保养里程：1万公里或6个月', 5, 680.00, 2.5, 1),
('刹车系统检修', '更换前后刹车片，补充刹车油，检查刹车盘', 'COMPLETED', 100, '刹车系统检修完成，制动距离正常，无异响', '注意刹车片磨损情况，3万公里后检查', 4, 860.00, 3.0, 2),
('电池更换', '更换免维护蓄电池，检查充电系统', 'COMPLETED', 100, '电池更换完成，启动正常，充电系统工作良好', '新电池质保2年，定期检查', 5, 520.00, 1.0, 3),
('轮胎更换', '更换四条轮胎，动平衡调校', 'COMPLETED', 100, '轮胎更换完成，行驶平稳，无异常磨损', '新轮胎磨合期注意控制车速', 4, 2400.00, 2.0, 4),
('空调系统维修', '更换空调滤芯，清洗蒸发器，补充制冷剂', 'COMPLETED', 100, '空调系统工作正常，制冷效果良好', '建议每年清洗一次空调系统', 5, 380.00, 2.0, 5),
('大灯更换', '更换左前大灯总成，调试灯光角度', 'COMPLETED', 100, '大灯更换完成，照明效果良好', '注意保护灯罩，避免划伤', 4, 1280.00, 1.5, 6),

-- 进行中的维修项目
('发动机故障检修', '发动机异响，需要详细检查活塞连杆', 'IN_PROGRESS', 65, '已拆解发动机，发现活塞环磨损严重', '需要更换活塞环组件，预计明天完成', null, 2800.00, 8.0, 7),
('变速箱保养', '自动变速箱油更换，换挡逻辑重新学习', 'IN_PROGRESS', 80, '变速箱油已更换，正在进行路试', '路试完成后即可交车', null, 580.00, 3.0, 8),
('车身钣金修复', '右侧车门凹陷修复，需要钣金整形', 'IN_PROGRESS', 40, '钣金整形进行中，准备进入喷漆工序', '预计后天完成', null, 1200.00, 6.0, 9),
('电路系统检修', '车辆间歇性熄火，检查点火系统', 'IN_PROGRESS', 75, '发现点火线圈老化，正在更换', '更换完成后需要路试验证', null, 450.00, 3.5, 10),

-- 待处理的维修项目
('常规保养', '5000公里例行保养检查', 'PENDING', 0, null, null, null, 480.00, 2.0, 11),
('左前大灯不亮', '左前大灯突然不亮，需要检查电路', 'PENDING', 0, null, null, null, 650.00, 1.5, 12),
('行驶异响检查', '行驶时底盘有异响，需要全面检查', 'PENDING', 0, null, null, null, 350.00, 3.0, 13),
('油耗异常', '最近油耗突然增加，需要检查发动机状态', 'PENDING', 0, null, null, null, 800.00, 4.5, 14),
('空调不制冷', '空调系统不制冷，需要检查制冷剂', 'PENDING', 0, null, null, null, 420.00, 2.5, 15),
('方向盘抖动', '高速行驶时方向盘抖动', 'PENDING', 0, null, null, null, 380.00, 2.0, 16),

-- 已取消的维修项目
('天窗维修', '客户取消维修，已联系保险公司', 'CANCELLED', 0, null, '客户选择保险理赔', null, 0.00, 0.0, 17),
('音响升级', '客户改变主意，不进行音响升级', 'CANCELLED', 0, null, '客户取消订单', null, 0.00, 0.0, 18);

-- 8. 插入维修记录详情
INSERT INTO maintenance_records (name, description, cost, item_id) VALUES
-- 发动机保养的详细记录
('机油更换', '使用全合成机油5W-30，容量5升', 380.00, 1),
('机滤更换', '更换高品质机油滤清器', 65.00, 1),
('空滤更换', '更换高效空气滤清器', 85.00, 1),
('空调滤芯更换', '更换活性炭空调滤清器', 95.00, 1),
('发动机舱清洗', '使用专业发动机清洗剂', 45.00, 1),

-- 刹车系统检修的详细记录
('前刹车片更换', '更换陶瓷前刹车片', 280.00, 2),
('后刹车片更换', '更换半金属后刹车片', 220.00, 2),
('刹车油更换', '更换DOT4刹车油', 85.00, 2),
('刹车系统检查', '检查刹车盘磨损情况', 150.00, 2),

-- 电池更换的详细记录
('蓄电池更换', '更换12V 60Ah免维护蓄电池', 480.00, 3),

-- 轮胎更换的详细记录
('四轮轮胎更换', '更换四条215/60R16轮胎', 2320.00, 4),
('动平衡校正', '四轮动平衡调校', 80.00, 4),

-- 空调系统维修的详细记录
('空调滤芯更换', '更换活性炭空调滤清器', 95.00, 5),
('制冷剂补充', '补充R134a制冷剂', 120.00, 5),
('蒸发器清洗', '清洗空调蒸发器', 150.00, 5),

-- 大灯更换的详细记录
('左前大灯总成更换', '更换LED大灯总成', 1200.00, 6),
('灯光调试', '调试大灯照射角度', 80.00, 6),

-- 发动机故障检修的详细记录
('发动机拆解检查', '拆解发动机检查活塞连杆', 800.00, 7),
('活塞环更换', '更换活塞环组件', 1500.00, 7),
('发动机组装', '重新组装发动机', 500.00, 7),

-- 变速箱保养的详细记录
('变速箱油更换', '更换自动变速箱油', 380.00, 8),
('变速箱清洗', '清洗变速箱内部', 200.00, 8),

-- 车身钣金修复的详细记录
('钣金整形', '右侧车门凹陷修复', 800.00, 9),
('表面处理', '打磨抛光处理', 200.00, 9),
('底漆喷涂', '喷涂防锈底漆', 200.00, 9),

-- 电路系统检修的详细记录
('点火线圈更换', '更换老化点火线圈', 280.00, 10),
('火花塞更换', '更换铱金火花塞', 170.00, 10);

-- 9. 插入维修项目-维修人员关联数据
INSERT INTO item_repairman (item_id, repairman_id) VALUES
-- 发动机保养 - 张大师(高级技师)
(1, 1),
-- 刹车系统检修 - 赵技师(机修)
(2, 6),
-- 电池更换 - 王电工(电工)
(3, 3),
-- 轮胎更换 - 张大师+吴学徒
(4, 1), (4, 8),
-- 空调系统维修 - 王电工(电工)
(5, 3),
-- 大灯更换 - 王电工(电工)
(6, 3),
-- 发动机故障检修 - 张大师+孙师傅(发动机专家)
(7, 1), (7, 7),
-- 变速箱保养 - 李师傅(变速箱专家)
(8, 2),
-- 车身钣金修复 - 陈师傅(钣金)+刘师傅(喷漆)
(9, 4), (9, 5),
-- 电路系统检修 - 王电工(电工)
(10, 3),
-- 常规保养 - 赵技师(机修)
(11, 6),
-- 左前大灯不亮 - 王电工(电工)
(12, 3),
-- 行驶异响检查 - 张大师(高级技师)
(13, 1),
-- 油耗异常 - 孙师傅(发动机专家)
(14, 7),
-- 空调不制冷 - 王电工(电工)
(15, 3),
-- 方向盘抖动 - 赵技师(机修)
(16, 6);

-- 10. 插入维修记录-材料关联数据
INSERT INTO record_material (record_id, material_id, amount) VALUES
-- 发动机保养相关材料
(1, 1, 1), -- 机油更换 - 全合成机油
(2, 4, 1), -- 机滤更换 - 机油滤清器
(3, 5, 1), -- 空滤更换 - 空气滤清器
(4, 6, 1), -- 空调滤芯更换 - 空调滤清器
(5, 26, 1), -- 发动机舱清洗 - 发动机清洗剂

-- 刹车系统检修相关材料
(6, 8, 1), -- 前刹车片更换 - 前刹车片
(7, 9, 1), -- 后刹车片更换 - 后刹车片
(8, 11, 1), -- 刹车油更换 - 刹车油

-- 电池更换相关材料
(9, 15, 1), -- 蓄电池更换 - 蓄电池

-- 轮胎更换相关材料
(10, 12, 4), -- 四轮轮胎更换 - 轮胎

-- 空调系统维修相关材料
(11, 6, 1), -- 空调滤芯更换 - 空调滤清器
(12, 25, 1), -- 制冷剂补充 - 制冷剂

-- 大灯更换相关材料
(13, 17, 1), -- 左前大灯总成更换 - 大灯总成

-- 发动机故障检修相关材料（预估）
(15, 20, 4), -- 活塞环更换 - 火花塞（作为配件）

-- 变速箱保养相关材料
(16, 2, 1), -- 变速箱油更换 - 半合成机油（代替变速箱油）

-- 电路系统检修相关材料
(18, 19, 1), -- 点火线圈更换 - 点火线圈
(19, 20, 4); -- 火花塞更换 - 火花塞

-- 11. 插入记录信息（费用明细）
INSERT INTO record_infos (create_time, update_time, complete_time, total_amount, material_cost, labor_cost, item_id) VALUES
-- 已完成项目的费用明细
('2024-01-15 09:00:00', '2024-01-15 11:30:00', '2024-01-15 11:30:00', 680.00, 305.00, 375.00, 1),
('2024-01-20 10:00:00', '2024-01-20 13:00:00', '2024-01-20 13:00:00', 860.00, 500.00, 360.00, 2),
('2024-01-25 14:00:00', '2024-01-25 15:00:00', '2024-01-25 15:00:00', 520.00, 480.00, 120.00, 3),
('2024-02-01 08:30:00', '2024-02-01 10:30:00', '2024-02-01 10:30:00', 2400.00, 2320.00, 390.00, 4),
('2024-02-05 13:00:00', '2024-02-05 15:00:00', '2024-02-05 15:00:00', 380.00, 120.00, 260.00, 5),
('2024-02-10 16:00:00', '2024-02-10 17:30:00', '2024-02-10 17:30:00', 1280.00, 1200.00, 195.00, 6),

-- 进行中项目的费用明细（部分完成）
('2024-02-15 09:00:00', '2024-02-18 16:00:00', null, 2800.00, 1800.00, 1120.00, 7),
('2024-02-20 10:00:00', '2024-02-21 14:00:00', null, 580.00, 380.00, 375.00, 8),
('2024-02-22 08:00:00', '2024-02-23 17:00:00', null, 1200.00, 0.00, 660.00, 9),
('2024-02-25 14:00:00', '2024-02-26 16:30:00', null, 450.00, 280.00, 455.00, 10),

-- 待处理项目的初始记录
('2024-02-28 09:00:00', '2024-02-28 09:00:00', null, 0.00, 0.00, 0.00, 11),
('2024-03-01 10:00:00', '2024-03-01 10:00:00', null, 0.00, 0.00, 0.00, 12),
('2024-03-02 11:00:00', '2024-03-02 11:00:00', null, 0.00, 0.00, 0.00, 13),
('2024-03-03 14:00:00', '2024-03-03 14:00:00', null, 0.00, 0.00, 0.00, 14),
('2024-03-04 15:00:00', '2024-03-04 15:00:00', null, 0.00, 0.00, 0.00, 15),
('2024-03-05 16:00:00', '2024-03-05 16:00:00', null, 0.00, 0.00, 0.00, 16);

-- 12. 业务统计查询示例

-- 查询各品牌车辆的维修次数和平均费用
SELECT 
    c.brand,
    COUNT(mi.item_id) as maintenance_count,
    AVG(CASE WHEN ri.total_amount > 0 THEN ri.total_amount ELSE mi.cost END) as avg_cost,
    SUM(CASE WHEN ri.total_amount > 0 THEN ri.total_amount ELSE mi.cost END) as total_revenue
FROM cars c 
LEFT JOIN maintenance_items mi ON c.car_id = mi.car_id
LEFT JOIN record_infos ri ON mi.item_id = ri.item_id
GROUP BY c.brand
ORDER BY maintenance_count DESC;

-- 查询维修人员工作量和收入统计
SELECT 
    r.name,
    r.specialty,
    r.type,
    s.hourly_wage,
    COUNT(mi.item_id) as completed_items,
    SUM(mi.work_hours) as total_work_hours,
    SUM(mi.work_hours * s.hourly_wage) as estimated_income
FROM repairmen r
JOIN salaries s ON r.salary_id = s.salary_id
LEFT JOIN item_repairman ir ON r.repairman_id = ir.repairman_id
LEFT JOIN maintenance_items mi ON ir.item_id = mi.item_id AND mi.status = 'COMPLETED'
GROUP BY r.repairman_id
ORDER BY estimated_income DESC;

-- 查询材料使用情况和库存预警
SELECT 
    m.name,
    m.type,
    m.stock,
    m.price,
    COALESCE(SUM(rm.amount), 0) as used_amount,
    m.stock - COALESCE(SUM(rm.amount), 0) as remaining_stock,
    CASE 
        WHEN m.stock - COALESCE(SUM(rm.amount), 0) < 10 THEN '库存不足'
        WHEN m.stock - COALESCE(SUM(rm.amount), 0) < 30 THEN '库存偏低' 
        ELSE '库存充足'
    END as stock_status
FROM materials m
LEFT JOIN record_material rm ON m.material_id = rm.material_id
GROUP BY m.material_id
ORDER BY remaining_stock ASC;

-- 查询月度营收统计
SELECT 
    DATE_FORMAT(ri.complete_time, '%Y-%m') as month,
    COUNT(DISTINCT mi.item_id) as completed_orders,
    SUM(ri.total_amount) as total_revenue,
    AVG(ri.total_amount) as avg_order_value,
    SUM(ri.material_cost) as material_costs,
    SUM(ri.labor_cost) as labor_costs
FROM record_infos ri
JOIN maintenance_items mi ON ri.item_id = mi.item_id
WHERE ri.complete_time IS NOT NULL
GROUP BY DATE_FORMAT(ri.complete_time, '%Y-%m')
ORDER BY month DESC;

-- 查询客户满意度统计
SELECT 
    CASE 
        WHEN mi.score = 5 THEN '非常满意'
        WHEN mi.score = 4 THEN '满意'
        WHEN mi.score = 3 THEN '一般'
        WHEN mi.score = 2 THEN '不满意'
        WHEN mi.score = 1 THEN '非常不满意'
        ELSE '未评分'
    END as satisfaction_level,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM maintenance_items WHERE status = 'COMPLETED'), 2) as percentage
FROM maintenance_items mi
WHERE mi.status = 'COMPLETED'
GROUP BY mi.score
ORDER BY mi.score DESC;
