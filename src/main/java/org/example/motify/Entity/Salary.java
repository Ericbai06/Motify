package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "salaries")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salaryId;

    @Column(nullable = false)
    private Float hourlyRate;  // 小时费率

    @Column(nullable = false, unique = true)
    private String type;  // 工资类型

    @Column(nullable = false)
    private Float hourlyWage;  // 小时工资

    @OneToOne(mappedBy = "salary")
    private Repairman repairman;  // 关联的维修人员
} 