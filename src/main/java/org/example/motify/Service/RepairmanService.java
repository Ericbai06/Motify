package org.example.motify.Service;

import org.example.motify.Entity.*;
import org.example.motify.Repository.*;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RepairmanService {
    @Autowired
    private RepairmanRepository repairmanRepository;
    
    @Autowired
    private MaintenanceItemRepository MaintenanceItemRepository;

    private final MaterialRepository materialRepository;
    private final PasswordEncoder passwordEncoder;
    private final CarRepository carRepository;

    @Autowired
    public RepairmanService(MaterialRepository materialRepository,
                          PasswordEncoder passwordEncoder,
                          CarRepository carRepository) {
        this.materialRepository = materialRepository;
        this.passwordEncoder = passwordEncoder;
        this.carRepository = carRepository;
    }

    public Repairman register(Repairman repairman) {
        if (repairman.getUsername() == null || repairman.getUsername().trim().isEmpty()) {
            throw new BadRequestException("用户名不能为空");
        }
        if (repairman.getPassword() == null || repairman.getPassword().trim().isEmpty()) {
            throw new BadRequestException("密码不能为空");
        }
        if (repairmanRepository.existsByUsername(repairman.getUsername())) {
            throw new BadRequestException("用户名已存在");
        }
        repairman.setPassword(passwordEncoder.encode(repairman.getPassword()));
        return repairmanRepository.save(repairman);
    }

    @Transactional(readOnly = true)
    public Optional<Repairman> login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("密码不能为空");
        }
        return repairmanRepository.findByUsername(username)
                .filter(repairman -> passwordEncoder.matches(password, repairman.getPassword()))
                .or(() -> {
                    throw new AuthenticationException("用户名或密码错误");
                });
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

    public MaintenanceItem updateMaintenanceItem(Long recordId, MaintenanceItem record) {
        if (recordId == null) {
            throw new BadRequestException("维修记录ID不能为空");
        }
        MaintenanceItem existingRecord = MaintenanceItemRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", recordId));
        
        // 更新维修进度
        if (record.getProgress() != null) {
            if (record.getProgress() < 0 || record.getProgress() > 100) {
                throw new BadRequestException("维修进度必须在0-100之间");
            }
            existingRecord.setProgress(record.getProgress());
        }
        
        // 更新维修结果
        if (record.getResult() != null) {
            existingRecord.setResult(record.getResult());
        }
        
        // 更新备注
        if (record.getReminder() != null) {
            existingRecord.setReminder(record.getReminder());
        }
        
        return MaintenanceItemRepository.save(existingRecord);
    }

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
        return MaintenanceItemRepository.save(record);
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

    @Transactional(readOnly = true)
    public double calculateTotalIncome(Long repairmanId) {
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));
        return repairman.getMaintenanceItems().stream()
                .filter(record -> record.getProgress() == 100)
                .mapToDouble(record -> record.getRecordInfo().getTotalAmount())
                .sum();
    }

    public MaintenanceItem acceptMaintenanceItem(Long repairmanId, Long recordId) {
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new RuntimeException("维修人员不存在"));
        
        MaintenanceItem record = MaintenanceItemRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("维修记录不存在"));
        
        // 更新维修记录状态
        record.setProgress(10); // 开始维修，设置初始进度
        record.getRepairmen().add(repairman);
        
        // 更新记录信息
        if (record.getRecordInfo() == null) {
            RecordInfo recordInfo = new RecordInfo();
            recordInfo.setMaintenanceItem(record);
            record.setRecordInfo(recordInfo);
        }
        record.getRecordInfo().setUpdateTime(java.time.LocalDateTime.now());
        
        return MaintenanceItemRepository.save(record);
    }
} 