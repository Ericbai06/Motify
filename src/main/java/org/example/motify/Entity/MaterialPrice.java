package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "material_prices")
public class MaterialPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    @Column(nullable = false)
    private Double unitPrice;  // 单价

    @Column(nullable = false)
    private LocalDateTime updateTime;  // 价格更新时间

    @OneToOne(mappedBy = "materialPrice")
    private Material material;  // 关联的材料
} 