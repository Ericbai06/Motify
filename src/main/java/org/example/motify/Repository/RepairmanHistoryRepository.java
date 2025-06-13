package org.example.motify.Repository;

import org.example.motify.Entity.RepairmanHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RepairmanHistoryRepository extends JpaRepository<RepairmanHistory, Long> {
    List<RepairmanHistory> findByRepairmanIdOrderByOperationTimeDesc(Long repairmanId);
    RepairmanHistory findTop1ByRepairmanIdAndOperationTimeLessThanOrderByOperationTimeDesc(Long repairmanId, LocalDateTime operationTime);
    RepairmanHistory findTop1ByRepairmanIdAndOperationTimeGreaterThanOrderByOperationTimeAsc(Long repairmanId, LocalDateTime operationTime);
}
