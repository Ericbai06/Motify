package org.example.motify.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import org.example.motify.Enum.RepairmanType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "salaries")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "repairmen" })
public class Salary {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private RepairmanType type; // 工种/工资类型主键

    @Column(nullable = false)
    private Float hourlyRate; // 时薪

    @OneToMany(mappedBy = "salary")
    @JsonIgnore
    private List<Repairman> repairmen; // 关联的维修人员

    // 自定义toString方法避免循环引用
    @Override
    public String toString() {
        return "Salary{" +
                "type=" + type +
                ", hourlyRate=" + hourlyRate +
                '}';
    }
}
