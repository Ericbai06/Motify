CREATE TRIGGER trg_maintenancerecord_cost
AFTER INSERT ON maintenance_records
FOR EACH ROW
BEGIN
    -- 计算工时费
    UPDATE maintenance_records
    SET labor_cost = (
        SELECT IFNULL(NEW.work_hours / 60.0 * s.hourly_rate, 0)
        FROM repairmen r
        JOIN salaries s ON r.type = s.type
        WHERE r.repairman_id = NEW.repair_man_id
    ),
    material_cost = (
        SELECT IFNULL(SUM(m.price * rm.amount), 0)
        FROM record_material rm
        JOIN materials m ON rm.material_id = m.material_id
        WHERE rm.record_id = NEW.record_id
    ),
    cost = IFNULL(labor_cost, 0) + IFNULL(material_cost, 0)
    WHERE record_id = NEW.record_id;
END