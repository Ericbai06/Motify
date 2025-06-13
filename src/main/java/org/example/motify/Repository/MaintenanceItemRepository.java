package org.example.motify.Repository;

import org.example.motify.Entity.MaintenanceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
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
}