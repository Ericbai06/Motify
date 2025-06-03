package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.Map;

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

    @ElementCollection
    @CollectionTable(
        name = "record_material",
        joinColumns = @JoinColumn(name = "record_id")
    )
    @MapKeyJoinColumn(name = "material_id")
    @Column(name = "amount")
    private Map<Material, Integer> materialAmounts;  // 材料及其使用数量

    // 计算材料总费用
    public Double calculateMaterialCost() {
        if (materialAmounts == null || materialAmounts.isEmpty()) {
            return 0.0;
        }
        return materialAmounts.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    // 更新总费用
    @PrePersist
    @PreUpdate
    public void updateCost() {
        this.cost = calculateMaterialCost();
    }
}