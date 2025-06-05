package org.example.motify.Service;

import org.example.motify.Entity.*;
import org.example.motify.Repository.*;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.AuthenticationException;
import org.example.motify.util.PasswordEncoder;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Enum.RepairmanType;
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
    @Autowired
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    @Autowired
    private final RecordMaterialRepository recordMaterialRepository;
    @Autowired
    private final SalaryRepository salaryRepository;

    // 初始化默认薪资标准
    @Transactional
    public void initializeDefaultSalaries() {
        salaryRepository.insertDefaultSalaries();
    }

    // 根据工种类型获取薪资标准
    public Salary getSalaryByType(RepairmanType type) {
        return salaryRepository.findByType(type);
    }

    // 设置维修人员的薪资标准
    @Transactional
    public Repairman setRepairmanSalary(Long repairmanId, RepairmanType type) {
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));
        
        Salary salary = salaryRepository.findByType(type);
        if (salary == null) {
            throw new ResourceNotFoundException("Salary", "type", type);
        }
        
        repairman.setType(type);
        repairman.setHourlyRate(salary.getHourlyRate());
        
        return repairmanRepository.save(repairman);
    }

    // 更新特定工种的薪资标准
    @Transactional
    public Salary updateSalaryStandard(RepairmanType type, Float hourlyRate) {
        if (hourlyRate <= 0) {
            throw new BadRequestException("时薪必须大于0");
        }
        
        String typeStr = type.name();
        boolean exists = salaryRepository.existsByType(typeStr);
        
        if (exists) {
            salaryRepository.updateSalary(typeStr, hourlyRate);
        } else {
            salaryRepository.insertSalary(typeStr, hourlyRate);
        }
        
        return salaryRepository.findByType(type);
    }

    // 添加工种和对应的薪资标准
    @Transactional
    public Salary addSalaryStandard(RepairmanType type, Float hourlyRate) {
        if (hourlyRate <= 0) {
            throw new BadRequestException("时薪必须大于0");
        }
        
        String typeStr = type.name();
        if (salaryRepository.existsByType(typeStr)) {
            throw new BadRequestException("该工种薪资标准已存在");
        }
        
        salaryRepository.insertSalary(typeStr, hourlyRate);
        return salaryRepository.findByType(type);
    }

    // 为新注册的维修人员设置默认工种和薪资
    @Transactional
    public Repairman setDefaultSalaryForNewRepairman(Repairman repairman, RepairmanType defaultType) {
        Salary salary = salaryRepository.findByType(defaultType);
        if (salary == null) {
            // 如果没有找到对应的薪资标准，初始化默认薪资标准
            initializeDefaultSalaries();
            salary = salaryRepository.findByType(defaultType);
            
            // 如果仍然没有找到，使用一个固定的默认值
            if (salary == null) {
                repairman.setHourlyRate(50.0f); // 默认时薪
                repairman.setType(defaultType);
                return repairmanRepository.save(repairman);
            }
        }
        
        repairman.setType(defaultType);
        repairman.setHourlyRate(salary.getHourlyRate());
        return repairmanRepository.save(repairman);
    }

    // 修改现有的register方法，增加设置默认薪资
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
        
        // 如果没有指定工种类型，设置为学徒
        RepairmanType type = repairman.getType();
        if (type == null) {
            type = RepairmanType.APPRENTICE;
        }
        
        // 保存基本信息
        Repairman savedRepairman = repairmanRepository.save(repairman);
        
        // 设置薪资信息
        return setDefaultSalaryForNewRepairman(savedRepairman, type);
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
        System.out.println("加密的密码: " + PasswordEncoder.encode(password));

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

    // 拒绝维修工单
    public MaintenanceItem rejectMaintenanceItem(Long repairmanId, Long itemId, String reason) {
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        MaintenanceItem item = maintenanceItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", itemId));

        // 验证工单状态，只有PENDING状态可以拒绝
        if (item.getStatus() != MaintenanceStatus.PENDING) {
            throw new BadRequestException("工单当前状态不允许拒绝");
        }

        item.setStatus(MaintenanceStatus.CANCELLED);
        item.setResult(reason); // 存储拒绝原因
        item.setUpdateTime(java.time.LocalDateTime.now());
        return maintenanceItemRepository.save(item);
    }

    // 更新维修进度
    public MaintenanceItem updateMaintenanceProgress(Long repairmanId, Long itemId, Integer progress, String description) {
        // 确认维修人员存在
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        // 确认工单存在
        MaintenanceItem item = maintenanceItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", itemId));

        // 确认工单已被接受并且属于该维修人员
        if (!item.getRepairmen().contains(repairman)) {
            throw new BadRequestException("该工单不属于此维修人员");
        }

        // 验证进度值在有效范围内
        if (progress < 0 || progress > 100) {
            throw new BadRequestException("维修进度必须在0-100之间");
        }

        // 更新进度信息
        item.setProgress(progress);
        if (description != null && !description.isEmpty()) {
            item.setDescription(item.getDescription() + "\n" + description);
        }
        item.setUpdateTime(java.time.LocalDateTime.now());
        return maintenanceItemRepository.save(item);
    }

    // 完成维修工单
    @Transactional
    public MaintenanceItem completeMaintenanceItem(Long repairmanId, Long itemId, String result, 
                                                  Double workingHours, List<Map<String, Object>> materialsUsed) {
        // 确认维修人员存在
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        // 确认工单存在
        MaintenanceItem item = maintenanceItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", itemId));

        // 确认工单已被接受并且属于该维修人员
        if (!item.getRepairmen().contains(repairman)) {
            throw new BadRequestException("该工单不属于此维修人员");
        }

        // 计算材料费用并更新库存
        double materialCost = 0.0;
        if (materialsUsed != null && !materialsUsed.isEmpty()) {
            for (Map<String, Object> materialInfo : materialsUsed) {
                Long materialId = Long.valueOf(materialInfo.get("materialId").toString());
                Integer quantity = (Integer) materialInfo.get("quantity");
                
                Material material = materialRepository.findById(materialId)
                        .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
                
                if (material.getStock() < quantity) {
                    throw new BadRequestException("材料 " + material.getName() + " 库存不足");
                }
                
                material.setStock(material.getStock() - quantity);
                materialRepository.save(material);
                
                materialCost += material.getPrice() * quantity;
            }
        }

        // 计算工时费
        Double hourlyRate = repairman.getHourlyRate();
        double laborCost = hourlyRate * workingHours;

        // 更新工单信息
        item.setStatus(MaintenanceStatus.COMPLETED);
        item.setProgress(100);
        item.setResult(result);
        item.setMaterialCost(materialCost);
        item.setLaborCost(laborCost);
        item.setCost(materialCost + laborCost);
        item.setCompleteTime(java.time.LocalDateTime.now());
        item.setUpdateTime(java.time.LocalDateTime.now());
        
        // 保存工单更新
        MaintenanceItem updatedItem = maintenanceItemRepository.save(item);
        
        // 创建维修记录
        MaintenanceRecord record = new MaintenanceRecord();
        record.setMaintenanceItem(updatedItem);
        record.setName("完成维修：" + updatedItem.getName());
        record.setDescription(result);
        record.setCost(materialCost + laborCost);
        record.setRepairManId(repairmanId);
        // 将小时转换为分钟
        record.setWorkHours(Math.round(workingHours * 60));
        MaintenanceRecord savedRecord = maintenanceRecordRepository.save(record);
        
        // 对每个材料创建记录
        if (materialsUsed != null) {
            for (Map<String, Object> materialInfo : materialsUsed) {
                Long materialId = Long.valueOf(materialInfo.get("materialId").toString());
                Integer quantity = (Integer) materialInfo.get("quantity");
                
                // 创建材料使用记录
                RecordMaterial recordMaterial = new RecordMaterial();
                recordMaterial.setRecordId(savedRecord.getRecordId());
                recordMaterial.setMaterialId(materialId);
                recordMaterial.setAmount(quantity);
                recordMaterialRepository.save(recordMaterial);
            }
        }

        return updatedItem;
    }

    // 计算收入统计
    public Map<String, Object> calculateIncome(Long repairmanId, String startDateStr, String endDateStr) {
        // 确认维修人员存在
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        java.time.LocalDateTime startDate;
        java.time.LocalDateTime endDate;
        
        if (startDateStr != null && !startDateStr.isEmpty()) {
            startDate = java.time.LocalDate.parse(startDateStr).atStartOfDay();
        } else {
            startDate = null;
        }
        if (endDateStr != null && !endDateStr.isEmpty()) {
            endDate = java.time.LocalDate.parse(endDateStr).atTime(23, 59, 59);
        } else {
            endDate = null;
        }

        List<MaintenanceItem> completedItems;
        if (startDate != null && endDate != null) {
            completedItems = repairman.getMaintenanceItems().stream()
                    .filter(item -> item.getStatus() == MaintenanceStatus.COMPLETED)
                    .filter(item -> item.getCompleteTime().isAfter(startDate) && item.getCompleteTime().isBefore(endDate))
                    .toList();
        } else {
            completedItems = repairman.getMaintenanceItems().stream()
                    .filter(item -> item.getStatus() == MaintenanceStatus.COMPLETED)
                    .toList();
        }
        
        double totalIncome = completedItems.stream()
                .mapToDouble(MaintenanceItem::getLaborCost)
                .sum();
        
        int totalWorkOrders = completedItems.size();
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("totalIncome", totalIncome);
        result.put("totalWorkOrders", totalWorkOrders);
        result.put("repairmanName", repairman.getName());
        result.put("repairmanType", repairman.getType());
        result.put("hourlyRate", repairman.getHourlyRate());
        
        return result;
    }
}
