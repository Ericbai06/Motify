package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import org.example.motify.Enum.MaintenanceStatus;

@Data
@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(nullable = false)
    private String description;  // 维修描述

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;  // 维修状态

    @Column(nullable = false)
    private Integer progress;  // 维修进度

    private Integer score;  // 用户评分

    private String reminder;  // 提醒信息

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @OneToOne(mappedBy = "maintenanceRecord", cascade = CascadeType.ALL)
    private RecordInfo recordInfo;

    @ManyToMany
    @JoinTable(
        name = "record_repairman",
        joinColumns = @JoinColumn(name = "record_id"),
        inverseJoinColumns = @JoinColumn(name = "repairman_id")
    )
    private List<Repairman> repairman;

    @OneToMany(mappedBy = "maintenanceRecord", cascade = CascadeType.ALL)
    private List<Material> materials;

    @Column
    private String result;  // 维修结果
}
