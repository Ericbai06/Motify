package org.example.motify.Repository;

import org.example.motify.Entity.Material;
import org.example.motify.Enum.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
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

    // ============== 库存管理相关方法 ==============

    /**
     * 减少材料库存
     * 
     * @param materialId 材料ID
     * @param amount     减少数量
     * @return 更新的行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Material m SET m.stock = m.stock - :amount WHERE m.materialId = :materialId AND m.stock >= :amount")
    int reduceStock(@Param("materialId") Long materialId, @Param("amount") Integer amount);

    /**
     * 检查材料库存是否充足
     * 
     * @param materialId     材料ID
     * @param requiredAmount 需要数量
     * @return 是否库存充足
     */
    @Query("SELECT CASE WHEN m.stock >= :requiredAmount THEN true ELSE false END FROM Material m WHERE m.materialId = :materialId")
    Boolean isStockSufficient(@Param("materialId") Long materialId, @Param("requiredAmount") Integer requiredAmount);

    /**
     * 获取材料当前库存
     * 
     * @param materialId 材料ID
     * @return 当前库存数量
     */
    @Query("SELECT m.stock FROM Material m WHERE m.materialId = :materialId")
    Integer getCurrentStock(@Param("materialId") Long materialId);

    /**
     * 增加材料库存（用于入库或退库）
     * 
     * @param materialId 材料ID
     * @param amount     增加数量
     * @return 更新的行数
     */
    @Modifying
    @Transactional
    @Query("UPDATE Material m SET m.stock = m.stock + :amount WHERE m.materialId = :materialId")
    int increaseStock(@Param("materialId") Long materialId, @Param("amount") Integer amount);

    /**
     * 批量检查多个材料的库存是否充足
     * 
     * @param materialIds 材料ID列表
     * @return 库存不足的材料ID列表
     */
    @Query(value = """
            SELECT m.material_id
            FROM materials m
            JOIN record_material rm ON m.material_id = rm.material_id
            WHERE rm.record_id = :recordId AND m.stock < rm.amount
            """, nativeQuery = true)
    List<Long> findInsufficientStockMaterials(@Param("recordId") Long recordId);
}