package org.example.motify.Repository;

import org.example.motify.Entity.Salary;
import org.example.motify.Enum.RepairmanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, RepairmanType> {
    // 根据类型查找薪资
    Salary findByType(RepairmanType type);
    
    // 使用原生SQL插入新工种及其工资标准
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO salaries (type, hourly_rate) VALUES (:type, :hourlyRate)", nativeQuery = true)
    void insertSalary(@Param("type") String type, @Param("hourlyRate") Float hourlyRate);
    
    // 检查工种是否存在
    @Query(value = "SELECT COUNT(*) > 0 FROM salaries WHERE type = :type", nativeQuery = true)
    boolean existsByType(@Param("type") String type);
    
    // 更新现有工种的工资标准
    @Modifying
    @Transactional
    @Query(value = "UPDATE salaries SET hourly_rate = :hourlyRate WHERE type = :type", nativeQuery = true)
    int updateSalary(@Param("type") String type, @Param("hourlyRate") Float hourlyRate);
    
    // 根据不同工种类型批量插入工资标准
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO salaries (type, hourly_rate) VALUES " +
           "('MECHANIC', 80.0), ('ELECTRICIAN', 60.0), ('BODYWORKER', 45.0), " +
           "('PAINTER', 100.0), ('APPRENTICE', 35.0), ('INSPECTOR', 50.0), " +
           "('DIAGNOSER', 90.0) " +
           "ON DUPLICATE KEY UPDATE hourly_rate = VALUES(hourly_rate)", nativeQuery = true)
    void insertDefaultSalaries();
} 