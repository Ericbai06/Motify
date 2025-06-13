package org.example.motify.Repository;

import org.example.motify.Entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    List<UserHistory> findByUserIdOrderByOperationTimeDesc(Long userId);
    UserHistory findTop1ByUserIdAndOperationTimeLessThanOrderByOperationTimeDesc(Long userId, LocalDateTime operationTime);
    UserHistory findTop1ByUserIdAndOperationTimeGreaterThanOrderByOperationTimeAsc(Long userId, LocalDateTime operationTime);
}
