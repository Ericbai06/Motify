package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.motify.Enum.RepairmanType;

@Entity
@Table(name = "required_repairman_types")
@Data
public class RequiredRepairmanType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private MaintenanceItem maintenanceItem;

    @Enumerated(EnumType.STRING)
    private RepairmanType type;

    private Integer required; // 需要的人数

    private Integer assigned; // 已分配的人数
}
