package org.example.motify.Controller;

import org.example.motify.Entity.Car;
import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Entity.User;
import org.example.motify.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        User result = userService.register(user);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", result.getUserId());
        data.put("username", result.getUsername());
        data.put("phone", result.getPhone());
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", data);
        return resp;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        User result = userService.login(username, password).orElseThrow();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", result.getUserId());
        data.put("username", result.getUsername());
        data.put("phone", result.getPhone());
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", data);
        return resp;
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public Map<String, Object> getUser(@PathVariable Long userId) {
        User result = userService.getUserById(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", result.getUserId());
        data.put("username", result.getUsername());
        data.put("phone", result.getPhone());
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", data);
        return resp;
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{userId}")
    public Map<String, Object> updateUser(@PathVariable Long userId, @RequestBody User user) {
        User result = userService.updateUser(userId, user);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", result.getUserId());
        data.put("username", result.getUsername());
        data.put("phone", result.getPhone());
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", data);
        return resp;
    }

    /**
     * 获取用户车辆列表
     */
    @GetMapping("/{userId}/cars")
    public Map<String, Object> getUserCars(@PathVariable Long userId) {
        List<Car> cars = userService.getUserCars(userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", cars);
        return resp;
    }

    /**
     * 添加车辆
     */
    @PostMapping("/{userId}/cars")
    public Map<String, Object> addCar(@PathVariable Long userId, @RequestBody Car car) {
        Car result = userService.addCar(userId, car);
        Map<String, Object> data = new HashMap<>();
        data.put("carId", result.getCarId());
        data.put("brand", result.getBrand());
        data.put("model", result.getModel());
        data.put("licensePlate", result.getLicensePlate());
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", data);
        return resp;
    }

    /**
     * 获取用户维修记录
     */
    @GetMapping("/{userId}/maintenance-records")
    public Map<String, Object> getUserMaintenanceItems(@PathVariable Long userId) {
        List<MaintenanceItem> records = userService.getUserMaintenanceItems(userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", records);
        return resp;
    }

    /**
     * 提交维修请求
     */
    @PostMapping("/{userId}/maintenance-records")
    public Map<String, Object> submitRepairRequest(@PathVariable Long userId, @RequestBody Map<String, Object> req) {
        Long carId = Long.valueOf(req.get("carId").toString());
        String description = req.get("description").toString();
        MaintenanceItem result = userService.submitRepairRequest(userId, carId, description);
        Map<String, Object> data = new HashMap<>();
        data.put("recordId", result.getItemId());
        data.put("description", result.getDescription());
        data.put("status", result.getStatus().name());
        data.put("progress", result.getProgress());
        data.put("createTime", result.getRecordInfo() != null ? result.getRecordInfo().getCreateTime() : null);
        Map<String, Object> car = new HashMap<>();
        car.put("carId", result.getCar().getCarId());
        car.put("brand", result.getCar().getBrand());
        car.put("model", result.getCar().getModel());
        car.put("licensePlate", result.getCar().getLicensePlate());
        data.put("car", car);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", data);
        return resp;
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> req) {
        String phone = req.get("phone");
        String code = req.get("code");
        String newPassword = req.get("newPassword");
        userService.resetPassword(phone, code, newPassword);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "密码重置成功");
        resp.put("data", null);
        return resp;
    }
} 