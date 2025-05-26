package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import org.example.motify.Enum.MaterialType;

@Entity
@Table(name = "materials")
@Data
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long materialId;

    @Column(nullable = false)
    private String name;  // 材料名称

    @Column
    private String description;  // 材料描述

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MaterialType type;  // 材料类型

    @Column
    private Integer stock;  // 库存数量

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "price_id")
    private MaterialPrice materialPrice;  // 材料价格信息

    @ManyToOne
    @JoinColumn(name = "item_id")
    private MaintenanceItem maintenanceItem;

    @ManyToMany(mappedBy = "materials")
    private List<MaintenanceRecord> maintenanceRecords;
} 