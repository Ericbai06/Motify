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

    @Query("SELECT r FROM Repairman r WHERE r.type = :type " +
            "ORDER BY (SELECT COUNT(m) FROM MaintenanceItem m JOIN m.repairmenAcceptance ra " +
            "WHERE KEY(ra) = r AND m.status = 'IN_PROGRESS')")
    List<Repairman> findByTypeOrderByWorkloadAsc(@Param("type") RepairmanType type);
}