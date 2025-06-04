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
@RequestMapping("/api/repairmen")
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
    public ResponseEntity<Repairman> login(@RequestParam String username, @RequestParam String password) {
        try {
            Optional<Repairman> repairman = repairmanService.login(username, password);
            return repairman.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取维修人员信息
    @GetMapping("/{repairmanId}")
    public ResponseEntity<Repairman> getRepairmanInfo(@PathVariable Long repairmanId) {
        try {
            Optional<Repairman> repairman = repairmanService.getRepairmanById(repairmanId);
            return repairman.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 更新维修人员信息
    @PutMapping("/{repairmanId}")
    public ResponseEntity<Repairman> updateRepairman(@PathVariable Long repairmanId, @RequestBody Repairman repairman) {
        try {
            repairman.setRepairmanId(repairmanId);
            Repairman updatedRepairman = repairmanService.updateRepairman(repairman);
            return ResponseEntity.ok(updatedRepairman);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 获取维修人员的维修项目列表
    @GetMapping("/{repairmanId}/maintenance-items")
    public ResponseEntity<List<MaintenanceItem>> getMaintenanceItems(@PathVariable Long repairmanId) {
        try {
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
    @GetMapping("/{repairmanId}/current-records")
    public ResponseEntity<List<MaintenanceItem>> getCurrentRecords(@PathVariable Long repairmanId) {
        try {
            List<MaintenanceItem> records = repairmanService.getRepairmanCurrentRecords(repairmanId);
            return ResponseEntity.ok(records);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 获取已完成维修记录
    @GetMapping("/{repairmanId}/completed-records")
    public ResponseEntity<List<MaintenanceItem>> getCompletedRecords(@PathVariable Long repairmanId) {
        try {
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

    // // 接受维修项目
    // @PostMapping("/{repairmanId}/accept/{recordId}")
    // public ResponseEntity<MaintenanceItem> acceptMaintenanceItem(
    //         @PathVariable Long repairmanId,
    //         @PathVariable Long recordId) {
    //     try {
    //         MaintenanceItem record = repairmanService.acceptMaintenanceItem(repairmanId, recordId);
    //         return ResponseEntity.ok(record);
    //     } catch (ResourceNotFoundException e) {
    //         return ResponseEntity.notFound().build();
    //     } catch (BadRequestException e) {
    //         return ResponseEntity.badRequest().build();
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

    // 示例：获取工单材料及用量
    // @GetMapping("/maintenance-items/{itemId}/materials")
    // public Map<Material, Integer> getMaterialsWithAmount(@PathVariable Long itemId) {
    //     MaintenanceItem item = maintenanceItemRepository.findById(itemId).orElseThrow(...);
    //     return item.getMaterials();
    // }
} 