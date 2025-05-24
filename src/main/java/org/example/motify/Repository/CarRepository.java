package org.example.motify.Repository;

import org.example.motify.Entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByUser_UserId(Long userId);
    List<Car> findByBrandAndModel(String brand, String model);
} 