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

    @Column(nullable = false)
    private Long repairManId;  // 创建该维修记录的维修人员ID

    @Column(nullable = false)
    private long workHours;  // 工作时长（单位：小时）



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

    // 计算工时费用
    public Double calculateLaborCost() {
        // 需要通过维修人员ID获取时薪，这里暂时返回0
        // 实际实现需要依赖Service层或Repository层
        return 0.0;
    }

    // 更新总费用
    @PrePersist
    @PreUpdate
    public void updateCost() {
        Double materialCost = calculateMaterialCost();
        Double laborCost = calculateLaborCost();
        this.cost = materialCost + laborCost;
    }
}