package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
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

    @Column(nullable = false)
    private Double price;  // 材料单价
} 