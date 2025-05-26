package org.example.motify.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.motify.Entity.Car;
import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Entity.User;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Service.UserService;
import org.example.motify.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private Car testCar;
    private MaintenanceItem testMaintenanceItem;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testUser");
        testUser.setPhone("13800138000");

        testCar = new Car();
        testCar.setCarId(1L);
        testCar.setBrand("Toyota");
        testCar.setModel("Camry");
        testCar.setLicensePlate("京A12345");
        testCar.setUser(testUser);

        testMaintenanceItem = new MaintenanceItem();
        testMaintenanceItem.setItemId(1L);
        testMaintenanceItem.setDescription("维修描述");
        testMaintenanceItem.setStatus(MaintenanceStatus.PENDING);
        testMaintenanceItem.setProgress(0);
        testMaintenanceItem.setCar(testCar);
    }

    @Test
    @WithMockUser
    void register_Success() throws Exception {
        when(userService.register(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/auth/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testUser"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    @WithMockUser
    void login_Success() throws Exception {
        when(userService.login(anyString(), anyString())).thenReturn(Optional.of(testUser));

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testUser");
        loginRequest.put("password", "password123");

        mockMvc.perform(post("/api/auth/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testUser"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    @WithMockUser
    void getUser_Success() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(testUser);

        mockMvc.perform(get("/api/auth/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testUser"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    @WithMockUser
    void updateUser_Success() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/auth/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testUser"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"));
    }

    @Test
    @WithMockUser
    void getUserCars_Success() throws Exception {
        List<Car> cars = Arrays.asList(testCar);
        when(userService.getUserCars(anyLong())).thenReturn(cars);

        mockMvc.perform(get("/api/auth/users/1/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].carId").value(1))
                .andExpect(jsonPath("$.data[0].brand").value("Toyota"))
                .andExpect(jsonPath("$.data[0].model").value("Camry"))
                .andExpect(jsonPath("$.data[0].licensePlate").value("京A12345"));
    }

    @Test
    @WithMockUser
    void addCar_Success() throws Exception {
        when(userService.addCar(anyLong(), any(Car.class))).thenReturn(testCar);

        mockMvc.perform(post("/api/auth/users/1/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.carId").value(1))
                .andExpect(jsonPath("$.data.brand").value("Toyota"))
                .andExpect(jsonPath("$.data.model").value("Camry"))
                .andExpect(jsonPath("$.data.licensePlate").value("京A12345"));
    }

    @Test
    @WithMockUser
    void getUserMaintenanceItems_Success() throws Exception {
        List<MaintenanceItem> items = Arrays.asList(testMaintenanceItem);
        when(userService.getUserMaintenanceItems(anyLong())).thenReturn(items);

        mockMvc.perform(get("/api/auth/users/1/maintenance-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].itemId").value(1))
                .andExpect(jsonPath("$.data[0].description").value("维修描述"))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"))
                .andExpect(jsonPath("$.data[0].progress").value(0));
    }

    @Test
    @WithMockUser
    void submitRepairRequest_Success() throws Exception {
        when(userService.submitRepairRequest(anyLong(), anyLong(), anyString()))
                .thenReturn(testMaintenanceItem);

        Map<String, Object> request = new HashMap<>();
        request.put("carId", 1);
        request.put("description", "维修描述");

        mockMvc.perform(post("/api/auth/users/1/maintenance-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.recordId").value(1))
                .andExpect(jsonPath("$.data.description").value("维修描述"))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.progress").value(0));
    }

    @Test
    @WithMockUser
    void resetPassword_Success() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("phone", "13800138000");
        request.put("code", "123456");
        request.put("newPassword", "newPassword123");

        mockMvc.perform(post("/api/auth/users/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码重置成功"));
    }
} 