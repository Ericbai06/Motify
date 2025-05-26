package org.example.motify.Repository;

import org.example.motify.Entity.Repairman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RepairmanRepository extends JpaRepository<Repairman, Long> {
    Optional<Repairman> findByUsername(String username);
    boolean existsByUsername(String username);
    Optional<Repairman> findByEmail(String email);
    Optional<Repairman> findByPhone(String phone);
    
    @Query("SELECT r FROM Repairman r WHERE r.type = ?1")
    Optional<Repairman> findByType(String type);
} 