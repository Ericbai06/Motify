package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "record_infos")
@Data
public class RecordInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long infoId;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Column(nullable = false)
    private boolean isPaid;

    @Column
    private Double totalAmount;

    @OneToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MaintenanceRecord maintenanceRecord;
} 