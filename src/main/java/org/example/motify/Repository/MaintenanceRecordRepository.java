package org.example.motify.Repository;

import org.example.motify.Entity.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    
    // 根据维修项目ID查找维修记录
    List<MaintenanceRecord> findByMaintenanceItem_ItemId(Long itemId);
    
    // 根据维修记录名称查找
    List<MaintenanceRecord> findByNameContaining(String name);
    
    // 根据材料ID查找维修记录
    @Query("SELECT mr FROM MaintenanceRecord mr JOIN mr.materials m WHERE m.materialId = :materialId")
    List<MaintenanceRecord> findByMaterialId(@Param("materialId") Long materialId);
    
    // 根据维修记录费用范围查找
    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.cost BETWEEN :minCost AND :maxCost")
    List<MaintenanceRecord> findByCostRange(@Param("minCost") Double minCost, @Param("maxCost") Double maxCost);
    
    // 查找特定维修项目的所有维修记录及其使用的材料
    @Query("SELECT mr FROM MaintenanceRecord mr LEFT JOIN FETCH mr.materials WHERE mr.maintenanceItem.itemId = :itemId")
    List<MaintenanceRecord> findByItemIdWithMaterials(@Param("itemId") Long itemId);
} 