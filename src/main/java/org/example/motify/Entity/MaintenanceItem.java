package org.example.motify.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import org.example.motify.Enum.MaintenanceStatus;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    private Double cost; // 维修总成本

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_repairman", joinColumns = @JoinColumn(name = "item_id"))
    @MapKeyJoinColumn(name = "repairman_id")
    @Column(name = "is_accepted")
    private Map<Repairman, Boolean> repairmenAcceptance;

    @OneToMany(mappedBy = "maintenanceItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MaintenanceRecord> maintenanceRecords;

    @OneToMany(mappedBy = "maintenanceItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<RequiredRepairmanType> requiredTypes = new ArrayList<>();

    /**
     * 获取所有分配到该工单的维修人员列表（排除已拒绝的）
     * 
     * @return 维修人员列表
     */
    public List<Repairman> getRepairmen() {
        if (repairmenAcceptance == null) {
            return List.of();
        }
        return repairmenAcceptance.entrySet().stream()
                .filter(entry -> !Boolean.FALSE.equals(entry.getValue())) // 排除已拒绝的维修人员
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取已接受工单的维修人员列表
     * 
     * @return 已接受的维修人员列表
     */
    public List<Repairman> getAcceptedRepairmen() {
        if (repairmenAcceptance == null) {
            return List.of();
        }
        return repairmenAcceptance.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue())) // 排除null和false值
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取待处理工单的维修人员列表（未明确接受或拒绝）
     * 
     * @return 待处理的维修人员列表
     */
    public List<Repairman> getPendingRepairmen() {
        if (repairmenAcceptance == null) {
            return List.of();
        }
        return repairmenAcceptance.entrySet().stream()
                .filter(entry -> entry.getValue() == null) // 筛选待处理的维修人员（值为null）
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取已拒绝工单的维修人员列表
     * 
     * @return 已拒绝的维修人员列表
     */
    public List<Repairman> getRejectedRepairmen() {
        if (repairmenAcceptance == null) {
            return List.of();
        }
        return repairmenAcceptance.entrySet().stream()
                .filter(entry -> Boolean.FALSE.equals(entry.getValue())) // 筛选已拒绝的维修人员
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 检查特定维修人员是否已接受工单
     * 
     * @param repairman 维修人员
     * @return 是否接受
     */
    public boolean isAcceptedBy(Repairman repairman) {
        if (repairmenAcceptance == null) {
            return false;
        }
        Boolean accepted = repairmenAcceptance.get(repairman);
        return accepted != null && accepted;
    }

    /**
     * 设置维修人员并初始化接受状态
     * 
     * @param repairmen 维修人员列表
     */
    public void setRepairmen(List<Repairman> repairmen) {
        if (repairmen == null) {
            this.repairmenAcceptance = null;
            return;
        }

        if (this.repairmenAcceptance == null) {
            this.repairmenAcceptance = new java.util.HashMap<>();
        } else {
            this.repairmenAcceptance.clear();
        }

        // 将每个维修人员添加到Map中，默认接受状态为false
        for (Repairman repairman : repairmen) {
            this.repairmenAcceptance.put(repairman, false);
        }
    }

    // /**
    // * 添加维修人员并设置接受状态
    // *
    // * @param repairman 维修人员
    // * @param accepted 是否接受
    // */
    // public void addRepairman(Repairman repairman, boolean accepted) {
    // if (this.repairmenAcceptance == null) {
    // this.repairmenAcceptance = new java.util.HashMap<>();
    // }
    // this.repairmenAcceptance.put(repairman, accepted);
    // }

    public List<RequiredRepairmanType> getRequiredTypes() {
        if (requiredTypes == null) {
            requiredTypes = new ArrayList<>();
        }
        return requiredTypes;
    }

    public void setRequiredTypes(List<RequiredRepairmanType> requiredTypes) {
        this.requiredTypes = requiredTypes;
    }
}
