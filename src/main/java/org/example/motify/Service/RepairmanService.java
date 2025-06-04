package org.example.motify.Service;

import org.example.motify.Entity.*;
import org.example.motify.Repository.*;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.AuthenticationException;
import org.example.motify.util.PasswordEncoder;
import org.example.motify.Enum.MaintenanceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class RepairmanService {
    @Autowired
    private final RepairmanRepository repairmanRepository;
    @Autowired
    private final MaintenanceItemRepository maintenanceItemRepository;
    @Autowired
    private final MaterialRepository materialRepository;
    @Autowired
    private final CarRepository carRepository;

    public Repairman register(Repairman repairman) {
        if (repairman.getUsername() == null || repairman.getUsername().trim().isEmpty()) {
            throw new BadRequestException("用户名不能为空");
        }
        if (repairman.getPassword() == null || repairman.getPassword().trim().isEmpty()) {
            throw new BadRequestException("密码不能为空");
        }
        if (repairman.getName() == null || repairman.getName().trim().isEmpty()) {
            throw new BadRequestException("姓名不能为空");
        }
        if (repairman.getGender() == null || repairman.getGender().trim().isEmpty()) {
            throw new BadRequestException("性别不能为空");
        }
        if (repairmanRepository.existsByUsername(repairman.getUsername())) {
            throw new BadRequestException("用户名已存在");
        }
        repairman.setPassword(PasswordEncoder.encode(repairman.getPassword()));
        return repairmanRepository.save(repairman);
    }

    @Transactional(readOnly = true)
    public Optional<Repairman> login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("登录失败: 用户名为空");
            throw new BadRequestException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("登录失败: 密码为空");
            throw new BadRequestException("密码不能为空");
        }

        // 先检查用户是否存在
        Optional<Repairman> repairmanOpt = repairmanRepository.findByUsername(username);
        if (repairmanOpt.isEmpty()) {
            System.out.println("登录失败: 用户名 " + username + " 不存在");
            throw new AuthenticationException("用户名或密码错误");
        }

        // 检查密码是否匹配
        Repairman repairman = repairmanOpt.get();
        String storedPassword = repairman.getPassword();
        System.out.println("找到用户: " + username + ", 开始验证密码");

        // 打印密码信息，便于调试（生产环境请移除这些敏感信息的打印）
        System.out.println("输入的密码: " + password);
        System.out.println("存储的加密密码: " + storedPassword);

        boolean passwordMatches = PasswordEncoder.matches(password, storedPassword);
        System.out.println("密码验证结果: " + (passwordMatches ? "成功" : "失败"));

        if (!passwordMatches) {
            throw new AuthenticationException("用户名或密码错误");
        }

        return Optional.of(repairman);
    }

    @Transactional(readOnly = true)
    public Optional<Repairman> getRepairmanById(Long repairmanId) {
        return repairmanRepository.findById(repairmanId)
                .or(() -> {
                    throw new ResourceNotFoundException("Repairman", "id", repairmanId);
                });
    }

    public Repairman updateRepairman(Repairman repairman) {
        if (repairman.getRepairmanId() == null) {
            throw new BadRequestException("维修人员ID不能为空");
        }
        if (!repairmanRepository.existsById(repairman.getRepairmanId())) {
            throw new ResourceNotFoundException("Repairman", "id", repairman.getRepairmanId());
        }
        return repairmanRepository.save(repairman);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceItem> getRepairmanMaintenanceItems(Long repairmanId) {
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));
        return repairman.getMaintenanceItems();
    }

    // public MaintenanceItem updateMaintenanceItem(Long recordId, MaintenanceItem record) {
    //     if (recordId == null) {
    //         throw new BadRequestException("维修记录ID不能为空");
    //     }
    //     MaintenanceItem existingRecord = maintenanceItemRepository.findById(recordId)
    //             .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", recordId));
        
    //     // 更新维修进度
    //     if (record.getProgress() != null) {
    //         if (record.getProgress() < 0 || record.getProgress() > 100) {
    //             throw new BadRequestException("维修进度必须在0-100之间");
    //         }
    //         existingRecord.setProgress(record.getProgress());
    //     }
        
    //     // 更新维修结果
    //     if (record.getResult() != null) {
    //         existingRecord.setResult(record.getResult());
    //     }
        
    //     // 更新备注
    //     if (record.getReminder() != null) {
    //         existingRecord.setReminder(record.getReminder());
    //     }

    //     // 更新材料使用情况
    //     if (record.getMaintenanceRecords() != null) {
    //         for (MaintenanceRecord maintenanceRecord : record.getMaintenanceRecords()) {
    //             if (maintenanceRecord.getMaterialAmounts() != null) {
    //                 for (Map.Entry<Material, Integer> entry : maintenanceRecord.getMaterialAmounts().entrySet()) {
    //                     Material material = entry.getKey();
    //                     Integer amount = entry.getValue();
                        
    //                     // 检查库存
    //                     if (material.getStock() < amount) {
    //                         throw new BadRequestException("材料 " + material.getName() + " 库存不足");
    //                     }
                        
    //                     // 更新库存
    //                     material.setStock(material.getStock() - amount);
    //                     materialRepository.save(material);
    //                 }
    //             }
    //         }
    //     }

    //     // 更新费用信息
    //     RecordInfo recordInfo = new RecordInfo();
    //     recordInfo.setMaintenanceItem(existingRecord);
        
    //     // 计算材料费用
    //     double materialCost = record.getMaintenanceRecords().stream()
    //             .mapToDouble(MaintenanceRecord::calculateMaterialCost)
    //             .sum();
    //     recordInfo.setMaterialCost(materialCost);

    //     // 计算工时费用
    //     double laborCost = record.getRepairmen().stream()
    //             .mapToDouble(repairman -> repairman.getHourlyRate() * record.getWorkHours())
    //             .sum();
    //     recordInfo.setLaborCost(laborCost);
        
    //     recordInfo.setUpdateTime(LocalDateTime.now());
        
    //     // 添加到记录信息列表
    //     if (existingRecord.getRecordInfos() == null) {
    //         existingRecord.setRecordInfos(new java.util.ArrayList<>());
    //     }
    //     existingRecord.getRecordInfos().add(recordInfo);
        
    //     return maintenanceItemRepository.save(existingRecord);
    // }

    public MaintenanceItem saveMaintenanceItem(MaintenanceItem record) {
        if (record.getCar() == null || record.getCar().getCarId() == null) {
            throw new BadRequestException("车辆信息不能为空");
        }
        if (record.getRepairmen() == null || record.getRepairmen().isEmpty()) {
            throw new BadRequestException("维修人员不能为空");
        }
        if (record.getProgress() < 0 || record.getProgress() > 100) {
            throw new BadRequestException("维修进度必须在0-100之间");
        }
        return maintenanceItemRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceItem> getRepairmanCurrentRecords(Long repairmanId) {
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));
        return repairman.getMaintenanceItems().stream()
                .filter(record -> record.getProgress() < 100)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaintenanceItem> getRepairmanCompletedRecords(Long repairmanId) {
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));
        return repairman.getMaintenanceItems().stream()
                .filter(record -> record.getProgress() == 100)
                .toList();
    }

    // @Transactional(readOnly = true)
    // public double calculateTotalIncome(Long repairmanId) {
    //     Repairman repairman = repairmanRepository.findById(repairmanId)
    //             .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));
    //     return repairman.getMaintenanceItems().stream()
    //             .filter(record -> record.getProgress() == 100)
    //             .mapToDouble(record -> record.getRecordInfos().stream()
    //                     .mapToDouble(RecordInfo::getTotalAmount)
    //                     .sum())
    //             .sum();
    // }

    public MaintenanceItem acceptMaintenanceItem(Long repairmanId, Long itemId) {
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        MaintenanceItem item = maintenanceItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", itemId));

        // 如果工单状态是ACCEPTED，说明已经被其他维修人员接收
        if (item.getStatus() == MaintenanceStatus.ACCEPTED) {
            throw new BadRequestException("工单已被其他维修人员接收");
        }

        // 检查其他非PENDING状态
        if (item.getStatus() != null && item.getStatus() != MaintenanceStatus.PENDING) {
            throw new BadRequestException("工单当前状态不允许接收");
        }

        if (item.getRepairmen() == null) {
            item.setRepairmen(new java.util.ArrayList<>());
        }
        item.getRepairmen().add(repairman);
        item.setStatus(MaintenanceStatus.ACCEPTED);
        item.setProgress(0);
        item.setUpdateTime(java.time.LocalDateTime.now());
        return maintenanceItemRepository.save(item);
    }

    // 以维修记录为例，统计材料费用
    public double calculateMaterialCost(MaintenanceRecord record) {
        if (record.getMaterialAmounts() == null || record.getMaterialAmounts().isEmpty()) {
            return 0.0;
        }
        return record.getMaterialAmounts().entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }
}
