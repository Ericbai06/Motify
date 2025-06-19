package org.example.motify.Controller;

import org.example.motify.Entity.*;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Enum.RepairmanType;
import org.example.motify.Service.RepairmanService;
import org.example.motify.Exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping({ "/api/repairman", "/api/repairmen" })
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
        if (repairman.isPresent()) {
            return ExceptionLogger.createSuccessResponse(repairman.get());
        } else {
            return ExceptionLogger.createNotFoundResponse("Repairman", username);
        }
    }

    // 获取维修人员信息
    @PostMapping("/info")
    public ResponseEntity<?> getRepairmanInfo(@RequestBody Map<String, Long> payload) {
        Long repairmanId = payload.get("repairmanId");
        Optional<Repairman> repairman = repairmanService.getRepairmanById(repairmanId);
        if (repairman.isPresent()) {
            return ExceptionLogger.createSuccessResponse(repairman.get());
        } else {
            return ExceptionLogger.createNotFoundResponse("Repairman", repairmanId);
        }
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

    /**
     * 获取维修人员已完成的维修工单及其个人工时费统计。
     * <p>
     * 前端传入repairmanId，后端返回该维修人员所有已完成（COMPLETED状态）的工单，
     * 并在每个工单的返回体中增加personalLaborCost字段，表示该维修人员在该工单下的工时费总和。
     * <br>
     * <b>请求示例：</b>
     * 
     * <pre>
     * {
     *   "repairmanId": 1
     * }
     * </pre>
     * 
     * <b>返回示例：</b>
     * 
     * <pre>
     * [
     *   {
     *     "item": { ...MaintenanceItem对象... },
     *     "personalLaborCost": 120.0
     *   },
     *   ...
     * ]
     * </pre>
     * 
     * @param payload 包含repairmanId的Map
     * @return 统一响应结构，data字段为List<Map<String, Object>>，每个Map包含item和personalLaborCost
     */
    @PostMapping("/completed-records")
    public ResponseEntity<?> getCompletedRecords(@RequestBody Map<String, Long> payload) {
        Long repairmanId = payload.get("repairmanId");
        List<Map<String, Object>> records = repairmanService.getRepairmanCompletedRecords(repairmanId);
        return ExceptionLogger.createSuccessResponse(records);
    }

    // 获取已拒绝的维修工单
    @PostMapping("/rejected-items")
    public ResponseEntity<?> getRejectedItems(@RequestBody Map<String, Long> payload) {
        Long repairmanId = payload.get("repairmanId");
        List<MaintenanceItem> items = repairmanService.getRepairmanRejectedItems(repairmanId);
        return ExceptionLogger.createSuccessResponse(items);
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

        MaintenanceItem item = repairmanService.completeMaintenanceItem(repairmanId, itemId, result, workingHours,
                materialsUsed);
        return ExceptionLogger.createSuccessResponse(item);
    }

    /**
     * 添加维修记录（MaintenanceRecord）。
     * <p>
     * 维修人员每次维修后调用此接口，记录本次维修的内容、工时、开始时间及所用材料。
     * <br>
     * 前端可传递 name 字段作为记录名称，若未传递则后端自动生成。
     * <br>
     * 材料用量通过 materials 字段传递，为 List<Map>，每个 map 包含 materialId 和 amount。
     *
     * <b>请求示例：</b>
     * 
     * <pre>
     * {
     *   "maintenanceItemId": 3,
     *   "description": "补胎",
     *   "repairmanId": 1,
     *   "workHours": 1,
     *   "startTime": "2024-06-02T10:00:00",
     *   "name": "补胎记录", // 可选
     *   "materials": [
     *     {"materialId": 9, "amount": 1},
     *     {"materialId": 10, "amount": 1}
     *   ]
     * }
     * </pre>
     *
     * @param payload 包含维修记录信息的 Map，详见上方示例
     * @return 新增的 MaintenanceRecord 对象，封装在统一响应结构中
     */
    @PostMapping("/maintenance-records/add")
    public ResponseEntity<?> addMaintenanceRecord(@RequestBody Map<String, Object> payload) {
        MaintenanceRecord record = repairmanService.addMaintenanceRecord(payload);
        return ExceptionLogger.createSuccessResponse(record);
    }

    // 回滚维修人员信息到历史版本
    @PostMapping("/rollback")
    public ResponseEntity<?> rollbackRepairman(@RequestBody Map<String, Object> req) {
        Long repairmanId = Long.valueOf(req.get("repairmanId").toString());
        Long historyId = Long.valueOf(req.get("historyId").toString());
        Repairman result = repairmanService.rollbackRepairmanToHistory(repairmanId, historyId);
        return ExceptionLogger.createSuccessResponse(result);
    }

    /**
     * 连续撤销维修人员信息到上一个历史版本
     */
    @PostMapping("/{repairmanId}/undo")
    public ResponseEntity<?> undoRepairman(@PathVariable Long repairmanId) {
        Repairman result = repairmanService.undoRepairmanHistory(repairmanId);
        return ExceptionLogger.createSuccessResponse(result);
    }

    /**
     * 连续重做维修人员信息到下一个历史版本
     */
    @PostMapping("/{repairmanId}/redo")
    public ResponseEntity<?> redoRepairman(@PathVariable Long repairmanId) {
        Repairman result = repairmanService.redoRepairmanHistory(repairmanId);
        return ExceptionLogger.createSuccessResponse(result);
    }

    // 获取维修人员撤销/重做能力
    @GetMapping("/{repairmanId}/undo-redo-status")
    public Map<String, Object> getUndoRedoStatus(@PathVariable Long repairmanId) {
        Map<String, Boolean> status = repairmanService.getUndoRedoStatus(repairmanId);
        Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", status);
        return resp;
    }

}
