CREATE TRIGGER trg_required_repairman_type_updated
AFTER UPDATE ON required_repairman_types
FOR EACH ROW
BEGIN
    DECLARE v_item_id BIGINT;
    DECLARE v_total_types INT;
    DECLARE v_fulfilled_types INT;
    DECLARE v_current_status VARCHAR(50);
    
    -- 获取当前维修工单ID
    SET v_item_id = NEW.item_id;
    
    -- 获取当前工单状态
    SELECT status INTO v_current_status 
    FROM maintenance_items 
    WHERE item_id = v_item_id;
    
    -- 只有当工单状态为PENDING或ACCEPTED时才进行检查
    IF v_current_status IN ('PENDING', 'ACCEPTED') THEN
        -- 计算该工单所有工种的数量
        SELECT COUNT(*) INTO v_total_types
        FROM required_repairman_types
        WHERE item_id = v_item_id AND required > 0;
        
        -- 计算已满足分配要求的工种数量
        SELECT COUNT(*) INTO v_fulfilled_types
        FROM required_repairman_types
        WHERE item_id = v_item_id AND required > 0 AND assigned >= required;
        
        -- 如果所有工种都满足分配要求，更新工单状态为IN_PROGRESS
        IF v_total_types > 0 AND v_total_types = v_fulfilled_types THEN
            UPDATE maintenance_items
            SET status = 'IN_PROGRESS'
            WHERE item_id = v_item_id;
        END IF;
    END IF;
END 