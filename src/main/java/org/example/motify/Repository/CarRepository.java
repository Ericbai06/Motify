package org.example.motify.Repository;

import org.example.motify.Entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByUser_UserId(Long userId);
    List<Car> findByLicensePlate(String licensePlate);
    List<Car> findByBrand(String brand);
    List<Car> findByModel(String model);
    @Query("SELECT c FROM Car c WHERE c.user.userId = :userId")
    List<Car> findByUserId(@Param("userId") Long userId);

    // 新增：使用原生SQL，只返回基本字段
    @Query(value = "SELECT c.car_id, c.brand, c.model, c.license_plate, " +
                   "u.user_id, u.username, u.name, u.phone, u.email " +
                   "FROM cars c " +
                   "INNER JOIN users u ON c.user_id = u.user_id " +
                   "WHERE u.user_id = :userId " +
                   "ORDER BY c.car_id", 
           nativeQuery = true)
    List<Object[]> findCarBasicInfoByUserId(@Param("userId") Long userId);
} 