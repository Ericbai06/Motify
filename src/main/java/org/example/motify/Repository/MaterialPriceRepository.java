package org.example.motify.Repository;

import org.example.motify.Entity.MaterialPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialPriceRepository extends JpaRepository<MaterialPrice, Long> {
} 