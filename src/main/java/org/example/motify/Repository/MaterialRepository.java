package org.example.motify.Repository;

import org.example.motify.Entity.Material;
import org.example.motify.Enum.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    
    // 根据材料名称查找
    List<Material> findByNameContaining(String name);
    
    // 根据材料类型查找
    List<Material> findByType(MaterialType type);
    
    // 根据库存数量范围查找
    @Query("SELECT m FROM Material m WHERE m.stock BETWEEN :minStock AND :maxStock")
    List<Material> findByStockRange(@Param("minStock") Integer minStock, @Param("maxStock") Integer maxStock);
    
    // 根据维修记录ID查找材料（使用原生SQL查询）
    @Query(value = "SELECT m.* FROM materials m " +
            "JOIN record_material rm ON m.material_id = rm.material_id " +
            "WHERE rm.record_id = :recordId", nativeQuery = true)
    List<Material> findByRecordId(@Param("recordId") Long recordId);
    
    // 查找特定价格范围的材料
    @Query("SELECT m FROM Material m WHERE m.price BETWEEN :minPrice AND :maxPrice")
    List<Material> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
} 