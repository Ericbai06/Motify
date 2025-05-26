package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "record_infos")
public class RecordInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long infoId;

    @Column(nullable = false)
    private LocalDateTime createTime;  // 创建时间

    @Column
    private LocalDateTime updateTime;  // 更新时间

    @Column
    private LocalDateTime completeTime;  // 完成时间

    @Column(nullable = false)
    private Double totalAmount;  // 总金额

    @OneToOne
    @JoinColumn(name = "item_id")
    private MaintenanceItem maintenanceItem;
} 