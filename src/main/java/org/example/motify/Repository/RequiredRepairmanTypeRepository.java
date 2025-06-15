package org.example.motify.Repository;

import org.example.motify.Entity.RequiredRepairmanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface RequiredRepairmanTypeRepository extends JpaRepository<RequiredRepairmanType, Long> {
    // 根据工单ID查询所有工种需求
    List<RequiredRepairmanType> findByMaintenanceItem_ItemId(Long itemId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE required_repairman_types SET assigned = assigned + 1 WHERE item_id = :itemId AND type = :type", nativeQuery = true)
    int incrementAssignedCount(@Param("itemId") Long itemId, @Param("type") String type);
}
