package org.example.motify.Repository;

import org.example.motify.Entity.MaintenanceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaintenanceItemRepository extends JpaRepository<MaintenanceItem, Long> {
    // 通过用户ID查找该用户的所有维修项目
    List<MaintenanceItem> findByCar_User_UserId(Long userId);
}