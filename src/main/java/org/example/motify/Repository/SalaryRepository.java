package org.example.motify.Repository;

import org.example.motify.Entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByRepairman_RepairmanId(Long repairmanId);
    Salary findFirstByRepairman_RepairmanIdOrderBySalaryIdDesc(Long repairmanId);
} 