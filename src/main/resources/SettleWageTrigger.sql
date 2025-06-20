-- 触发器：当maintenance_items工单状态变为COMPLETED时，自动为所有参与该工单的维修人员结算工资（插入/更新wages表）
CREATE TRIGGER trg_settle_wage_on_completed
AFTER UPDATE ON maintenance_items
FOR EACH ROW
BEGIN
    -- 只在状态从非COMPLETED变为COMPLETED时触发
    IF NEW.status = 'COMPLETED' AND OLD.status <> 'COMPLETED' THEN

        -- 遍历所有参与该工单的维修人员，结算工资
        INSERT INTO wages (
            hourly_rate,
            month,
            repairman_id,
            repairman_name,
            repairman_type,
            settlement_date,
            total_income,
            total_work_hours,
            year
        )
        SELECT
            s.hourly_rate,
            MONTH(NEW.complete_time),
            r.repairman_id,
            r.name,
            r.type,
            NOW(6),
            SUM(mr.work_hours/60.0 * s.hourly_rate),
            SUM(mr.work_hours/60.0),
            YEAR(NEW.complete_time)
        FROM maintenance_records mr
        JOIN repairmen r ON mr.repair_man_id = r.repairman_id
        JOIN salaries s ON r.type = s.type
        WHERE mr.item_id = NEW.item_id
        GROUP BY r.repairman_id, r.name, r.type, s.hourly_rate
        ON DUPLICATE KEY UPDATE
            hourly_rate = VALUES(hourly_rate),
            repairman_name = VALUES(repairman_name),
            repairman_type = VALUES(repairman_type),
            settlement_date = VALUES(settlement_date),
            total_income = VALUES(total_income),
            total_work_hours = VALUES(total_work_hours),
            year = VALUES(year),
            month = VALUES(month);

    END IF;
END