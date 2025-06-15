-- ========================================
-- 重命名索引脚本 - 将Hibernate自动生成的索引名改为有意义的名称
-- ========================================

-- 1. 管理员表 (admins) - 重命名用户名唯一索引
ALTER TABLE `admins` 
RENAME INDEX `UKmi8vkhus4xbdbqcac2jm4spvd` TO `uk_admin_username`;

-- 2. 汽车表 (cars) - 重命名车牌号唯一索引和用户外键索引
ALTER TABLE `cars` 
RENAME INDEX `UKdbc9idlyetvssufb2vxicvb87` TO `uk_car_license_plate`;

ALTER TABLE `cars` 
RENAME INDEX `FKqw4c8e6nqrvy3ti1xj8w8wyc9` TO `idx_car_user_id`;

-- 3. 维修项目-维修人员关联表 (item_repairman) - 重命名外键索引
ALTER TABLE `item_repairman` 
RENAME INDEX `FKl3mjeo5dyopnpqnmp05uqkift` TO `idx_item_repairman_repairman_id`;

ALTER TABLE `item_repairman` 
RENAME INDEX `FK19r2r1n11oy0var7hk0jmkbwr` TO `idx_item_repairman_item_id`;

-- 4. 维修项目表 (maintenance_items) - 重命名汽车外键索引
ALTER TABLE `maintenance_items` 
RENAME INDEX `FK4ahy8jqhv98sn0off3lm5jevf` TO `idx_maintenance_item_car_id`;

-- 5. 维修记录表 (maintenance_records) - 重命名维修项目外键索引
ALTER TABLE `maintenance_records` 
RENAME INDEX `FKsq6ax65x22cfi6xb4iq2ynjv5` TO `idx_maintenance_record_item_id`;

-- 6. 维修人员表 (repairmen) - 重命名用户名唯一索引和类型外键索引
ALTER TABLE `repairmen` 
RENAME INDEX `UKtq4j89imqjwse41h4p86scorc` TO `uk_repairman_username`;

ALTER TABLE `repairmen` 
RENAME INDEX `FKo0p1w76q7b5qwnqaf9qfptlea` TO `idx_repairman_type`;

-- 7. 所需维修人员类型表 (required_repairman_types) - 重命名维修项目外键索引
ALTER TABLE `required_repairman_types` 
RENAME INDEX `FK4xmkvjv7amo0n7b9xemh51op0` TO `idx_required_type_item_id`;

-- 8. 用户表 (users) - 重命名用户名唯一索引
ALTER TABLE `users` 
RENAME INDEX `UKr43af9ap4edm43mmtq01oddj6` TO `uk_user_username`;

-- 9. 工资表 (wages) - 重命名维修人员外键索引
ALTER TABLE `wages` 
RENAME INDEX `FK2e0cow4qra7bap3ir892f3r2i` TO `idx_wage_repairman_id`;

-- ========================================
-- 索引重命名完成
-- 新的索引命名规范说明：
-- - uk_表名_字段名：唯一索引 (Unique Key)
-- - idx_表名_字段名：普通索引 (Index)
-- - 外键索引：idx_当前表名_外键字段名
-- ========================================

-- 验证索引重命名结果
SHOW INDEX FROM admins;
SHOW INDEX FROM cars;
SHOW INDEX FROM item_repairman;
SHOW INDEX FROM maintenance_items;
SHOW INDEX FROM maintenance_records;
SHOW INDEX FROM repairmen;
SHOW INDEX FROM required_repairman_types;
SHOW INDEX FROM users;
SHOW INDEX FROM wages;
