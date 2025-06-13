package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.motify.Enum.RepairmanType;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "repairman_history")
public class RepairmanHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long repairmanId;
    private String username;
    private String name;
    private String password;
    private String phone;
    private String email;
    private RepairmanType type;
    private String operation; // UPDATE/DELETE
    private String operator; // 操作人
    private LocalDateTime operationTime;

    // getter/setter 省略
}

