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
    
    // 直接定义可设置的枚举类型字段
    @Enumerated(EnumType.STRING)
    @Column(name = "type") 
    private RepairmanType type;  // 工种类型，可写字段

    // 通过type字段与Salary关联，只读方式
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type", referencedColumnName = "type", insertable = false, updatable = false)
    private Salary salary;

    @ManyToMany(mappedBy = "repairmen")
    @JsonIgnore
    private List<MaintenanceItem> maintenanceItems;  // 维修项目

    // 获取时薪
    public Double getHourlyRate() {
        return salary != null ? salary.getHourlyRate().doubleValue() : 0.0;
    }
    
    // 此方法仅用于API兼容性，实际不修改薪资
    // 薪资应通过修改type和对应的Salary记录来变更
    public void setHourlyRate(Float hourlyRate) {
        // 这个方法不应直接修改工资，因为工资由type关联的Salary决定
        // 为了兼容API，这里提供一个空实现
    }
} 