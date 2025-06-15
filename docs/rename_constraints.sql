-- 重命名外键约束脚本
-- 执行前请确保已连接到 motify 数据库

USE motify;

-- 1. 重命名 cars 表的外键约束
-- 删除旧约束：FKqw4c8e6nqrvy3ti1xj8w8wyc9
-- 添加新约束：fk_car_user
ALTER TABLE cars DROP FOREIGN KEY FKqw4c8e6nqrvy3ti1xj8w8wyc9;
ALTER TABLE cars 
ADD CONSTRAINT fk_car_user 
FOREIGN KEY (user_id) REFERENCES users (user_id);

-- 2. 重命名 item_repairman 表的外键约束
-- 删除旧约束：FK19r2r1n11oy0var7hk0jmkbwr
-- 添加新约束：fk_item_repairman_item
ALTER TABLE item_repairman DROP FOREIGN KEY FK19r2r1n11oy0var7hk0jmkbwr;
ALTER TABLE item_repairman 
ADD CONSTRAINT fk_item_repairman_item 
FOREIGN KEY (item_id) REFERENCES maintenance_items (item_id);

-- 删除旧约束：FKl3mjeo5dyopnpqnmp05uqkift
-- 添加新约束：fk_item_repairman_repairman
ALTER TABLE item_repairman DROP FOREIGN KEY FKl3mjeo5dyopnpqnmp05uqkift;
ALTER TABLE item_repairman 
ADD CONSTRAINT fk_item_repairman_repairman 
FOREIGN KEY (repairman_id) REFERENCES repairmen (repairman_id);

-- 3. 重命名 maintenance_items 表的外键约束
-- 删除旧约束：FK4ahy8jqhv98sn0off3lm5jevf
-- 添加新约束：fk_maintenance_item_car
ALTER TABLE maintenance_items DROP FOREIGN KEY FK4ahy8jqhv98sn0off3lm5jevf;
ALTER TABLE maintenance_items 
ADD CONSTRAINT fk_maintenance_item_car 
FOREIGN KEY (car_id) REFERENCES cars (car_id);

-- 4. 重命名 repairmen 表的外键约束
-- 删除旧约束：FKo0p1w76q7b5qwnqaf9qfptlea
-- 添加新约束：fk_repairman_salary_type
ALTER TABLE repairmen DROP FOREIGN KEY FKo0p1w76q7b5qwnqaf9qfptlea;
ALTER TABLE repairmen 
ADD CONSTRAINT fk_repairman_salary_type 
FOREIGN KEY (type) REFERENCES salaries (type);

-- 5. 重命名 required_repairman_types 表的外键约束
-- 删除旧约束：FK4xmkvjv7amo0n7b9xemh51op0
-- 添加新约束：fk_required_type_maintenance_item
ALTER TABLE required_repairman_types DROP FOREIGN KEY FK4xmkvjv7amo0n7b9xemh51op0;
ALTER TABLE required_repairman_types 
ADD CONSTRAINT fk_required_type_maintenance_item 
FOREIGN KEY (item_id) REFERENCES maintenance_items (item_id);

-- 6. 重命名 wages 表的外键约束
-- 删除旧约束：FK2e0cow4qra7bap3ir892f3r2i
-- 添加新约束：fk_wage_repairman
ALTER TABLE wages DROP FOREIGN KEY FK2e0cow4qra7bap3ir892f3r2i;
ALTER TABLE wages 
ADD CONSTRAINT fk_wage_repairman 
FOREIGN KEY (repairman_id) REFERENCES repairmen (repairman_id);

-- 1. maintenance_records表 - 重命名item_id外键约束
ALTER TABLE `maintenance_records` 
DROP FOREIGN KEY `FKsq6ax65x22cfi6xb4iq2ynjv5`;

ALTER TABLE `maintenance_records` 
ADD CONSTRAINT `fk_maintenance_record_item` 
FOREIGN KEY (`item_id`) REFERENCES `maintenance_items` (`item_id`) 
ON DELETE RESTRICT ON UPDATE RESTRICT;

-- 注意：以下约束已经有了合适的名称，无需修改：
-- - fk_record_item (maintenance_records 表)
-- - fk_record_material (record_material 表)

-- 验证约束重命名结果
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM 
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE 
    TABLE_SCHEMA = 'motify' 
    AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, CONSTRAINT_NAME;
