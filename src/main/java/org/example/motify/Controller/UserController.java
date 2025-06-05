package org.example.motify.Controller;

import org.example.motify.Entity.Car;
import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Entity.User;
import org.example.motify.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
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
        data.put("name", result.getName());
        data.put("phone", result.getPhone());
        data.put("email", result.getEmail());
        data.put("address", result.getAddress());

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
    public Map<String, Object> updateUser(@PathVariable Long userId,
            @RequestBody Map<String, Object> updateData) {
        logger.info("========== START updateUser request ==========");
        logger.info("Updating user - userId: {}", userId);
        logger.info("Update data: {}", updateData);

        try {
            // 验证允许更新的字段
            Map<String, Object> allowedFields = new HashMap<>();

            // 用户名更新
            if (updateData.containsKey("username")) {
                String username = (String) updateData.get("username");
                if (username != null && !username.trim().isEmpty()) {
                    allowedFields.put("username", username.trim());
                }
            }

            // 姓名更新
            if (updateData.containsKey("name")) {
                String name = (String) updateData.get("name");
                if (name != null && !name.trim().isEmpty()) {
                    allowedFields.put("name", name.trim());
                }
            }

            // 手机号更新
            if (updateData.containsKey("phone")) {
                String phone = (String) updateData.get("phone");
                if (phone != null && !phone.trim().isEmpty()) {
                    allowedFields.put("phone", phone.trim());
                }
            }

            // 邮箱更新
            if (updateData.containsKey("email")) {
                String email = (String) updateData.get("email");
                if (email != null && !email.trim().isEmpty()) {
                    allowedFields.put("email", email.trim());
                }
            }

            // 地址更新
            if (updateData.containsKey("address")) {
                String address = (String) updateData.get("address");
                if (address != null) { // 地址可以为空字符串
                    allowedFields.put("address", address.trim());
                }
            }

            // 检查是否有要更新的字段
            if (allowedFields.isEmpty()) {
                Map<String, Object> errorResp = new HashMap<>();
                errorResp.put("code", 400);
                errorResp.put("message", "没有有效的更新字段");
                errorResp.put("data", null);
                return errorResp;
            }

            logger.info("Allowed fields for update: {}", allowedFields);
            User result = userService.updateUserSelective(userId, allowedFields);

            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("userId", result.getUserId());
            data.put("username", result.getUsername());
            data.put("name", result.getName());
            data.put("phone", result.getPhone());
            data.put("email", result.getEmail());
            data.put("address", result.getAddress());

            Map<String, Object> resp = new HashMap<>();
            resp.put("code", 200);
            resp.put("message", "用户信息更新成功");
            resp.put("data", data);

            logger.info("User updated successfully - userId: {}", userId);
            logger.info("========== END updateUser request SUCCESS ==========");
            return resp;

        } catch (Exception e) {
            logger.error("========== ERROR in updateUser method ==========");
            logger.error("Error details - userId: {}, error message: {}", userId, e.getMessage());
            logger.error("Exception type: {}", e.getClass().getSimpleName());
            logger.error("Full stack trace: ", e);
            logger.error("========== END updateUser request ERROR ==========");

            Map<String, Object> errorResp = new HashMap<>();
            errorResp.put("code", 500);
            errorResp.put("message", "更新用户信息失败: " + e.getMessage());
            errorResp.put("data", null);
            return errorResp;
        }
    }

    /**
     * 获取用户车辆列表
     */
    @GetMapping("/{userId}/cars")
    public Map<String, Object> getUserCars(@PathVariable Long userId) {
        List<Map<String, Object>> cars = userService.getUserCarsSafe(userId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", cars);
        return resp;
    }

    /**
     * 添加车辆
     */
    @RequestMapping(value = "/{userId}/cars", method = RequestMethod.POST)
    public Map<String, Object> addCar(@PathVariable Long userId,
            HttpServletRequest request) {
        logger.info("========== START addCar request ==========");
        logger.info("Received addCar request - userId: {}", userId);
        logger.info("Request content-type: '{}'", request.getContentType());
        logger.info("Request method: {}", request.getMethod());
        logger.info("Request URI: {}", request.getRequestURI());

        try {
            // 手动读取请求体
            StringBuilder requestBody = new StringBuilder();
            String line;
            try (java.io.BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            logger.info("Request body: {}", requestBody.toString());

            // 手动解析JSON
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Car car = objectMapper.readValue(requestBody.toString(), Car.class);

            if (car == null) {
                logger.error("Car object is null in request body");
                Map<String, Object> errorResp = new HashMap<>();
                errorResp.put("code", 400);
                errorResp.put("message", "Car object cannot be null");
                errorResp.put("data", null);
                return errorResp;
            }

            logger.info("Car object received - brand: {}, model: {}, licensePlate: {}",
                    car.getBrand(), car.getModel(), car.getLicensePlate());

            // Validate car fields
            if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
                logger.error("Car brand is null or empty");
                Map<String, Object> errorResp = new HashMap<>();
                errorResp.put("code", 400);
                errorResp.put("message", "Car brand cannot be null or empty");
                errorResp.put("data", null);
                return errorResp;
            }

            logger.info("Calling userService.addCar with userId: {} and car: {}", userId, car);
            Car result = userService.addCar(userId, car);
            logger.info("Successfully created car with ID: {}", result.getCarId());

            Map<String, Object> data = new HashMap<>();
            data.put("carId", result.getCarId());
            data.put("brand", result.getBrand());
            data.put("model", result.getModel());
            data.put("licensePlate", result.getLicensePlate());

            Map<String, Object> resp = new HashMap<>();
            resp.put("code", 200);
            resp.put("message", "success");
            resp.put("data", data);

            logger.info("Returning successful response with car ID: {}", result.getCarId());
            logger.info("========== END addCar request SUCCESS ==========");
            return resp;

        } catch (Exception e) {
            logger.error("========== ERROR in addCar method ==========");
            logger.error("Error details - userId: {}, error message: {}", userId, e.getMessage());
            logger.error("Exception type: {}", e.getClass().getSimpleName());
            logger.error("Full stack trace: ", e);
            logger.error("========== END addCar request ERROR ==========");

            Map<String, Object> errorResp = new HashMap<>();
            errorResp.put("code", 500);
            errorResp.put("message", "Internal server error: " + e.getMessage());
            errorResp.put("data", null);
            return errorResp;
        }
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
     * 获取用户当前正在进行的维修项目
     */
    @GetMapping("/{userId}/maintenance-records/current")
    public Map<String, Object> getUserCurrentMaintenanceItems(@PathVariable Long userId) {
        List<MaintenanceItem> currentRecords = userService.getUserCurrentMaintenanceItems(userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", currentRecords);
        return resp;
    }

    /**
     * 提交维修请求
     */
    @PostMapping(value = "/{userId}/maintenance-records", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> submitRepairRequest(@PathVariable Long userId, @RequestBody Map<String, Object> req) {
        Long carId = Long.valueOf(req.get("carId").toString());
        String name = (String) req.get("name");
        String description = (String) req.get("description");

        MaintenanceItem result = userService.submitRepairRequest(userId, carId, name, description);

        Map<String, Object> data = new HashMap<>();
        data.put("itemId", result.getItemId());
        data.put("name", result.getName());
        data.put("description", result.getDescription());
        data.put("status", result.getStatus());
        data.put("progress", result.getProgress());
        data.put("createTime", result.getCreateTime());
        data.put("cost", result.getCost());

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "维修请求提交成功");
        resp.put("data", data);
        return resp;
    }

    /**
     * 重置密码
     */
    @PostMapping(value = "/reset-password", produces = MediaType.APPLICATION_JSON_VALUE)
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

    /**
     * 提交催单请求
     */
    @PostMapping(value = "/{userId}/maintenance-records/{itemId}/rush-order", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> submitRushOrder(@PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestBody Map<String, String> req) {
        String reminderMessage = req.get("reminderMessage");

        MaintenanceItem result = userService.submitRushOrder(userId, itemId, reminderMessage);

        Map<String, Object> data = new HashMap<>();
        data.put("itemId", result.getItemId());
        data.put("name", result.getName());
        data.put("status", result.getStatus());
        data.put("reminder", result.getReminder());
        data.put("updateTime", result.getUpdateTime());

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "催单提交成功");
        resp.put("data", data);
        return resp;
    }

    /**
     * 提交服务评分
     */
    @PostMapping(value = "/{userId}/maintenance-records/{itemId}/rating", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> submitServiceRating(@PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> req) {
        Integer score = req.get("score");

        MaintenanceItem result = userService.submitServiceRating(userId, itemId, score);

        Map<String, Object> data = new HashMap<>();
        data.put("itemId", result.getItemId());
        data.put("name", result.getName());
        data.put("status", result.getStatus());
        data.put("score", result.getScore());
        data.put("updateTime", result.getUpdateTime());

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "评分提交成功");
        resp.put("data", data);
        return resp;
    }

    /**
     * 获取维修项目详情
     */
    @GetMapping("/{userId}/maintenance-records/{itemId}")
    public Map<String, Object> getMaintenanceItemDetail(@PathVariable Long userId,
            @PathVariable Long itemId) {
        MaintenanceItem result = userService.getMaintenanceItemDetail(userId, itemId);

        Map<String, Object> data = new HashMap<>();
        data.put("itemId", result.getItemId());
        data.put("name", result.getName());
        data.put("description", result.getDescription());
        data.put("status", result.getStatus());
        data.put("progress", result.getProgress());
        data.put("result", result.getResult());
        data.put("reminder", result.getReminder());
        data.put("score", result.getScore());
        data.put("createTime", result.getCreateTime());
        data.put("updateTime", result.getUpdateTime());
        data.put("completeTime", result.getCompleteTime());
        data.put("cost", result.getCost());
        data.put("materialCost", result.getMaterialCost());
        data.put("laborCost", result.getLaborCost());

        // 添加车辆信息
        Map<String, Object> carInfo = new HashMap<>();
        carInfo.put("carId", result.getCar().getCarId());
        carInfo.put("brand", result.getCar().getBrand());
        carInfo.put("model", result.getCar().getModel());
        carInfo.put("licensePlate", result.getCar().getLicensePlate());
        data.put("car", carInfo);

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "success");
        resp.put("data", data);
        return resp;
    }

    /**
     * 测试端点 - 用于诊断Content-Type问题
     */
    @RequestMapping(value = "/{userId}/cars/test", method = RequestMethod.POST)
    public Map<String, Object> testAddCar(@PathVariable Long userId,
            HttpServletRequest request) {
        logger.info("========== TEST endpoint reached ==========");
        logger.info("Request content-type: '{}'", request.getContentType());
        logger.info("Request method: {}", request.getMethod());
        logger.info("Request URI: {}", request.getRequestURI());

        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 200);
        resp.put("message", "Test endpoint reached successfully");
        resp.put("contentType", request.getContentType());
        resp.put("method", request.getMethod());
        resp.put("uri", request.getRequestURI());
        return resp;
    }
}