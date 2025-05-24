package org.example.motify.Repository;

import org.example.motify.Entity.MaintenanceRecord;
import org.example.motify.Enum.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {
    List<MaintenanceRecord> findByRepairman_RepairmanId(Long repairmanId);
    List<MaintenanceRecord> findByRecordInfo_CreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    // 通过状态查询维修记录
    List<MaintenanceRecord> findByStatus(MaintenanceStatus status);

    // 通过用户ID查询该用户的所有维修记录
    List<MaintenanceRecord> findByCar_User_UserId(Long userId);
}