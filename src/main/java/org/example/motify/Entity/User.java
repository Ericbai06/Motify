package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

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
    private String address;  // 地址

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore  // 防止JSON序列化时的循环引用
    private List<Car> cars;  // 用户的车辆
}