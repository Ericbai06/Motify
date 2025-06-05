package org.example.motify.Repository;

import org.example.motify.Entity.RecordMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecordMaterialRepository extends JpaRepository<RecordMaterial, Long> {
    
    // 根据记录ID查找材料使用情况
    List<RecordMaterial> findByRecordId(Long recordId);
    
    // 根据材料ID查找使用记录
    List<RecordMaterial> findByMaterialId(Long materialId);
    
    // 根据记录ID和材料ID查找特定记录
    RecordMaterial findByRecordIdAndMaterialId(Long recordId, Long materialId);
    
    // 查找某材料的总使用量
    @Query("SELECT SUM(rm.amount) FROM RecordMaterial rm WHERE rm.materialId = :materialId")
    Integer findTotalUsageByMaterialId(@Param("materialId") Long materialId);
    
    // 查找某维修记录的材料费用总和
    @Query("SELECT SUM(m.price * rm.amount) FROM RecordMaterial rm JOIN Material m ON rm.materialId = m.materialId WHERE rm.recordId = :recordId")
    Double calculateMaterialCostByRecordId(@Param("recordId") Long recordId);
}