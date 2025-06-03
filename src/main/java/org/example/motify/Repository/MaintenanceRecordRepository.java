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
    
    // 根据材料ID查找维修记录（使用原生SQL查询）
    @Query(value = "SELECT mr.* FROM maintenance_records mr " +
            "JOIN record_material rm ON mr.record_id = rm.record_id " +
            "WHERE rm.material_id = :materialId", nativeQuery = true)
    List<MaintenanceRecord> findByMaterialId(@Param("materialId") Long materialId);
    
    // 根据维修记录费用范围查找
    @Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.cost BETWEEN :minCost AND :maxCost")
    List<MaintenanceRecord> findByCostRange(@Param("minCost") Double minCost, @Param("maxCost") Double maxCost);
    
    // 查找特定维修项目的所有维修记录及其使用的材料
    @Query("SELECT mr FROM MaintenanceRecord mr LEFT JOIN FETCH mr.materialAmounts WHERE mr.maintenanceItem.itemId = :itemId")
    List<MaintenanceRecord> findByItemIdWithMaterials(@Param("itemId") Long itemId);

    // 查找特定维修记录的材料使用情况（使用原生SQL查询）
    @Query(value = "SELECT mr.*, m.*, rm.amount " +
            "FROM maintenance_records mr " +
            "LEFT JOIN record_material rm ON mr.record_id = rm.record_id " +
            "LEFT JOIN materials m ON rm.material_id = m.material_id " +
            "WHERE mr.record_id = :recordId", nativeQuery = true)
    List<Object[]> findRecordWithMaterialsAndAmounts(@Param("recordId") Long recordId);
} 