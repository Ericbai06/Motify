package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "record_material")  // 映射到已存在的表
public class RecordMaterial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "record_id", nullable = false)
    private Long recordId;
    
    @Column(name = "material_id", nullable = false)
    private Long materialId;
    
    @Column(nullable = false)
    private Integer amount;
}
