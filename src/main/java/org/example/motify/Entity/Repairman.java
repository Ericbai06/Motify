package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.motify.Enum.RepairmanType;

@Data
@Entity
@Table(name = "repairmen")
public class Repairman {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repairmanId;

    @Column(nullable = false, unique = true)
    private String username;  // 用户名

    @Column(nullable = false)
    private String password;  // 密码

    @Column(nullable = false)
    private String name;  // 姓名

    @Column
    private String phone;  // 电话

    @Column
    private String email;  // 邮箱

    @Column(nullable = false)
    private String gender;  // 性别

    @ManyToOne
    @JoinColumn(name = "type", referencedColumnName = "type")
    private Salary salary; // 工资信息（可写入）

    @ManyToMany(mappedBy = "repairmen")
    @JsonIgnore
    private List<MaintenanceItem> maintenanceItems;  // 维修项目

    @Column(name = "type", insertable = false, updatable = false)
    private String type; // 或 private RepairmanType type;

    // 获取时薪
    public Double getHourlyRate() {
        return salary != null ? salary.getHourlyRate().doubleValue() : 0.0;
    }

    // 通过 repairman.getSalary().getType() 获取工种
} 