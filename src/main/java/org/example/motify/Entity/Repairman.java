package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

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

    @Column
    private String specialty;  // 专长

    @Column(nullable = false)
    private String gender;  // 性别

    @Column(nullable = false, insertable = false, updatable = false)
    private String type;  // 工资类型

    @OneToOne
    @JoinColumn(name = "type", referencedColumnName = "type")
    private Salary salary;  // 工资信息

    @ManyToMany(mappedBy = "repairmen")
    private List<MaintenanceItem> maintenanceItems;  // 维修项目
} 