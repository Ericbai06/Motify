package org.example.motify.Controller;

import org.example.motify.Entity.Admin;
import org.example.motify.Entity.User;
import org.example.motify.Entity.Repairman;
import org.example.motify.Entity.Car;
import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Entity.MaintenanceRecord;
import org.example.motify.Entity.Wage;
import org.example.motify.Service.AdminService;
import org.example.motify.Exception.AuthenticationException;
import org.example.motify.Exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 管理员注册
     */
    @PostMapping(value = "/register",
                 produces = MediaType.APPLICATION_JSON_VALUE, 
                 consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        try {
            Admin registeredAdmin = adminService.registerAdmin(admin);
            // 不返回密码
            registeredAdmin.setPassword(null);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "管理员注册成功",
                "data", registeredAdmin
            ));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "注册失败：" + e.getMessage()
            ));
        }
    }
    
    /**
     * 管理员登录
     */
    @PostMapping(value = "/login",
                 produces = MediaType.APPLICATION_JSON_VALUE, 
                 consumes = MediaType.APPLICATION_JSON_VALUE)
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
                    "data", admin
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "登录失败"
                ));
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "系统错误：" + e.getMessage()
            ));
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
                    "data", admin
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "获取信息失败：" + e.getMessage()
            ));
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
                "count", users.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "获取用户列表失败：" + e.getMessage()
            ));
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
                "count", repairmen.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "获取维修人员列表失败：" + e.getMessage()
            ));
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
                "count", cars.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "获取车辆列表失败：" + e.getMessage()
            ));
        }
    }

    /**
     * 获取所有维修工单信息
     */
    @GetMapping("/maintenance-items")
    public ResponseEntity<?> getAllMaintenanceItems() {
        try {
            List<MaintenanceItem> maintenanceItems = adminService.getAllMaintenanceItems();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "获取维修工单列表成功",
                "data", maintenanceItems,
                "count", maintenanceItems.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "获取维修工单列表失败：" + e.getMessage()
            ));
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
                "count", maintenanceRecords.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "获取历史维修记录列表失败：" + e.getMessage()
            ));
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
                "count", wages.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "获取工时费发放记录列表失败：" + e.getMessage()
            ));
        }
    }
}