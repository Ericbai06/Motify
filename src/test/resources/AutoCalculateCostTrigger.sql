DELIMITER $$
CREATE TRIGGER trg_maintenanceitem_completed
AFTER UPDATE ON maintenance_items
FOR EACH ROW
BEGIN
    IF NEW.status = 'COMPLETED' AND OLD.status <> 'COMPLETED' THEN
        -- 汇总工时费
        UPDATE maintenance_items mi
        SET mi.labor_cost = (
            SELECT IFNULL(SUM(mr.work_hours * r.hourly_rate), 0)
            FROM maintenance_records mr
            JOIN repairmen r ON mr.repair_man_id = r.repairman_id
            WHERE mr.item_id = NEW.item_id
        ),
        -- 汇总材料费用
        mi.material_cost = (
            SELECT IFNULL(SUM(m.price * rm.amount), 0)
            FROM maintenance_records mr
            JOIN record_material rm ON mr.record_id = rm.record_id
            JOIN materials m ON rm.material_id = m.material_id
            WHERE mr.item_id = NEW.item_id
        ),
        -- 总费用
        mi.cost = (
            SELECT IFNULL(SUM(mr.work_hours * r.hourly_rate), 0)
            FROM maintenance_records mr
            JOIN repairmen r ON mr.repair_man_id = r.repairman_id
            WHERE mr.item_id = NEW.item_id
        ) + (
            SELECT IFNULL(SUM(m.price * rm.amount), 0)
            FROM maintenance_records mr
            JOIN record_material rm ON mr.record_id = rm.record_id
            JOIN materials m ON rm.material_id = m.material_id
            WHERE mr.item_id = NEW.item_id
        )
        WHERE mi.item_id = NEW.item_id;
    END IF;
END$$
DELIMITER ;