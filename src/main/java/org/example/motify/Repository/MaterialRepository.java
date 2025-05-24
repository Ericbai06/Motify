package org.example.motify.Repository;

import org.example.motify.Entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByMaintenanceRecord_RecordId(Long recordId);
} 