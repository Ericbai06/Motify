package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(nullable = false)
    private String name;  // 维修项目名称

    @Column
    private String description;  // 维修项目描述

    @Column(nullable = false)
    private Double cost;  // 维修项目费用

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private MaintenanceItem maintenanceItem;

    @ManyToMany
    @JoinTable(
        name = "record_material",
        joinColumns = @JoinColumn(name = "record_id"),
        inverseJoinColumns = @JoinColumn(name = "material_id")
    )
    private List<Material> materials;
}