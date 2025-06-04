package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import org.example.motify.Enum.MaintenanceStatus;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "maintenance_items")
public class MaintenanceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false)
    private String name; // 维修项目名称

    @Column(nullable = false)
    private String description; // 维修描述

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status; // 维修状态

    @Column
    private Integer progress; // 维修进度

    @Column
    private String result; // 维修结果

    @Column
    private String reminder; // 催单信息

    @Column
    private Integer score; // 用户评分

    // @Column
    // private Double workHours; // 工作时长

    @Column(nullable = false)
    private LocalDateTime createTime; // 创建时间

    @Column
    private LocalDateTime updateTime; // 更新时间

    @Column
    private LocalDateTime completeTime; // 完成时间

    @Column
    private Double materialCost; // 材料费用

    @Column
    private Double laborCost; // 工时费用

    @Column(nullable = false)
    private Double cost
    ; // 维修总成本

    
    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    @JsonIgnore
    private Car car;

    @ManyToMany
    @JoinTable(name = "item_repairman", joinColumns = @JoinColumn(name = "item_id"), inverseJoinColumns = @JoinColumn(name = "repairman_id"))
    @JsonIgnore
    private List<Repairman> repairmen;

    @OneToMany(mappedBy = "maintenanceItem", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MaintenanceRecord> maintenanceRecords;
}
