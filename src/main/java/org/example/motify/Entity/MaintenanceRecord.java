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
    private Long repairManId; // 创建该维修记录的维修人员ID

    @Column(nullable = false)
    private long workHours; // 工作时长（单位：分钟）

    @Column
    private LocalDateTime startTime; // 维修项目开始时间

    @ManyToOne
    // 配置级联删除：工单删除时自动删除关联的维修记录
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_record_item", foreignKeyDefinition = "FOREIGN KEY (item_id) REFERENCES maintenance_items(item_id) ON DELETE CASCADE"))
    @JsonIgnore
    private MaintenanceItem maintenanceItem;

    @ElementCollection
    // 配置级联删除：维修记录删除时自动删除关联的材料记录
    @CollectionTable(name = "record_material", joinColumns = @JoinColumn(name = "record_id", foreignKey = @ForeignKey(name = "fk_record_material", foreignKeyDefinition = "FOREIGN KEY (record_id) REFERENCES maintenance_records(record_id) ON DELETE CASCADE")))
    @MapKeyColumn(name = "material_id")
    @Column(name = "amount")
    @JsonIgnore
    private Map<Long, Integer> materialAmounts;
}