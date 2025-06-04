package org.example.motify.Controller;

import org.example.motify.Entity.*;
import org.example.motify.Service.RepairmanService;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;


@RestController
@RequestMapping({"/api/repairman", "/api/repairmen"})
public class RepairmanController {
    @Autowired
    private RepairmanService repairmanService;

    // 注册维修人员
    @PostMapping("/register")
    public ResponseEntity<Repairman> register(@RequestBody Repairman repairman) {
        try {
            Repairman registeredRepairman = repairmanService.register(repairman);
            return ResponseEntity.ok(registeredRepairman);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 维修人员登录
    @PostMapping("/login")
    public ResponseEntity<Repairman> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            Optional<Repairman> repairman = repairmanService.login(username, password);
            return repairman.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().build();
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取维修人员信息
    @PostMapping("/info")
    public ResponseEntity<Repairman> getRepairmanInfo(@RequestBody Map<String, Long> payload) {
        try {
            Long repairmanId = payload.get("repairmanId");
            Optional<Repairman> repairman = repairmanService.getRepairmanById(repairmanId);
            return repairman.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 更新维修人员信息
    @PostMapping("/update")
    public ResponseEntity<Repairman> updateRepairman(@RequestBody Repairman repairman) {
        try {
            Repairman updatedRepairman = repairmanService.updateRepairman(repairman);
            return ResponseEntity.ok(updatedRepairman);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取维修人员的维修项目列表
    @PostMapping("/maintenance-items/list")
    public ResponseEntity<List<MaintenanceItem>> getMaintenanceItems(@RequestBody Map<String, Long> payload) {
        try {
            Long repairmanId = payload.get("repairmanId");
            List<MaintenanceItem> items = repairmanService.getRepairmanMaintenanceItems(repairmanId);
            return ResponseEntity.ok(items);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // // 更新维修项目
    // @PutMapping("/maintenance-items/{recordId}")
    // public ResponseEntity<MaintenanceItem> updateMaintenanceItem(
    //         @PathVariable Long recordId,
    //         @RequestBody MaintenanceItem record) {
    //     try {
    //         MaintenanceItem updatedRecord = repairmanService.updateMaintenanceItem(recordId, record);
    //         return ResponseEntity.ok(updatedRecord);
    //     } catch (ResourceNotFoundException e) {
    //         return ResponseEntity.notFound().build();
    //     } catch (BadRequestException e) {
    //         return ResponseEntity.badRequest().build();
    //     }
    // }

    // 获取当前维修记录
    @PostMapping("/current-records")
    public ResponseEntity<List<MaintenanceItem>> getCurrentRecords(@RequestBody Map<String, Long> payload) {
        try {
            Long repairmanId = payload.get("repairmanId");
            List<MaintenanceItem> records = repairmanService.getRepairmanCurrentRecords(repairmanId);
            return ResponseEntity.ok(records);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 获取已完成维修记录
    @PostMapping("/completed-records")
    public ResponseEntity<List<MaintenanceItem>> getCompletedRecords(@RequestBody Map<String, Long> payload) {
        try {
            Long repairmanId = payload.get("repairmanId");
            List<MaintenanceItem> records = repairmanService.getRepairmanCompletedRecords(repairmanId);
            return ResponseEntity.ok(records);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // // 计算总收入
    // @GetMapping("/{repairmanId}/total-income")
    // public ResponseEntity<Double> getTotalIncome(@PathVariable Long repairmanId) {
    //     try {
    //         double totalIncome = repairmanService.calculateTotalIncome(repairmanId);
    //         return ResponseEntity.ok(totalIncome);
    //     } catch (ResourceNotFoundException e) {
    //         return ResponseEntity.notFound().build();
    //     }
    // }

    // 保存维修项目
    @PostMapping("/maintenance-items")
    public ResponseEntity<MaintenanceItem> saveMaintenanceItem(@RequestBody MaintenanceItem record) {
        try {
            MaintenanceItem savedRecord = repairmanService.saveMaintenanceItem(record);
            return ResponseEntity.ok(savedRecord);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 接收维修工单
    @PostMapping("/maintenance-items/accept")
    public ResponseEntity<Map<String, Object>> acceptMaintenanceItem(@RequestBody Map<String, Long> payload) {
        Map<String, Object> resp = new java.util.HashMap<>();
        try {
            Long repairmanId = payload.get("repairmanId");
            Long itemId = payload.get("itemId");
            MaintenanceItem item = repairmanService.acceptMaintenanceItem(repairmanId, itemId);
            resp.put("code", 200);
            resp.put("message", "success");
            resp.put("data", item);
            return ResponseEntity.ok(resp);
        } catch (BadRequestException e) {
            resp.put("code", 400);
            resp.put("message", e.getMessage());
            resp.put("data", null);
            return ResponseEntity.status(400).body(resp);
        } catch (ResourceNotFoundException e) {
            resp.put("code", 404);
            resp.put("message", e.getMessage());
            resp.put("data", null);
            return ResponseEntity.status(404).body(resp);
        }
    }

    // 示例：获取工单材料及用量
    // @GetMapping("/maintenance-items/{itemId}/materials")
    // public Map<Material, Integer> getMaterialsWithAmount(@PathVariable Long itemId) {
    //     MaintenanceItem item = maintenanceItemRepository.findById(itemId).orElseThrow(...);
    //     return item.getMaterials();
    // }
}
