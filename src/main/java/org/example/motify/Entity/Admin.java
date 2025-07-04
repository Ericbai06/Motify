package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Data
public class Admin {
    // Getters and Setters
    @Id //主键标识
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime lastLoginTime;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;  

    // 构造函数
    public Admin() {}

    public boolean isActive() {
        return active != null && active;
    }

}