package org.example.motify.Controller;

import org.example.motify.Entity.Admin;
import org.example.motify.Service.AdminService;
import org.example.motify.Exception.AuthenticationException;
import org.example.motify.Exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 管理员注册
     */
    @PostMapping("/register")
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
    @PostMapping("/login")
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
}