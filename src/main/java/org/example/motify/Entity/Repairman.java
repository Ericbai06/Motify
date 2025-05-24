package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "repairmen")
@Data
public class Repairman {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repairmanId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String specialty;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, insertable = false, updatable = false)
    private String type;

    @OneToOne
    @JoinColumn(name = "type", referencedColumnName = "type")
    private Salary salary;

    @ManyToMany(mappedBy = "repairman")
    private List<MaintenanceRecord> maintenanceRecords;
} 