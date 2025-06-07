package org.example.motify.Repository;

import org.example.motify.Entity.RequiredRepairmanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RequiredRepairmanTypeRepository extends JpaRepository<RequiredRepairmanType, Long> {
    // 根据工单ID查询所有工种需求
    List<RequiredRepairmanType> findByMaintenanceItem_ItemId(Long itemId);
}
