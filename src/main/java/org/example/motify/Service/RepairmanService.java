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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class RepairmanService {
    private static final Logger logger = LoggerFactory.getLogger(RepairmanService.class);

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
    @Autowired
    private final RepairmanHistoryRepository repairmanHistoryRepository;
    @Autowired
    private final MaterialService materialService;

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
        Repairman old = repairmanRepository.findById(repairman.getRepairmanId())
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairman.getRepairmanId()));
        // 保存历史记录
        saveRepairmanHistory(old, "UPDATE", "system");
        // 修复：如果新对象的password为null或空，则保留原有密码
        if (repairman.getPassword() == null || repairman.getPassword().isEmpty()) {
            repairman.setPassword(old.getPassword());
        }
        return repairmanRepository.save(repairman);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceItem> getRepairmanMaintenanceItems(Long repairmanId) {
        List<MaintenanceItem> allItems = maintenanceItemRepository.findByRepairmanId(repairmanId);
        return allItems.stream()
                .filter(item -> {
                    Map<Repairman, Boolean> acceptance = item.getRepairmenAcceptance();
                    if (acceptance == null)
                        return false;
                    // acceptance 里没有 repairmanId=72，说明 is_accepted=null，应返回
                    if (acceptance.keySet().stream().noneMatch(r -> r.getRepairmanId().equals(repairmanId))) {
                        return true;
                    }
                    // 否则，只有 value==null 或 true 时返回
                    for (Map.Entry<Repairman, Boolean> entry : acceptance.entrySet()) {
                        if (entry.getKey().getRepairmanId().equals(repairmanId)) {
                            return entry.getValue() == null || Boolean.TRUE.equals(entry.getValue());
                        }
                    }
                    return false;
                })
                .toList();
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

        // 获取所有工单
        List<MaintenanceItem> allItems = repairman.getMaintenanceItems();

        // 过滤掉已经被该维修人员拒绝的工单，并且只返回未完成的工单
        return allItems.stream()
                .filter(item -> {
                    // 检查维修人员是否在工单的repairmenAcceptance中
                    Map<Repairman, Boolean> acceptance = item.getRepairmenAcceptance();
                    return acceptance != null && acceptance.containsKey(repairman);
                })
                .filter(record -> record.getProgress() < 100)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRepairmanCompletedRecords(Long repairmanId) {
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        List<MaintenanceItem> allItems = repairman.getMaintenanceItems();

        // 过滤获取已完成工单
        List<MaintenanceItem> completedItems = allItems.stream()
                .filter(item -> {
                    Map<Repairman, Boolean> acceptance = item.getRepairmenAcceptance();
                    return acceptance != null && acceptance.containsKey(repairman);
                })
                .filter(item -> item.getStatus() == MaintenanceStatus.COMPLETED)
                .toList();

        List<Map<String, Object>> result = new ArrayList<>();
        for (MaintenanceItem item : completedItems) {
            List<MaintenanceRecord> records = maintenanceRecordRepository
                    .findByMaintenanceItem_ItemIdAndRepairManId(item.getItemId(), repairmanId);
            double personalLaborCost = records.stream()
                    .mapToDouble(r -> r.getLaborCost() != null ? r.getLaborCost() : 0)
                    .sum();
            Map<String, Object> map = new HashMap<>();
            map.put("item", item);
            map.put("personalLaborCost", personalLaborCost);
            result.add(map);
        }
        return result;
    }

    /**
     * 获取维修人员已拒绝的维修工单
     * 
     * @param repairmanId 维修人员ID
     * @return 已拒绝的维修工单列表
     */
    @Transactional(readOnly = true)
    public List<MaintenanceItem> getRepairmanRejectedItems(Long repairmanId) {
        // 直接用数据库查询获取与该维修人员相关的所有工单（包括已拒绝的）
        List<MaintenanceItem> allItems = maintenanceItemRepository.findByRepairmanId(repairmanId);
        // 只保留 is_accepted 为 false 的工单
        return allItems.stream()
                .filter(item -> {
                    Map<Repairman, Boolean> acceptance = item.getRepairmenAcceptance();
                    if (acceptance == null)
                        return false;
                    for (Map.Entry<Repairman, Boolean> entry : acceptance.entrySet()) {
                        if (entry.getKey().getRepairmanId().equals(repairmanId)) {
                            return Boolean.FALSE.equals(entry.getValue());
                        }
                    }
                    return false;
                })
                .toList();
    }

    public MaintenanceItem acceptMaintenanceItem(Long repairmanId, Long itemId) {
        // 检查维修人员是否存在
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        // 使用新的数据库查询方法获取MaintenanceItem及其关联数据
        MaintenanceItem item = maintenanceItemRepository.findItemByIdWithDetails(itemId);
        if (item == null) {
            throw new ResourceNotFoundException("MaintenanceItem", "id", itemId);
        }

        // 检查 item-repairman 表中的 is_accepted 值
        Boolean isAccepted = maintenanceItemRepository.checkRepairmanAcceptance(itemId, repairmanId);
        if (isAccepted != null) {
            if (isAccepted) {
                throw new BadRequestException("您已经接受过该工单");
            } else {
                throw new BadRequestException("您已经拒绝过该工单，无法再次接受");
            }
        }

        // 如果工单状态是ACCEPTED，说明已经被其他维修人员接收
        if (maintenanceItemRepository.countByItemIdAndStatusAccepted(itemId) > 0) {
            throw new BadRequestException("工单已被其他维修人员接收");
        }

        // 检查其他非PENDING状态
        String status = maintenanceItemRepository.getItemStatus(itemId);
        if (status != null && !status.equals("PENDING")) {
            throw new BadRequestException("工单当前状态不允许接收");
        }

        if (item.getRepairmen() == null) {
            item.setRepairmen(new java.util.ArrayList<>());
        } else if (item.getRepairmen().contains(repairman)) {
            throw new BadRequestException("工单已被该维修人员接收");
        }
        item.getRepairmen().add(repairman);

        // 更新对应工种的已分配数量
        RepairmanType type = repairman.getType();
        for (RequiredRepairmanType requirement : item.getRequiredTypes()) {
            if (requirement.getType() == type) {
                requirement.setAssigned(requirement.getAssigned() + 1);
                requiredTypeRepository.save(requirement);
                break;
            }
        }

        // 检查所有工种是否都分配完成
        boolean allAssigned = true;
        for (RequiredRepairmanType requirement : item.getRequiredTypes()) {
            if (requirement.getAssigned() < requirement.getRequired()) {
                allAssigned = false;
                break;
            }
        }
        if (allAssigned) {
            item.setStatus(MaintenanceStatus.ACCEPTED);
            item.setProgress(0);
            item.setUpdateTime(java.time.LocalDateTime.now());
        }

        // 关键：这里要加上
        maintenanceItemRepository.acceptRepairman(itemId, repairmanId);

        // 保存更新后的工单
        MaintenanceItem updatedItem = maintenanceItemRepository.save(item);
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
        // 检查 item-repairman 表中的 is_accepted 值
        Boolean isAccepted = maintenanceItemRepository.checkRepairmanAcceptance(itemId, repairmanId);
        if (isAccepted != null) {
            if (isAccepted) {
                throw new BadRequestException("您已经接受过该工单，无法拒绝");
            } else {
                throw new BadRequestException("您已经拒绝过该工单");
            }
        }

        // 直接调用 repository 层的原生 SQL 方法，将 is_accepted 设为 false
        maintenanceItemRepository.rejectRepairman(itemId, repairmanId);

        // 使用新的数据库查询方法获取MaintenanceItem
        MaintenanceItem item = maintenanceItemRepository.findItemByIdWithDetails(itemId);
        if (item == null) {
            throw new ResourceNotFoundException("MaintenanceItem", "id", itemId);
        }

        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        assignNextRepairman(item, repairman.getType());
        maintenanceItemRepository.save(item);

        // 返回最新工单信息
        return maintenanceItemRepository.findItemByIdWithDetails(itemId);
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
        List<Long> assignedIds = maintenanceItemRepository.findAllAssignedRepairmanIds(item.getItemId());
        availableRepairmen.removeIf(r -> assignedIds.contains(r.getRepairmanId()));

        if (availableRepairmen.isEmpty()) {
            throw new BadRequestException("没有足够的维修人员可分配");
        }

        // 分配给工作量最少的维修人员
        Repairman nextRepairman = availableRepairmen.get(0);
        maintenanceItemRepository.assignRepairman(item.getItemId(), nextRepairman.getRepairmanId());
        // 注意：不再在这里增加 assigned，assigned 只在 accept 时增加
        maintenanceItemRepository.save(item);
    }

    // 更新维修进度
    public MaintenanceItem updateMaintenanceProgress(Long repairmanId, Long itemId, Integer progress,
            String description) {
        // 确认维修人员存在
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        // 使用新的数据库查询方法获取MaintenanceItem
        MaintenanceItem item = maintenanceItemRepository.findItemByIdWithDetails(itemId);
        if (item == null) {
            throw new ResourceNotFoundException("MaintenanceItem", "id", itemId);
        }

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

        // 使用新的数据库查询方法获取MaintenanceItem
        MaintenanceItem item = maintenanceItemRepository.findItemByIdWithDetails(itemId);
        if (item == null) {
            throw new ResourceNotFoundException("MaintenanceItem", "id", itemId);
        }

        // 确认工单已被接受并且属于该维修人员
        if (!item.getRepairmen().contains(repairman)) {
            throw new BadRequestException("该工单不属于此维修人员");
        }

        // 自动生成维修记录
        LocalDateTime endTime = LocalDateTime.now();
        long workMinutes = workingHours.longValue(); // 直接用前端传来的分钟数
        LocalDateTime startTime = endTime.minusMinutes(workMinutes);

        // 组装payload
        Map<String, Object> recordPayload = new java.util.HashMap<>();
        recordPayload.put("maintenanceItemId", itemId);
        recordPayload.put("description", result); // 这里用result作为维修记录描述
        recordPayload.put("repairmanId", repairmanId);
        recordPayload.put("workHours", workMinutes); // 单位：分钟
        recordPayload.put("startTime", startTime.toString());
        recordPayload.put("name", "维修完成-" + result + "-" + endTime.toString());
        recordPayload.put("materials", materialsUsed);
        this.addMaintenanceRecord(recordPayload);

        // 只设置状态和其他必要信息
        item.setStatus(MaintenanceStatus.COMPLETED);
        item.setProgress(100);
        item.setResult(result);
        item.setCompleteTime(endTime);
        item.setUpdateTime(endTime);

        return maintenanceItemRepository.save(item);
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

        // 修复工作时长解析 - 先解析为Double再转换为Long
        Object workHoursObj = payload.get("workHours");
        Long workHours;
        if (workHoursObj instanceof String) {
            workHours = Math.round(Double.parseDouble(workHoursObj.toString()));
        } else if (workHoursObj instanceof Number) {
            workHours = Math.round(((Number) workHoursObj).doubleValue());
        } else {
            throw new BadRequestException("工作时长格式不正确");
        }

        // 直接用LocalDateTime对象
        LocalDateTime startTime;
        Object startTimeObj = payload.get("startTime");
        if (startTimeObj instanceof LocalDateTime) {
            startTime = (LocalDateTime) startTimeObj;
        } else if (startTimeObj instanceof String) {
            // 兼容旧用法
            String startTimeStr = (String) startTimeObj;
            if (startTimeStr.endsWith("Z")) {
                String localTimeStr = startTimeStr.substring(0, startTimeStr.length() - 1);
                startTime = LocalDateTime.parse(localTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else if (startTimeStr.contains("T")) {
                startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else {
                startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } else {
            throw new BadRequestException("开始时间格式不正确");
        }

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

        // 使用MaterialService处理材料并减少库存
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> materials = (List<Map<String, Object>>) payload.get("materials");
        if (materials != null && !materials.isEmpty()) {
            materialService.useMaterials(materials, savedRecord.getRecordId());
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
        // 当有工种需求时，设置状态为AWAITING_ASSIGNMENT等待分配维修工
        maintenanceItem.setStatus(MaintenanceStatus.AWAITING_ASSIGNMENT);
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

    public void autoAssignRepairmen(MaintenanceItem item) {
        // // 先保存并刷新确保能获取到完整关联数据
        // MaintenanceItem savedItem = maintenanceItemRepository.saveAndFlush(item);

        // 重新从数据库获取带有全部关联的工单
        MaintenanceItem refreshedItem = maintenanceItemRepository.findById(item.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", item.getItemId()));

        // 从数据库加载这个工单的所有工种需求
        List<RequiredRepairmanType> requirements = requiredTypeRepository
                .findByMaintenanceItem_ItemId(refreshedItem.getItemId());

        for (RequiredRepairmanType requirement : requirements) {
            RepairmanType type = requirement.getType();
            int needed = requirement.getRequired();

            if (needed <= 0)
                continue;

            // 查询该工种维修人员并按当前工作量排序
            List<Repairman> availableRepairmen = repairmanRepository.findByTypeOrderByWorkloadAsc(type);
            logger.info(
                    "availableRepairmen: " + "type: " + type + " availableRepairmen: " + availableRepairmen.toString());

            // 分配需要的数量
            int assigned = 0;
            for (Repairman repairman : availableRepairmen) {
                if (assigned >= needed)
                    break;

                // 分配给此维修人员
                // refreshedItem.addRepairman(repairman, false); // 初始状态为未接受
                logger.info("assignRepairman: " + refreshedItem.getItemId() + " " + repairman.getRepairmanId());
                logger.info("repairman: " + repairman.toString());
                maintenanceItemRepository.assignRepairman(refreshedItem.getItemId(), repairman.getRepairmanId());
                assigned++;
            }
            if (assigned < needed) {
                throw new BadRequestException("没有足够的维修人员可分配");
            }

            // 不在这里更新已分配数量，而是在维修人员接受工单时更新
        }

        // 如果有变更，保存维修工单
        maintenanceItemRepository.save(refreshedItem);
    }

    private void saveRepairmanHistory(Repairman repairman, String operation, String operator) {
        RepairmanHistory history = new RepairmanHistory();
        history.setRepairmanId(repairman.getRepairmanId());
        history.setUsername(repairman.getUsername());
        history.setName(repairman.getName());
        history.setPassword(repairman.getPassword());
        history.setPhone(repairman.getPhone());
        history.setEmail(repairman.getEmail());
        history.setType(repairman.getType() != null ? repairman.getType() : null);
        history.setOperation(operation);
        history.setOperator(operator);
        history.setOperationTime(LocalDateTime.now());
        repairmanHistoryRepository.save(history);
    }

    // 回滚维修人员信息到历史版本
    public Repairman rollbackRepairmanToHistory(Long repairmanId, Long historyId) {
        RepairmanHistory history = repairmanHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("RepairmanHistory", "id", historyId));
        if (!history.getRepairmanId().equals(repairmanId)) {
            throw new BadRequestException("历史记录与维修人员ID不匹配");
        }
        Repairman repairman = repairmanRepository.findById(repairmanId)
                .orElseThrow(() -> new ResourceNotFoundException("Repairman", "id", repairmanId));

        // 注意：为了避免无限撤销的问题，我们不在撤销操作中保存新的历史记录
        // 如果需要记录撤销操作，应该在调用此方法的地方单独处理

        // 回滚字段
        repairman.setUsername(history.getUsername());
        repairman.setName(history.getName());
        repairman.setPhone(history.getPhone());
        repairman.setEmail(history.getEmail());
        repairman.setPassword(history.getPassword() != null ? history.getPassword() : repairman.getPassword());
        if (history.getType() != null) {
            repairman.setType(history.getType());
        }
        return repairmanRepository.save(repairman);
    }

    /**
     * 连续撤销：回滚到上一个历史版本
     */
    public Repairman undoRepairmanHistory(Long repairmanId) {
        List<RepairmanHistory> histories = repairmanHistoryRepository
                .findByRepairmanIdOrderByOperationTimeDesc(repairmanId);
        if (histories.size() < 2) {
            throw new BadRequestException("没有可撤销的历史记录");
        }
        // 获取上一个历史记录（第二新的记录）
        RepairmanHistory prev = histories.get(1);
        return rollbackRepairmanToHistory(repairmanId, prev.getId());
    }

    /**
     * 连续重做：回滚到下一个历史版本
     */
    public Repairman redoRepairmanHistory(Long repairmanId) {
        List<RepairmanHistory> histories = repairmanHistoryRepository
                .findByRepairmanIdOrderByOperationTimeDesc(repairmanId);
        if (histories.isEmpty()) {
            throw new BadRequestException("没有历史记录");
        }
        RepairmanHistory current = histories.get(0);
        RepairmanHistory next = repairmanHistoryRepository
                .findTop1ByRepairmanIdAndOperationTimeGreaterThanOrderByOperationTimeAsc(repairmanId,
                        current.getOperationTime());
        if (next == null) {
            throw new BadRequestException("没有可重做的历史记录");
        }
        return rollbackRepairmanToHistory(repairmanId, next.getId());
    }

    /**
     * 获取维修人员历史撤销/重做能力
     * 
     * @param repairmanId 维修人员ID
     * @return Map 包含 canUndo 和 canRedo 字段
     */
    public Map<String, Boolean> getUndoRedoStatus(Long repairmanId) {
        List<RepairmanHistory> histories = repairmanHistoryRepository
                .findByRepairmanIdOrderByOperationTimeDesc(repairmanId);
        boolean canUndo = histories.size() > 1;
        boolean canRedo = false;
        if (!histories.isEmpty()) {
            RepairmanHistory current = histories.get(0);
            RepairmanHistory next = repairmanHistoryRepository
                    .findTop1ByRepairmanIdAndOperationTimeGreaterThanOrderByOperationTimeAsc(repairmanId,
                            current.getOperationTime());
            canRedo = next != null;
        }
        Map<String, Boolean> result = new java.util.HashMap<>();
        result.put("canUndo", canUndo);
        result.put("canRedo", canRedo);
        return result;
    }

    /**
     * 为指定维修工单分配维修人员
     * 该方法允许其他服务调用，用于自动分配维修人员
     * 
     * @param item 需要分配维修人员的工单
     * @return 更新后的维修工单
     */
    @Transactional
    public MaintenanceItem assignRepairmenToItem(MaintenanceItem item) {
        // 调用私有方法完成实际分配
        autoAssignRepairmen(item);
        return item;
    }

    /**
     * 删除维修记录并恢复材料库存
     * 
     * @param recordId 维修记录ID
     * @throws ResourceNotFoundException 当维修记录不存在时
     */
    @Transactional
    public void deleteMaintenanceRecordAndRestoreStock(Long recordId) {
        // 检查维修记录是否存在
        MaintenanceRecord record = maintenanceRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRecord", "id", recordId));

        // 使用MaterialService恢复材料库存
        materialService.restoreMaterialStock(recordId);

        // 删除材料使用记录
        recordMaterialRepository.deleteByRecordId(recordId);

        // 删除维修记录
        maintenanceRecordRepository.delete(record);
    }

    /**
     * 取消材料使用并恢复库存（用于维修记录修改等场景）
     * 
     * @param recordId 维修记录ID
     */
    @Transactional
    public void restoreMaterialStock(Long recordId) {
        materialService.restoreMaterialStock(recordId);
    }
}
