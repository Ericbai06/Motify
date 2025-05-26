package org.example.motify.Repository;

import org.example.motify.Entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    // 只保留基础结构，无自定义方法
} 