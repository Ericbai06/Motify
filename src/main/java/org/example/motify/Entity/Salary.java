package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "salaries")
@Data
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salaryId;

    @Column(nullable = false)
    private Float hourlyWage;

    @Column(nullable = false, unique = true)
    private String type;

    @OneToOne(mappedBy = "salary")
    private Repairman repairman;
} 