package org.example.motify.Repository;

import org.example.motify.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    /**
     * 根据用户名查找管理员 - 使用原生SQL
     */
    @Query(value = "SELECT * FROM admins WHERE username = :username", nativeQuery = true)
    Optional<Admin> findByUsername(@Param("username") String username);
    
    // /**
    //  * 检查用户名是否存在 - 使用原生SQL
    //  */
    // @Query(value = "SELECT COUNT(*) > 0 FROM admins WHERE username = :username", nativeQuery = true)
    // boolean existsByUsername(@Param("username") String username);
    /**
     * 更新管理员最后登录时间 - 使用原生SQL
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE admins SET last_login_time = :loginTime WHERE admin_id = :adminId", nativeQuery = true)
    void updateLastLoginTime(@Param("adminId") Long adminId, @Param("loginTime") LocalDateTime loginTime);
    
    /**
     * 根据邮箱查找管理员 - 使用原生SQL
     */
    @Query(value = "SELECT * FROM admins WHERE email = :email", nativeQuery = true)
    Optional<Admin> findByEmail(@Param("email") String email);
    
    /**
     * 激活或禁用管理员账户 - 使用原生SQL
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE admins SET is_active = :isActive WHERE admin_id = :adminId", nativeQuery = true)
    void updateActiveStatus(@Param("adminId") Long adminId, @Param("isActive") boolean isActive);
    
    // =============== 数据统计查询方法 ===============
    
    /**
     * 统计各车型的维修次数与平均维修费用
     */
    @Query(value = """
        SELECT 
            c.brand,
            c.model,
            COUNT(mi.item_id) as repair_count,
            ROUND(AVG(mi.cost), 2) as avg_cost,
            ROUND(SUM(mi.cost), 2) as total_cost
        FROM cars c 
        LEFT JOIN maintenance_items mi ON c.car_id = mi.car_id 
        WHERE mi.status = 'COMPLETED'
        GROUP BY c.brand, c.model 
        ORDER BY repair_count DESC, avg_cost DESC
        """, nativeQuery = true)
    List<Object[]> getCarModelRepairStatistics();
    
    /**
     * 统计特定车型最常出现的故障类型（基于维修项目名称）
     */
    @Query(value = """
        SELECT 
            mi.name as fault_type,
            COUNT(mi.item_id) as occurrence_count,
            ROUND(AVG(mi.cost), 2) as avg_repair_cost
        FROM maintenance_items mi 
        JOIN cars c ON mi.car_id = c.car_id 
        WHERE c.brand = :brand AND c.model = :model 
        AND mi.status = 'COMPLETED'
        GROUP BY mi.name 
        ORDER BY occurrence_count DESC 
        LIMIT 10
        """, nativeQuery = true)
    List<Object[]> getCarModelFaultStatistics(@Param("brand") String brand, @Param("model") String model);
    
    /**
     * 按月份统计维修费用构成（基于维修记录）
     */
    @Query(value = """
        SELECT 
            YEAR(mr.start_time) as year,
            MONTH(mr.start_time) as month,
            COUNT(mr.record_id) as total_records,
            ROUND(SUM(IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)), 2) as total_cost,
            ROUND(SUM(IFNULL(material_cost.material_cost, 0)), 2) as total_material_cost,
            ROUND(SUM(IFNULL(labor_cost.labor_cost, 0)), 2) as total_labor_cost,
            ROUND(AVG(CASE WHEN (IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)) > 0 THEN IFNULL(material_cost.material_cost, 0) / (IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)) * 100 ELSE 0 END), 2) as material_cost_percentage,
            ROUND(AVG(CASE WHEN (IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)) > 0 THEN IFNULL(labor_cost.labor_cost, 0) / (IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)) * 100 ELSE 0 END), 2) as labor_cost_percentage
        FROM maintenance_records mr 
        LEFT JOIN (
            SELECT 
                rm.record_id,
                SUM(m.price * rm.amount) as material_cost
            FROM record_material rm
            JOIN materials m ON rm.material_id = m.material_id
            GROUP BY rm.record_id
        ) material_cost ON mr.record_id = material_cost.record_id
        LEFT JOIN (
            SELECT 
                mr_labor.record_id,
                (mr_labor.work_hours * s.hourly_rate / 60.0) as labor_cost
            FROM maintenance_records mr_labor
            JOIN repairmen r ON mr_labor.repair_man_id = r.repairman_id
            JOIN salaries s ON r.type = s.type
        ) labor_cost ON mr.record_id = labor_cost.record_id
        WHERE mr.start_time >= :startDate 
        AND mr.start_time <= :endDate
        GROUP BY YEAR(mr.start_time), MONTH(mr.start_time) 
        ORDER BY year DESC, month DESC
        """, nativeQuery = true)
    List<Object[]> getMonthlyCostAnalysis(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 按季度统计维修费用构成（基于维修记录）
     */
    @Query(value = """
        SELECT 
            YEAR(mr.start_time) as year,
            QUARTER(mr.start_time) as quarter,
            COUNT(mr.record_id) as total_records,
            ROUND(SUM(IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)), 2) as total_cost,
            ROUND(SUM(IFNULL(material_cost.material_cost, 0)), 2) as total_material_cost,
            ROUND(SUM(IFNULL(labor_cost.labor_cost, 0)), 2) as total_labor_cost,
            ROUND(AVG(CASE WHEN (IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)) > 0 THEN IFNULL(material_cost.material_cost, 0) / (IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)) * 100 ELSE 0 END), 2) as material_cost_percentage,
            ROUND(AVG(CASE WHEN (IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)) > 0 THEN IFNULL(labor_cost.labor_cost, 0) / (IFNULL(material_cost.material_cost, 0) + IFNULL(labor_cost.labor_cost, 0)) * 100 ELSE 0 END), 2) as labor_cost_percentage
        FROM maintenance_records mr 
        LEFT JOIN (
            SELECT 
                rm.record_id,
                SUM(m.price * rm.amount) as material_cost
            FROM record_material rm
            JOIN materials m ON rm.material_id = m.material_id
            GROUP BY rm.record_id
        ) material_cost ON mr.record_id = material_cost.record_id
        LEFT JOIN (
            SELECT 
                mr_labor.record_id,
                (mr_labor.work_hours * s.hourly_rate / 60.0) as labor_cost
            FROM maintenance_records mr_labor
            JOIN repairmen r ON mr_labor.repair_man_id = r.repairman_id
            JOIN salaries s ON r.type = s.type
        ) labor_cost ON mr.record_id = labor_cost.record_id
        WHERE mr.start_time >= :startDate 
        AND mr.start_time <= :endDate
        GROUP BY YEAR(mr.start_time), QUARTER(mr.start_time) 
        ORDER BY year DESC, quarter DESC
        """, nativeQuery = true)
    List<Object[]> getQuarterlyCostAnalysis(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 筛选负面反馈工单及涉及的员工（评分<=2）
     */
    @Query(value = """
        SELECT 
            mi.item_id,
            mi.name as item_name,
            mi.score,
            mi.result,
            c.brand,
            c.model,
            c.license_plate,
            r.repairman_id,
            r.name as repairman_name,
            r.type as repairman_type
        FROM maintenance_items mi 
        JOIN cars c ON mi.car_id = c.car_id 
        JOIN item_repairman ir ON mi.item_id = ir.item_id 
        JOIN repairmen r ON ir.repairman_id = r.repairman_id 
        WHERE mi.score IS NOT NULL 
        AND mi.score <= :maxScore 
        AND ir.is_accepted = true
        ORDER BY mi.score ASC, mi.update_time DESC
        """, nativeQuery = true)
    List<Object[]> getNegativeFeedbackOrders(@Param("maxScore") Integer maxScore);
    
    /**
     * 统计不同工种在一段时间内接受和完成的任务数量及占比
     */
    @Query(value = """
        SELECT 
            r.type as repairman_type,
            COUNT(DISTINCT ir.item_id) as accepted_tasks,
            COUNT(DISTINCT CASE WHEN mi.status = 'COMPLETED' THEN ir.item_id END) as completed_tasks,
            ROUND(COUNT(DISTINCT ir.item_id) * 100.0 / (
                SELECT COUNT(DISTINCT item_id) 
                FROM item_repairman 
                WHERE is_accepted = true
            ), 2) as accepted_percentage,
            ROUND(COUNT(DISTINCT CASE WHEN mi.status = 'COMPLETED' THEN ir.item_id END) * 100.0 / (
                SELECT COUNT(DISTINCT mi2.item_id) 
                FROM maintenance_items mi2 
                WHERE mi2.status = 'COMPLETED'
            ), 2) as completed_percentage
        FROM repairmen r 
        JOIN item_repairman ir ON r.repairman_id = ir.repairman_id 
        JOIN maintenance_items mi ON ir.item_id = mi.item_id 
        WHERE ir.is_accepted = true 
        AND mi.create_time >= :startDate 
        AND mi.create_time <= :endDate
        GROUP BY r.type 
        ORDER BY accepted_tasks DESC
        """, nativeQuery = true)
    List<Object[]> getRepairmanTypeTaskStatistics(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 统计未完成的维修任务/订单数量和类型
     */
    @Query(value = """
        SELECT 
            mi.status,
            COUNT(mi.item_id) as task_count,
            ROUND(AVG(DATEDIFF(NOW(), mi.create_time)), 0) as avg_days_pending
        FROM maintenance_items mi 
        WHERE mi.status IN ('PENDING', 'IN_PROGRESS') 
        GROUP BY mi.status 
        ORDER BY task_count DESC
        """, nativeQuery = true)
    List<Object[]> getUncompletedTasksOverview();
    
    /**
     * 按工种统计未完成任务
     */
    @Query(value = """
        SELECT 
            r.type as repairman_type,
            COUNT(DISTINCT mi.item_id) as uncompleted_tasks,
            ROUND(AVG(DATEDIFF(NOW(), mi.create_time)), 0) as avg_days_pending
        FROM maintenance_items mi 
        JOIN item_repairman ir ON mi.item_id = ir.item_id 
        JOIN repairmen r ON ir.repairman_id = r.repairman_id 
        WHERE mi.status IN ('PENDING', 'IN_PROGRESS') 
        AND ir.is_accepted = true
        GROUP BY r.type 
        ORDER BY uncompleted_tasks DESC
        """, nativeQuery = true)
    List<Object[]> getUncompletedTasksByRepairmanType();
    
    /**
     * 按维修人员统计未完成任务
     */
    @Query(value = """
        SELECT 
            r.repairman_id,
            r.name as repairman_name,
            r.type as repairman_type,
            COUNT(mi.item_id) as uncompleted_tasks,
            ROUND(AVG(DATEDIFF(NOW(), mi.create_time)), 0) as avg_days_pending
        FROM maintenance_items mi 
        JOIN item_repairman ir ON mi.item_id = ir.item_id 
        JOIN repairmen r ON ir.repairman_id = r.repairman_id 
        WHERE mi.status IN ('PENDING', 'IN_PROGRESS') 
        AND ir.is_accepted = true
        GROUP BY r.repairman_id, r.name, r.type 
        ORDER BY uncompleted_tasks DESC
        """, nativeQuery = true)
    List<Object[]> getUncompletedTasksByRepairman();
    
    /**
     * 按车辆统计未完成任务
     */
    @Query(value = """
        SELECT 
            c.car_id,
            c.brand,
            c.model,
            c.license_plate,
            COUNT(mi.item_id) as uncompleted_tasks,
            ROUND(AVG(DATEDIFF(NOW(), mi.create_time)), 0) as avg_days_pending
        FROM maintenance_items mi 
        JOIN cars c ON mi.car_id = c.car_id 
        WHERE mi.status IN ('PENDING', 'IN_PROGRESS') 
        GROUP BY c.car_id, c.brand, c.model, c.license_plate 
        ORDER BY uncompleted_tasks DESC
        """, nativeQuery = true)
    List<Object[]> getUncompletedTasksByCar();

    /**
     * 删除维修工单 - 按照外键依赖顺序删除相关数据
     * 1. 删除材料使用记录
     * 2. 删除维修记录
     * 3. 删除工单维修工关联
     * 4. 删除主工单
     */
    @Modifying
    @Transactional
    @Query(value = """
        DELETE rm FROM record_material rm 
        INNER JOIN maintenance_records mr ON rm.record_id = mr.record_id 
        WHERE mr.item_id = :itemId
        """, nativeQuery = true)
    void deleteRecordMaterialByItemId(@Param("itemId") Long itemId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM maintenance_records WHERE item_id = :itemId", nativeQuery = true)
    void deleteMaintenanceRecordsByItemId(@Param("itemId") Long itemId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM item_repairman WHERE item_id = :itemId", nativeQuery = true)
    void deleteItemRepairmanByItemId(@Param("itemId") Long itemId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM maintenance_items WHERE item_id = :itemId", nativeQuery = true)
    void deleteMaintenanceItemById(@Param("itemId") Long itemId);

    /**
     * 检查工单是否存在
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM maintenance_items WHERE item_id = :itemId", nativeQuery = true)
    boolean existsMaintenanceItemById(@Param("itemId") Long itemId);

    /**
     * 删除维修工单 - 利用数据库级联删除功能，只需删除主工单
     * 相关的维修记录、材料使用记录、工单维修工关联等会自动删除
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM maintenance_items WHERE item_id = :itemId", nativeQuery = true)
    void deleteMaintenanceItem(@Param("itemId") Long itemId);
}