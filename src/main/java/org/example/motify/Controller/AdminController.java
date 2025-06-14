package org.example.motify.Controller;

import org.example.motify.Entity.Admin;
import org.example.motify.Entity.User;
import org.example.motify.Entity.Repairman;
import org.example.motify.Entity.Car;
import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Entity.MaintenanceRecord;
import org.example.motify.Entity.Wage;
import org.example.motify.Entity.Material;
import org.example.motify.Service.AdminService;
import org.example.motify.Exception.AuthenticationException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Enum.MaterialType;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Enum.RepairmanType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员注册
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        try {
            Admin registeredAdmin = adminService.registerAdmin(admin);
            // 不返回密码
            registeredAdmin.setPassword(null);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "管理员注册成功",
                    "data", registeredAdmin));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "注册失败：" + e.getMessage()));
        }
    }

    /**
     * 管理员登录
     */
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            Optional<Admin> adminOpt = adminService.loginAdmin(username, password);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                // 不返回密码
                admin.setPassword(null);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "登录成功",
                        "data", admin));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "登录失败"));
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "系统错误：" + e.getMessage()));
        }
    }

    /**
     * 获取管理员信息
     */
    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdminInfo(@PathVariable Long adminId) {
        try {
            Optional<Admin> adminOpt = adminService.findById(adminId);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                admin.setPassword(null); // 不返回密码
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "data", admin));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取信息失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有用户信息
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            // 清除密码信息，保护隐私
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取用户列表成功",
                    "data", users,
                    "count", users.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取用户列表失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有维修人员信息
     */
    @GetMapping("/repairmen")
    public ResponseEntity<?> getAllRepairmen() {
        try {
            List<Repairman> repairmen = adminService.getAllRepairmen();
            // 清除密码信息，保护隐私
            repairmen.forEach(repairman -> repairman.setPassword(null));
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取维修人员列表成功",
                    "data", repairmen,
                    "count", repairmen.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取维修人员列表失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有车辆信息
     */
    @GetMapping("/cars")
    public ResponseEntity<?> getAllCars() {
        try {
            List<Car> cars = adminService.getAllCars();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取车辆列表成功",
                    "data", cars,
                    "count", cars.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取车辆列表失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有维修工单信息
     */
    @GetMapping("/maintenance-items")
    public ResponseEntity<?> getAllMaintenanceItems() {
        try {
            List<MaintenanceItem> maintenanceItems = adminService.getAllMaintenanceItems();

            // 清除维修人员密码字段以保护隐私
            maintenanceItems.forEach(item -> {
                if (item.getRepairmen() != null) {
                    item.getRepairmen().forEach(repairman -> repairman.setPassword(null));
                }
                if (item.getAcceptedRepairmen() != null) {
                    item.getAcceptedRepairmen().forEach(repairman -> repairman.setPassword(null));
                }
                if (item.getPendingRepairmen() != null) {
                    item.getPendingRepairmen().forEach(repairman -> repairman.setPassword(null));
                }
            });

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取维修工单列表成功",
                    "data", maintenanceItems,
                    "count", maintenanceItems.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取维修工单列表失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有历史维修记录
     */
    @GetMapping("/maintenance-records")
    public ResponseEntity<?> getAllMaintenanceRecords() {
        try {
            List<MaintenanceRecord> maintenanceRecords = adminService.getAllMaintenanceRecords();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取历史维修记录列表成功",
                    "data", maintenanceRecords,
                    "count", maintenanceRecords.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取历史维修记录列表失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有工时费发放记录
     */
    @GetMapping("/wages")
    public ResponseEntity<?> getAllWages() {
        try {
            List<Wage> wages = adminService.getAllWages();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取工时费发放记录列表成功",
                    "data", wages,
                    "count", wages.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取工时费发放记录列表失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有材料库存信息
     */
    @GetMapping("/materials")
    public ResponseEntity<?> getAllMaterials() {
        try {
            List<Material> materials = adminService.getAllMaterials();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取材料库存列表成功",
                    "data", materials,
                    "count", materials.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取材料库存列表失败：" + e.getMessage()));
        }
    }

    /**
     * 根据材料类型获取材料库存
     */
    @GetMapping("/materials/by-type")
    public ResponseEntity<?> getMaterialsByType(@RequestParam String type) {
        try {
            MaterialType materialType = MaterialType.valueOf(type.toUpperCase());
            List<Material> materials = adminService.getMaterialsByType(materialType);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取指定类型材料库存成功",
                    "data", materials,
                    "count", materials.size()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "无效的材料类型：" + type));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取指定类型材料库存失败：" + e.getMessage()));
        }
    }

    /**
     * 根据库存范围获取材料
     */
    @GetMapping("/materials/by-stock-range")
    public ResponseEntity<?> getMaterialsByStockRange(
            @RequestParam Integer minStock,
            @RequestParam Integer maxStock) {
        try {
            if (minStock < 0 || maxStock < 0 || minStock > maxStock) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "库存范围参数无效"));
            }

            List<Material> materials = adminService.getMaterialsByStockRange(minStock, maxStock);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取指定库存范围材料成功",
                    "data", materials,
                    "count", materials.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取指定库存范围材料失败：" + e.getMessage()));
        }
    }

    /**
     * 获取低库存材料（库存小于等于10的材料）
     */
    @GetMapping("/materials/low-stock")
    public ResponseEntity<?> getLowStockMaterials() {
        try {
            List<Material> materials = adminService.getMaterialsByStockRange(0, 10);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取低库存材料成功",
                    "data", materials,
                    "count", materials.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取低库存材料失败：" + e.getMessage()));
        }
    }

    // =============== 数据统计查询接口 ===============

    /**
     * 统计各车型的维修次数与平均维修费用
     */
    @GetMapping("/statistics/car-model-repairs")
    public ResponseEntity<?> getCarModelRepairStatistics() {
        try {
            List<Object[]> statistics = adminService.getCarModelRepairStatistics();
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("brand", row[0] != null ? row[0].toString() : "");
                map.put("model", row[1] != null ? row[1].toString() : "");
                map.put("repairCount", row[2] != null ? Integer.parseInt(row[2].toString()) : 0);
                map.put("avgCost", row[3] != null ? Double.parseDouble(row[3].toString()) : 0.0);
                map.put("totalCost", row[4] != null ? Double.parseDouble(row[4].toString()) : 0.0);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取车型维修统计成功",
                    "data", result,
                    "count", result.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取车型维修统计失败：" + e.getMessage()));
        }
    }

    /**
     * 统计特定车型最常出现的故障类型
     */
    @GetMapping("/statistics/car-model-faults")
    public ResponseEntity<?> getCarModelFaultStatistics(
            @RequestParam String brand,
            @RequestParam String model) {
        try {
            List<Object[]> statistics = adminService.getCarModelFaultStatistics(brand, model);
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("faultType", row[0] != null ? row[0].toString() : "");
                map.put("occurrenceCount", row[1] != null ? Integer.parseInt(row[1].toString()) : 0);
                map.put("avgRepairCost", row[2] != null ? Double.parseDouble(row[2].toString()) : 0.0);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取车型故障统计成功",
                    "data", result,
                    "count", result.size(),
                    "brand", brand,
                    "model", model));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取车型故障统计失败：" + e.getMessage()));
        }
    }

    /**
     * 按月份统计维修费用构成
     */
    @GetMapping("/statistics/monthly-cost-analysis")
    public ResponseEntity<?> getMonthlyCostAnalysis(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<Object[]> statistics = adminService.getMonthlyCostAnalysis(startDate, endDate);
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("year", row[0] != null ? Integer.parseInt(row[0].toString()) : 0);
                map.put("month", row[1] != null ? Integer.parseInt(row[1].toString()) : 0);
                map.put("totalRecords", row[2] != null ? Integer.parseInt(row[2].toString()) : 0);
                map.put("totalCost", row[3] != null ? Double.parseDouble(row[3].toString()) : 0.0);
                map.put("totalMaterialCost", row[4] != null ? Double.parseDouble(row[4].toString()) : 0.0);
                map.put("totalLaborCost", row[5] != null ? Double.parseDouble(row[5].toString()) : 0.0);
                map.put("materialCostPercentage", row[6] != null ? Double.parseDouble(row[6].toString()) : 0.0);
                map.put("laborCostPercentage", row[7] != null ? Double.parseDouble(row[7].toString()) : 0.0);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取月度费用分析成功",
                    "data", result,
                    "count", result.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取月度费用分析失败：" + e.getMessage()));
        }
    }

    /**
     * 按季度统计维修费用构成
     */
    @GetMapping("/statistics/quarterly-cost-analysis")
    public ResponseEntity<?> getQuarterlyCostAnalysis(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<Object[]> statistics = adminService.getQuarterlyCostAnalysis(startDate, endDate);
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("year", row[0] != null ? Integer.parseInt(row[0].toString()) : 0);
                map.put("quarter", row[1] != null ? Integer.parseInt(row[1].toString()) : 0);
                map.put("totalRecords", row[2] != null ? Integer.parseInt(row[2].toString()) : 0);
                map.put("totalCost", row[3] != null ? Double.parseDouble(row[3].toString()) : 0.0);
                map.put("totalMaterialCost", row[4] != null ? Double.parseDouble(row[4].toString()) : 0.0);
                map.put("totalLaborCost", row[5] != null ? Double.parseDouble(row[5].toString()) : 0.0);
                map.put("materialCostPercentage", row[6] != null ? Double.parseDouble(row[6].toString()) : 0.0);
                map.put("laborCostPercentage", row[7] != null ? Double.parseDouble(row[7].toString()) : 0.0);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取季度费用分析成功",
                    "data", result,
                    "count", result.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取季度费用分析失败：" + e.getMessage()));
        }
    }

    /**
     * 筛选负面反馈工单及涉及的员工
     */
    @GetMapping("/statistics/negative-feedback")
    public ResponseEntity<?> getNegativeFeedbackOrders(@RequestParam(defaultValue = "2") Integer maxScore) {
        try {
            List<Object[]> statistics = adminService.getNegativeFeedbackOrders(maxScore);
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("itemId", row[0] != null ? Long.parseLong(row[0].toString()) : 0L);
                map.put("itemName", row[1] != null ? row[1].toString() : "");
                map.put("score", row[2] != null ? Integer.parseInt(row[2].toString()) : 0);
                map.put("result", row[3] != null ? row[3].toString() : "");
                map.put("carBrand", row[4] != null ? row[4].toString() : "");
                map.put("carModel", row[5] != null ? row[5].toString() : "");
                map.put("licensePlate", row[6] != null ? row[6].toString() : "");
                map.put("repairmanId", row[7] != null ? Long.parseLong(row[7].toString()) : 0L);
                map.put("repairmanName", row[8] != null ? row[8].toString() : "");
                map.put("repairmanType", row[9] != null ? row[9].toString() : "");
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取负面反馈工单统计成功",
                    "data", result,
                    "count", result.size(),
                    "maxScore", maxScore));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取负面反馈工单统计失败：" + e.getMessage()));
        }
    }

    /**
     * 统计不同工种在一段时间内接受和完成的任务数量及占比
     */
    @GetMapping("/statistics/repairman-type-tasks")
    public ResponseEntity<?> getRepairmanTypeTaskStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<Object[]> statistics = adminService.getRepairmanTypeTaskStatistics(startDate, endDate);
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("repairmanType", row[0] != null ? row[0].toString() : "");
                map.put("acceptedTasks", row[1] != null ? Integer.parseInt(row[1].toString()) : 0);
                map.put("completedTasks", row[2] != null ? Integer.parseInt(row[2].toString()) : 0);
                map.put("acceptedPercentage", row[3] != null ? Double.parseDouble(row[3].toString()) : 0.0);
                map.put("completedPercentage", row[4] != null ? Double.parseDouble(row[4].toString()) : 0.0);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取工种任务统计成功",
                    "data", result,
                    "count", result.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取工种任务统计失败：" + e.getMessage()));
        }
    }

    /**
     * 统计未完成的维修任务概览
     */
    @GetMapping("/statistics/uncompleted-tasks-overview")
    public ResponseEntity<?> getUncompletedTasksOverview() {
        try {
            List<Object[]> statistics = adminService.getUncompletedTasksOverview();
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("status", row[0] != null ? row[0].toString() : "");
                map.put("taskCount", row[1] != null ? Integer.parseInt(row[1].toString()) : 0);
                map.put("avgDaysPending", row[2] != null ? Integer.parseInt(row[2].toString()) : 0);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取未完成任务概览成功",
                    "data", result,
                    "count", result.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取未完成任务概览失败：" + e.getMessage()));
        }
    }

    /**
     * 按工种统计未完成任务
     */
    @GetMapping("/statistics/uncompleted-tasks-by-type")
    public ResponseEntity<?> getUncompletedTasksByRepairmanType() {
        try {
            List<Object[]> statistics = adminService.getUncompletedTasksByRepairmanType();
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("repairmanType", row[0] != null ? row[0].toString() : "");
                map.put("uncompletedTasks", row[1] != null ? Integer.parseInt(row[1].toString()) : 0);
                map.put("avgDaysPending", row[2] != null ? Integer.parseInt(row[2].toString()) : 0);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取工种未完成任务统计成功",
                    "data", result,
                    "count", result.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取工种未完成任务统计失败：" + e.getMessage()));
        }
    }

    /**
     * 按维修人员统计未完成任务
     */
    @GetMapping("/statistics/uncompleted-tasks-by-repairman")
    public ResponseEntity<?> getUncompletedTasksByRepairman() {
        try {
            List<Object[]> statistics = adminService.getUncompletedTasksByRepairman();
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("repairmanId", row[0] != null ? Long.parseLong(row[0].toString()) : 0L);
                map.put("repairmanName", row[1] != null ? row[1].toString() : "");
                map.put("repairmanType", row[2] != null ? row[2].toString() : "");
                map.put("uncompletedTasks", row[3] != null ? Integer.parseInt(row[3].toString()) : 0);
                map.put("avgDaysPending", row[4] != null ? Integer.parseInt(row[4].toString()) : 0);
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取维修人员未完成任务统计成功",
                    "data", result,
                    "count", result.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取维修人员未完成任务统计失败：" + e.getMessage()));
        }
    }

    /**
     * 按车辆统计未完成任务
     */
    @GetMapping("/statistics/uncompleted-tasks-by-car")
    public ResponseEntity<?> getUncompletedTasksByCar() {
        try {
            List<Object[]> statistics = adminService.getUncompletedTasksByCar();
            List<Map<String, Object>> result = statistics.stream().map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("brand", row[0] != null ? row[0].toString() : "");
                map.put("model", row[1] != null ? row[1].toString() : "");
                map.put("licensePlate", row[2] != null ? row[2].toString() : "");
                map.put("taskCount", row[3] != null ? Integer.parseInt(row[3].toString()) : 0);
                map.put("taskNames", row[4] != null ? row[4].toString() : "");
                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取按车辆统计的未完成任务成功",
                    "data", result,
                    "count", result.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取按车辆统计的未完成任务失败：" + e.getMessage()));
        }
    }

    /**
     * 获取所有等待分配工种的维修工单
     */
    @GetMapping("/maintenance-items/awaiting-assignment")
    public ResponseEntity<?> getMaintenanceItemsAwaitingAssignment() {
        try {
            List<MaintenanceItem> items = adminService
                    .getMaintenanceItemsByStatus(MaintenanceStatus.AWAITING_ASSIGNMENT);

            // 清除维修人员密码字段以保护隐私
            items.forEach(item -> {
                if (item.getRepairmen() != null) {
                    item.getRepairmen().forEach(repairman -> repairman.setPassword(null));
                }
            });

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "获取等待分配工种的维修工单成功",
                    "data", items,
                    "count", items.size()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "获取等待分配工种的维修工单失败：" + e.getMessage()));
        }
    }

    /**
     * 管理员为维修工单分配维修工种和数量
     */
    @PostMapping("/maintenance-items/{itemId}/assign-types")
    public ResponseEntity<?> assignRepairmanTypesToMaintenanceItem(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> requiredTypes) {
        try {
            // 将字符串类型的工种转换为枚举类型
            Map<RepairmanType, Integer> requiredTypesMap = new HashMap<>();
            for (Map.Entry<String, Integer> entry : requiredTypes.entrySet()) {
                try {
                    RepairmanType type = RepairmanType.valueOf(entry.getKey().toUpperCase());
                    requiredTypesMap.put(type, entry.getValue());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "无效的维修工种类型：" + entry.getKey()));
                }
            }

            MaintenanceItem updatedItem = adminService.assignRepairmanTypesToMaintenanceItem(itemId, requiredTypesMap);

            // 清除维修人员密码字段以保护隐私
            if (updatedItem.getRepairmen() != null) {
                updatedItem.getRepairmen().forEach(repairman -> repairman.setPassword(null));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "维修工种分配成功",
                    "data", updatedItem));
        } catch (BadRequestException | ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "维修工种分配失败：" + e.getMessage()));
        }
    }
}