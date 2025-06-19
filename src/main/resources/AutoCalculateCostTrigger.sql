CREATE TRIGGER trg_maintenanceitem_completed
BEFORE UPDATE ON maintenance_items
FOR EACH ROW
BEGIN
    -- 只在状态从非COMPLETED变为COMPLETED时计算费用
    IF NEW.status = 'COMPLETED' AND OLD.status <> 'COMPLETED' THEN
        
        -- 计算工时费：SUM(工作时长 × 维修人员时薪)
        SET NEW.labor_cost = (
            SELECT IFNULL(SUM(mr.work_hours/60.0 * s.hourly_rate), 0)
            FROM maintenance_records mr
            JOIN repairmen r ON mr.repair_man_id = r.repairman_id
            JOIN salaries s ON r.type = s.type
            WHERE mr.item_id = NEW.item_id
        );
        
        -- 计算材料费：SUM(材料单价 × 使用数量)
        SET NEW.material_cost = (
            SELECT IFNULL(SUM(m.price * rm.amount), 0)
            FROM maintenance_records mr
            JOIN record_material rm ON mr.record_id = rm.record_id
            JOIN materials m ON rm.material_id = m.material_id
            WHERE mr.item_id = NEW.item_id
        );
        
        -- 计算总费用：工时费 + 材料费
        SET NEW.cost = IFNULL(NEW.labor_cost, 0) + IFNULL(NEW.material_cost, 0);
        
        -- 设置完成时间
        SET NEW.complete_time = NOW(6);
        
    END IF;
END