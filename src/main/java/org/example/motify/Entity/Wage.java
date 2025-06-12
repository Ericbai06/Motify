package org.example.motify.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "wages")
@Data
public class Wage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repairman_id", nullable = false)
    private Repairman repairman;

    @Column(name = "repairman_id", insertable = false, updatable = false)
    private Long repairmanId;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Double totalWorkHours;

    @Column(nullable = false)
    private Double totalIncome;

    @Column(nullable = false)
    private LocalDateTime settlementDate;

    // 可选：维修人员信息快照
    private String repairmanName;
    private String repairmanType;
    private Double hourlyRate;

    public Long getRepairmanId() {
        if (repairman != null) {
            return repairman.getRepairmanId();
        }
        return this.repairmanId;
    }
}
