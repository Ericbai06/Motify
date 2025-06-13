package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "user_history")
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String operation; // UPDATE/DELETE
    private String operator; // 操作人
    private LocalDateTime operationTime;

}

