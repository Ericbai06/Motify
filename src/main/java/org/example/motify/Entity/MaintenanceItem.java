package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.Map;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.trigger.MaintenanceItemTrigger;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "maintenance_items")
@EntityListeners(MaintenanceItemTrigger.class)
public class MaintenanceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false)
    private String name;  // 维修项目名称

    @Column(nullable = false)
    private String description;  // 维修描述

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;  // 维修状态

    @Column
    private Integer progress;  // 维修进度

    @Column
    private String result;  // 维修结果

    @Column
    private String reminder;  // 提醒信息

    @Column
    private Integer score;  // 用户评分

    @Column(nullable = false)
    private Double cost;  // 维修总成本

    @Column
    private Double workHours;  // 工作时长

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @OneToMany(mappedBy = "maintenanceItem", cascade = CascadeType.ALL)
    private List<RecordInfo> recordInfos;

    @ManyToMany
    @JoinTable(
        name = "item_repairman",
        joinColumns = @JoinColumn(name = "item_id"),
        inverseJoinColumns = @JoinColumn(name = "repairman_id")
    )
    private List<Repairman> repairmen;

    @OneToMany(mappedBy = "maintenanceItem", cascade = CascadeType.ALL)
    private List<MaintenanceRecord> maintenanceRecords;
}
