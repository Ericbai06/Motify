package org.example.motify.Repository;

import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Enum.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import java.util.List;

@Repository
public interface MaintenanceItemRepository extends JpaRepository<MaintenanceItem, Long> {
        // 通过用户ID查找该用户的所有维修项目
        List<MaintenanceItem> findByCar_User_UserId(Long userId);

        // 获取所有维修工单信息，包含关联的维修记录

        @Query("SELECT DISTINCT mi FROM MaintenanceItem mi " +
                        "LEFT JOIN FETCH mi.maintenanceRecords " +
                        "LEFT JOIN FETCH mi.car " +
                        "ORDER BY mi.createTime DESC")
        List<MaintenanceItem> findAllWithRecords();

        // 根据状态获取维修工单
        @Query("SELECT mi FROM MaintenanceItem mi WHERE mi.status = :status")
        List<MaintenanceItem> findByStatus(MaintenanceStatus status);

        // 查询与维修人员相关的所有工单（包括已拒绝的）
        @Query(value = "SELECT DISTINCT mi.* FROM maintenance_items mi " +
                        "LEFT JOIN item_repairman ir ON mi.item_id = ir.item_id " +
                        "WHERE ir.repairman_id = :repairmanId OR " +
                        "mi.item_id IN (SELECT DISTINCT record.item_id FROM maintenance_records record WHERE record.repair_man_id = :repairmanId)", nativeQuery = true)
        List<MaintenanceItem> findByRepairmanId(@Param("repairmanId") Long repairmanId);

        @Query(value = "SELECT COUNT(*) FROM maintenance_items WHERE item_id = :itemId AND status = 'ACCEPTED'", nativeQuery = true)
        int countByItemIdAndStatusAccepted(@Param("itemId") Long itemId);

        @Query(value = "SELECT status FROM maintenance_items WHERE item_id = :itemId", nativeQuery = true)
        String getItemStatus(@Param("itemId") Long itemId);

        @Modifying
        @Transactional
        @Query(value = "INSERT INTO item_repairman (item_id, repairman_id, is_accepted) VALUES (:itemId, :repairmanId, 1) ON DUPLICATE KEY UPDATE is_accepted = 1", nativeQuery = true)
        int addRepairmanToItem(@Param("itemId") Long itemId, @Param("repairmanId") Long repairmanId);

        @Modifying
        @Transactional
        @Query(value = "INSERT INTO item_repairman (item_id, repairman_id, is_accepted) VALUES (:itemId, :repairmanId, 0) ON DUPLICATE KEY UPDATE is_accepted = 0", nativeQuery = true)
        int rejectRepairmanForItem(@Param("itemId") Long itemId, @Param("repairmanId") Long repairmanId);
}