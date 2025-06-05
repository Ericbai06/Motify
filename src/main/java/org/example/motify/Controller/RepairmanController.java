package org.example.motify.Controller;

import org.example.motify.Entity.*;
import org.example.motify.Service.RepairmanService;
import org.example.motify.Exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping({"/api/repairman", "/api/repairmen"})
public class RepairmanController {
    @Autowired
    private RepairmanService repairmanService;

    // 注册维修人员
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Repairman repairman) {
        System.out.println("收到注册请求: " + repairman);
        Repairman registeredRepairman = repairmanService.register(repairman);
        System.out.println("注册成功: " + registeredRepairman);
        return ExceptionLogger.createSuccessResponse(registeredRepairman);
    }

    // 维修人员登录
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        Optional<Repairman> repairman = repairmanService.login(username, password);
        return repairman.map(ExceptionLogger::createSuccessResponse)
                .orElseGet(() -> ExceptionLogger.createNotFoundResponse("Repairman", username));
    }

    // 获取维修人员信息
    @PostMapping("/info")
    public ResponseEntity<?> getRepairmanInfo(@RequestBody Map<String, Long> payload) {
        Long repairmanId = payload.get("repairmanId");
        Optional<Repairman> repairman = repairmanService.getRepairmanById(repairmanId);
        return repairman.map(ExceptionLogger::createSuccessResponse)
                .orElseGet(() -> ExceptionLogger.createNotFoundResponse("Repairman", repairmanId));
    }

    // 更新维修人员信息
    @PostMapping("/update")
    public ResponseEntity<?> updateRepairman(@RequestBody Repairman repairman) {
        Repairman updatedRepairman = repairmanService.updateRepairman(repairman);
        return ExceptionLogger.createSuccessResponse(updatedRepairman);
    }

    // 获取维修人员的维修项目列表
    @PostMapping("/maintenance-items/list")
    public ResponseEntity<?> getMaintenanceItems(@RequestBody Map<String, Long> payload) {
        Long repairmanId = payload.get("repairmanId");
        List<MaintenanceItem> items = repairmanService.getRepairmanMaintenanceItems(repairmanId);
        return ExceptionLogger.createSuccessResponse(items);
    }

    // 更新维修工单进度
    @PutMapping("/{repairmanId}/maintenance-items/{itemId}/progress")
    public ResponseEntity<?> updateMaintenanceProgress(
            @PathVariable Long repairmanId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> payload) {
        Integer progress = (Integer) payload.get("progress");
        String description = (String) payload.get("description");
        MaintenanceItem item = repairmanService.updateMaintenanceProgress(repairmanId, itemId, progress, description);
        return ExceptionLogger.createSuccessResponse(item);
    }

    // 获取当前维修记录
    @PostMapping("/current-records")
    public ResponseEntity<?> getCurrentRecords(@RequestBody Map<String, Long> payload) {
        Long repairmanId = payload.get("repairmanId");
        List<MaintenanceItem> records = repairmanService.getRepairmanCurrentRecords(repairmanId);
        return ExceptionLogger.createSuccessResponse(records);
    }

    // 获取已完成维修记录
    @PostMapping("/completed-records")
    public ResponseEntity<?> getCompletedRecords(@RequestBody Map<String, Long> payload) {
        Long repairmanId = payload.get("repairmanId");
        List<MaintenanceItem> records = repairmanService.getRepairmanCompletedRecords(repairmanId);
        return ExceptionLogger.createSuccessResponse(records);
    }

    // 查询收入统计
    @GetMapping("/{repairmanId}/income")
    public ResponseEntity<?> getIncomeStatistics(
            @PathVariable Long repairmanId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> incomeData = repairmanService.calculateIncome(repairmanId, startDate, endDate);
        return ExceptionLogger.createSuccessResponse(incomeData);
    }

    // 保存维修项目
    @PostMapping("/maintenance-items")
    public ResponseEntity<?> saveMaintenanceItem(@RequestBody MaintenanceItem record) {
        MaintenanceItem savedRecord = repairmanService.saveMaintenanceItem(record);
        return ExceptionLogger.createSuccessResponse(savedRecord);
    }

    // 接收维修工单
    @PostMapping("/maintenance-items/accept")
    public ResponseEntity<?> acceptMaintenanceItem(@RequestBody Map<String, Long> payload) {
        Long repairmanId = payload.get("repairmanId");
        Long itemId = payload.get("itemId");
        MaintenanceItem item = repairmanService.acceptMaintenanceItem(repairmanId, itemId);
        return ExceptionLogger.createSuccessResponse(item);
    }

    // 接收维修工单 (与API文档匹配的路径版本)
    @PostMapping("/{repairmanId}/maintenance-items/{itemId}/accept")
    public ResponseEntity<?> acceptMaintenanceItemByPath(
            @PathVariable Long repairmanId,
            @PathVariable Long itemId) {
        MaintenanceItem item = repairmanService.acceptMaintenanceItem(repairmanId, itemId);
        return ExceptionLogger.createSuccessResponse(item);
    }

    // 拒绝维修工单
    @PostMapping("/{repairmanId}/maintenance-items/{itemId}/reject")
    public ResponseEntity<?> rejectMaintenanceItem(
            @PathVariable Long repairmanId,
            @PathVariable Long itemId,
            @RequestBody Map<String, String> payload) {
        String reason = payload.get("reason");
        MaintenanceItem item = repairmanService.rejectMaintenanceItem(repairmanId, itemId, reason);
        return ExceptionLogger.createSuccessResponse(item);
    }

    // 完成维修工单
    @PostMapping("/{repairmanId}/maintenance-items/{itemId}/complete")
    public ResponseEntity<?> completeMaintenanceItem(
            @PathVariable Long repairmanId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Object> payload) {
        String result = (String) payload.get("result");
        Double workingHours = Double.valueOf(payload.get("workingHours").toString());
        List<Map<String, Object>> materialsUsed = (List<Map<String, Object>>) payload.get("materials");
        
        MaintenanceItem item = repairmanService.completeMaintenanceItem(repairmanId, itemId, result, workingHours, materialsUsed);
        return ExceptionLogger.createSuccessResponse(item);
    }

    // 示例：获取工单材料及用量
    // @GetMapping("/maintenance-items/{itemId}/materials")
    // public Map<Material, Integer> getMaterialsWithAmount(@PathVariable Long itemId) {
    //     MaintenanceItem item = maintenanceItemRepository.findById(itemId).orElseThrow(...);
    //     return item.getMaterials();
    // }
}
