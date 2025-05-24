package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "materials")
@Data
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId;

    @Column(nullable = false)
    private String name;  // 材料名称

    @Column(nullable = false)
    private String description;  // 材料描述

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialType type;  // 材料类型

    @Column(nullable = false)
    private Integer stockQuantity;  // 库存数量

    @Column(nullable = false)
    private Integer minimumStock;  // 最低库存

    @ManyToOne
    @JoinColumn(name = "maintenance_record_id")
    private MaintenanceRecord maintenanceRecord;  // 所属维修记录

    @OneToOne(mappedBy = "material", cascade = CascadeType.ALL)
    private MaterialPrice materialPrice;  // 材料价格信息

    @Column(nullable = false)
    private Integer quantity;  // 使用数量

    @ManyToMany(mappedBy = "materials")
    private List<MaintenanceItem> maintenanceItems;  // 关联的维修项目
} 