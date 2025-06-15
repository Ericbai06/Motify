-- 修改外键约束为CASCADE删除
-- 这将允许删除maintenance_items时自动删除相关的子表记录

-- 1. 修改 item_repairman 表的外键约束
ALTER TABLE item_repairman DROP FOREIGN KEY fk_item_repairman_item;
ALTER TABLE item_repairman 
ADD CONSTRAINT fk_item_repairman_item 
FOREIGN KEY (item_id) REFERENCES maintenance_items(item_id) 
ON DELETE CASCADE ON UPDATE RESTRICT;

-- 2. 修改 maintenance_records 表的外键约束
ALTER TABLE maintenance_records DROP FOREIGN KEY fk_maintenance_record_item;
ALTER TABLE maintenance_records 
ADD CONSTRAINT fk_maintenance_record_item 
FOREIGN KEY (item_id) REFERENCES maintenance_items(item_id) 
ON DELETE CASCADE ON UPDATE RESTRICT;

-- 3. 修改 record_material 表的外键约束（如果需要的话）
-- record_material 依赖于 maintenance_records，所以应该也设置为CASCADE
ALTER TABLE record_material DROP FOREIGN KEY record_material_ibfk_1;
ALTER TABLE record_material 
ADD CONSTRAINT record_material_ibfk_1 
FOREIGN KEY (record_id) REFERENCES maintenance_records(record_id) 
ON DELETE CASCADE ON UPDATE RESTRICT;

-- 4. 修改 required_repairman_types 表的外键约束
ALTER TABLE required_repairman_types DROP FOREIGN KEY fk_required_type_maintenance_item;
ALTER TABLE required_repairman_types 
ADD CONSTRAINT fk_required_type_maintenance_item 
FOREIGN KEY (item_id) REFERENCES maintenance_items(item_id) 
ON DELETE CASCADE ON UPDATE RESTRICT;

-- 验证外键约束设置
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME,
    DELETE_RULE,
    UPDATE_RULE
FROM 
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE K
    JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R 
    ON K.CONSTRAINT_NAME = R.CONSTRAINT_NAME
WHERE 
    K.TABLE_SCHEMA = 'motify' 
    AND K.REFERENCED_TABLE_NAME = 'maintenance_items'
ORDER BY 
    K.TABLE_NAME, K.COLUMN_NAME;
