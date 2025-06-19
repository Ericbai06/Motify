package org.example.motify.Repository;

import org.example.motify.Entity.Repairman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.example.motify.Enum.RepairmanType;

@Repository
public interface RepairmanRepository extends JpaRepository<Repairman, Long> {
    Optional<Repairman> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Repairman> findByEmail(String email);

    Optional<Repairman> findByPhone(String phone);

    @Query("SELECT r FROM Repairman r WHERE r.type = ?1")
    Optional<Repairman> findByType(String type);

    @Query(value = "SELECT r.* " +
            "FROM repairmen r " +
            "LEFT JOIN item_repairman ir ON r.repairman_id = ir.repairman_id " +
            "LEFT JOIN maintenance_items m ON ir.item_id = m.item_id " +
            "    AND (m.status = 'IN_PROGRESS' OR m.status = 'PENDING' OR m.status = 'ACCEPTED') " +
            "WHERE r.type = ?#{#type.name()} " +
            "GROUP BY r.repairman_id " +
            "ORDER BY COUNT(m.item_id)", nativeQuery = true)
    List<Repairman> findByTypeOrderByWorkloadAsc(@Param("type") RepairmanType type);

    @Query("SELECT r.type FROM Repairman r WHERE r.repairmanId = :repairmanId")
    RepairmanType findTypeByRepairmanId(@Param("repairmanId") Long repairmanId);
}