package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Map;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(nullable = false)
    private String name; // 维修项目名称

    @Column
    private String description; // 维修项目描述

    @Column(nullable = false)
    private Double cost; // 维修项目费用

    @Column(nullable = false)
    private Long repairManId; // 创建该维修记录的维修人员ID

    @Column(nullable = false)
    private long workHours; // 工作时长（单位：分钟）

    @Column
    private LocalDateTime startTime; // 维修项目开始时间

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_record_item", foreignKeyDefinition = "FOREIGN KEY (item_id) REFERENCES maintenance_items(item_id) ON DELETE CASCADE"))
    @JsonIgnore
    private MaintenanceItem maintenanceItem;

    @ElementCollection
    @CollectionTable(name = "record_material", joinColumns = @JoinColumn(name = "record_id"))
    @MapKeyColumn(name = "material_id") // 使用@MapKeyColumn代替@MapKeyJoinColumn
    @Column(name = "amount")
    @JsonIgnore
    private Map<Long, Integer> materialAmounts; // 改为使用材料ID而不是Material对象作为键

    // 计算材料总费用 - 需要修改实现逻辑
    public Double calculateMaterialCost() {
        if (materialAmounts == null || materialAmounts.isEmpty()) {
            return 0.0;
        }

        // 由于键现在是材料ID而不是Material对象，我们需要查询材料价格
        // 这需要注入MaterialRepository或通过其他方式获取材料价格
        // 此处我们暂时返回0，实际实现需要在Service层处理
        return 0.0;
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