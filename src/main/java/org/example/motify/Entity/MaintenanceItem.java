package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "maintenance_items")
public class MaintenanceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false)
    private String name;  // 维修项目名称

    @Column
    private String description;  // 维修项目描述

    @Column(nullable = false)
    private Double cost;  // 维修项目费用

    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MaintenanceRecord maintenanceRecord;

    @ManyToMany
    @JoinTable(
        name = "maintenance_item_material",
        joinColumns = @JoinColumn(name = "item_id"),
        inverseJoinColumns = @JoinColumn(name = "material_id")
    )
    private List<Material> materials;
} 