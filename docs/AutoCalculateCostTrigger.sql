CREATE TRIGGER trg_maintenanceitem_completed
BEFORE UPDATE ON maintenance_items
FOR EACH ROW
BEGIN
    -- 只在状态从非COMPLETED变为COMPLETED时计算费用
    IF NEW.status = 'COMPLETED' AND OLD.status <> 'COMPLETED' THEN
        
        -- 计算工时费：SUM(工作时长 × 维修人员时薪)
        SET NEW.labor_cost = (
            SELECT IFNULL(SUM(mr.work_hours\60*.hourly_rate), 0)
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

-- 该触发器 trg_maintenanceitem_completed 用于在 maintenance_items 表的工单状态变为 COMPLETED（已完成）时，自动计算并更新该工单的费用信息。其主要功能如下：
-- 触发时机
-- 当 maintenance_items 表有记录被更新，且 status 字段由非 COMPLETED 变为 COMPLETED 时自动触发。
-- 自动汇总费用
-- 工时费（labor_cost）：统计该工单下所有维修记录的工时（work_hours）乘以对应维修人员的时薪（hourly_rate）之和。
-- 材料费（material_cost）：统计该工单下所有维修记录所用材料的总费用（材料单价 × 数量）。
-- 总费用（cost）：等于工时费与材料费之和。
-- 自动更新
-- 以上三项费用会自动写回到当前工单的 labor_cost、material_cost、cost 字段，无需手动计算。
-- 用途与意义：
-- 该触发器确保每当工单被标记为已完成时，相关费用能自动、准确地汇总和更新，避免人工计算错误，提高数据一致性和维护效率。