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
import java.util.ArrayList;

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
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    @Autowired
    private final RecordMaterialRepository recordMaterialRepository;
    @Autowired
    private final SalaryRepository salaryRepository;
    @Autowired
    private final RequiredRepairmanTypeRepository requiredTypeRepository;
    @Autowired
    private final CarRepository carRepository;

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
        // 移除可能导致空指针异常的打印语句
        if (record.getCar() == null || record.getCar().getCarId() == null) {
            throw new BadRequestException("车辆信息不能为空");
        }
        if (record.getRepairmen() == null || record.getRepairmen().isEmpty()) {
            throw new BadRequestException("维修人员不能为空");
        }
        if (record.getProgress() < 0 || record.getProgress() > 100) {
            throw new BadRequestException("维修进度必须在0-100之间");
        }

        // 设置创建时间
        if (record.getCreateTime() == null) {
            record.setCreateTime(LocalDateTime.now());
        }

        // 设置更新时间
        record.setUpdateTime(LocalDateTime.now());

        // 如果未设置状态，默认设为PENDING
        if (record.getStatus() == null) {
            record.setStatus(MaintenanceStatus.PENDING);
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

        // 保存更新后的工单
        MaintenanceItem updatedItem = maintenanceItemRepository.save(item);

        // 创建维修记录，记录开始时间
        MaintenanceRecord record = new MaintenanceRecord();
        record.setMaintenanceItem(updatedItem);
        record.setName("开始维修：" + updatedItem.getName());
        record.setDescription("维修人员" + repairman.getName() + "开始处理工单");
        record.setCost(0.0); // 初始费用为0
        record.setRepairManId(repairmanId);
        record.setWorkHours(0); // 初始工作时间为0
        record.setStartTime(LocalDateTime.now()); // 设置开始时间
        maintenanceRecordRepository.save(record);

        return updatedItem;
    }

    // 以维修记录为例，统计材料费用
    public double calculateMaterialCost(MaintenanceRecord record) {
        if (record.getMaterialAmounts() == null || record.getMaterialAmounts().isEmpty()) {
            return 0.0;
        }
        double totalCost = 0.0;
        for (Map.Entry<Long, Integer> entry : record.getMaterialAmounts().entrySet()) {
            Long materialId = entry.getKey();
            Integer quantity = entry.getValue();

            // 根据材料ID查询材料对象
            Material material = materialRepository.findById(materialId)
                    .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));

            totalCost += material.getPrice() * quantity;
        }
        return totalCost;
    }

    // 拒绝维修工单
    @Transactional
    public MaintenanceItem rejectMaintenanceItem(Long repairmanId, Long itemId, String reason) {
        // 获取维修人员
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        // 获取工单
        MaintenanceItem item = maintenanceItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", itemId));

        // 从工单中移除此维修人员
        Map<Repairman, Boolean> acceptance = item.getRepairmenAcceptance();
        if (acceptance != null) {
            acceptance.remove(repairman);
        }

        // 找出此维修人员的工种
        RepairmanType type = repairman.getType();

        // 更新对应工种的已分配数量
        for (RequiredRepairmanType requirement : item.getRequiredTypes()) {
            if (requirement.getType() == type) {
                requirement.setAssigned(requirement.getAssigned() - 1);
                requiredTypeRepository.save(requirement);
                break;
            }
        }

        // 保存工单状态
        maintenanceItemRepository.save(item);

        // 尝试分配新的维修人员
        assignNextRepairman(item, type);

        return item;
    }

    private void assignNextRepairman(MaintenanceItem item, RepairmanType type) {
        // 找出该工种还需要分配的人数
        int needed = 0;
        for (RequiredRepairmanType req : item.getRequiredTypes()) {
            if (req.getType() == type) {
                needed = req.getRequired() - req.getAssigned();
                break;
            }
        }

        if (needed <= 0)
            return;

        // 查找工作量最少的维修人员
        List<Repairman> availableRepairmen = repairmanRepository.findByTypeOrderByWorkloadAsc(type);

        // 排除已经分配给这个工单的维修人员
        List<Repairman> alreadyAssigned = new ArrayList<>(item.getRepairmenAcceptance().keySet());
        availableRepairmen.removeAll(alreadyAssigned);

        if (!availableRepairmen.isEmpty()) {
            // 分配给工作量最少的维修人员
            Repairman nextRepairman = availableRepairmen.get(0);
            item.addRepairman(nextRepairman, false);

            // 更新已分配数量
            for (RequiredRepairmanType req : item.getRequiredTypes()) {
                if (req.getType() == type) {
                    req.setAssigned(req.getAssigned() + 1);
                    requiredTypeRepository.save(req);
                    break;
                }
            }

            maintenanceItemRepository.save(item);
        }
    }

    // 更新维修进度
    public MaintenanceItem updateMaintenanceProgress(Long repairmanId, Long itemId, Integer progress,
            String description) {
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
        // 设置开始时间 - 从当前时间减去工作时长
        record.setStartTime(LocalDateTime.now());
        MaintenanceRecord savedRecord = maintenanceRecordRepository.save(record);

        // 对每个材料创建记录
        if (materialsUsed != null) {
            for (Map<String, Object> materialInfo : materialsUsed) {
                Long materialId = Long.valueOf(materialInfo.get("materialId").toString());
                Integer quantity = (Integer) materialInfo.get("quantity");

                // 创建材料使用记录
                RecordMaterial recordMaterial = new RecordMaterial();
                // 不再设置 id，使用数据库的自动递增功能
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
                    .filter(item -> item.getCompleteTime().isAfter(startDate)
                            && item.getCompleteTime().isBefore(endDate))
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

    @Transactional
    public MaintenanceRecord addMaintenanceRecord(Map<String, Object> payload) {
        Long maintenanceItemId = Long.valueOf(payload.get("maintenanceItemId").toString());
        String description = (String) payload.get("description");
        Long repairmanId = Long.valueOf(payload.get("repairmanId").toString());
        Integer workHours = Integer.valueOf(payload.get("workHours").toString());
        LocalDateTime startTime = LocalDateTime.parse(payload.get("startTime").toString());

        MaintenanceItem item = maintenanceItemRepository.findById(maintenanceItemId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", maintenanceItemId));

        MaintenanceRecord record = new MaintenanceRecord();
        record.setMaintenanceItem(item);
        record.setDescription(description);
        record.setRepairManId(repairmanId);
        record.setWorkHours(workHours);
        record.setStartTime(startTime);

        String name = payload.get("name") != null
                ? payload.get("name").toString()
                : (description != null ? description : "维修记录") + "-" + startTime.toString();
        record.setName(name);

        MaintenanceRecord savedRecord = maintenanceRecordRepository.save(record);

        // 处理材料
        List<Map<String, Object>> materials = (List<Map<String, Object>>) payload.get("materials");
        if (materials != null) {
            for (Map<String, Object> mat : materials) {
                Long materialId = Long.valueOf(mat.get("materialId").toString());
                Integer amount = Integer.valueOf(mat.get("amount").toString());
                RecordMaterial rm = new RecordMaterial();
                rm.setRecordId(savedRecord.getRecordId());
                rm.setMaterialId(materialId);
                rm.setAmount(amount);
                recordMaterialRepository.save(rm);
            }
        }
        return savedRecord;
    }

    public MaintenanceItem submitRepairRequest(Long userId, Long carId, String name, String description,
            Map<RepairmanType, Integer> requiredTypesMap) {
        // 基本验证
        if (carId == null) {
            throw new BadRequestException("车辆ID不能为空");
        }

        MaintenanceItem maintenanceItem = new MaintenanceItem();
        maintenanceItem.setName(name);
        maintenanceItem.setDescription(description);
        maintenanceItem.setCar(carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId)));
        maintenanceItem.setStatus(MaintenanceStatus.PENDING);
        maintenanceItem.setProgress(0);
        maintenanceItem.setCreateTime(LocalDateTime.now());
        maintenanceItem.setUpdateTime(LocalDateTime.now());
        maintenanceItem.setCost(0.0);

        // 保存维修工单，获取ID
        MaintenanceItem savedItem = maintenanceItemRepository.save(maintenanceItem);

        // 创建工种需求并保存
        for (Map.Entry<RepairmanType, Integer> entry : requiredTypesMap.entrySet()) {
            RequiredRepairmanType requiredType = new RequiredRepairmanType();
            requiredType.setMaintenanceItem(savedItem);
            requiredType.setType(entry.getKey());
            requiredType.setRequired(entry.getValue());
            requiredType.setAssigned(0);
            requiredTypeRepository.save(requiredType);
        }

        // 自动分配维修人员
        autoAssignRepairmen(savedItem);

        return savedItem;
    }

    private void autoAssignRepairmen(MaintenanceItem item) {
        // 先保存并刷新确保能获取到完整关联数据
        MaintenanceItem savedItem = maintenanceItemRepository.saveAndFlush(item);

        // 重新从数据库获取带有全部关联的工单
        MaintenanceItem refreshedItem = maintenanceItemRepository.findById(savedItem.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", savedItem.getItemId()));

        // 从数据库加载这个工单的所有工种需求
        List<RequiredRepairmanType> requirements = requiredTypeRepository
                .findByMaintenanceItem_ItemId(refreshedItem.getItemId());

        for (RequiredRepairmanType requirement : requirements) {
            RepairmanType type = requirement.getType();
            int needed = requirement.getRequired() - requirement.getAssigned();

            if (needed <= 0)
                continue;

            // 查询该工种维修人员并按当前工作量排序
            List<Repairman> availableRepairmen = repairmanRepository.findByTypeOrderByWorkloadAsc(type);

            // 分配需要的数量
            int assigned = 0;
            for (Repairman repairman : availableRepairmen) {
                if (assigned >= needed)
                    break;

                // 分配给此维修人员
                refreshedItem.addRepairman(repairman, false); // 初始状态为未接受
                assigned++;
            }

            // 更新已分配数量
            requirement.setAssigned(requirement.getAssigned() + assigned);
            requiredTypeRepository.save(requirement);
        }

        // 如果有变更，保存维修工单
        maintenanceItemRepository.save(refreshedItem);
    }
}
