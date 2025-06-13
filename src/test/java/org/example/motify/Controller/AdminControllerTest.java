package org.example.motify.Controller;

import org.example.motify.Entity.*;
import org.example.motify.Service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AdminController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    private Admin testAdmin;
    private User testUser;
    private Repairman testRepairman;
    private Car testCar;
    private MaintenanceItem testMaintenanceItem;
    private MaintenanceRecord testMaintenanceRecord;
    private Wage testWage;

    @BeforeEach
    void setUp() {
        // 设置测试管理员
        testAdmin = new Admin();
        testAdmin.setAdminId(1L);
        testAdmin.setUsername("admin");
        testAdmin.setName("管理员");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setActive(true);

        // 设置测试用户
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setName("测试用户");
        testUser.setEmail("user@example.com");
        testUser.setPhone("13800138000");

        // 设置测试维修人员
        testRepairman = new Repairman();
        testRepairman.setRepairmanId(1L);
        testRepairman.setUsername("repairman");
        testRepairman.setName("测试维修工");
        testRepairman.setPhone("13900139000");

        // 设置测试车辆
        testCar = new Car();
        testCar.setCarId(1L);
        testCar.setBrand("Toyota");
        testCar.setModel("Camry");
        testCar.setLicensePlate("京A12345");

        // 设置测试维修工单
        testMaintenanceItem = new MaintenanceItem();
        testMaintenanceItem.setItemId(1L);
        testMaintenanceItem.setName("发动机维修");
        testMaintenanceItem.setDescription("发动机需要检修");

        // 设置测试维修记录
        testMaintenanceRecord = new MaintenanceRecord();
        testMaintenanceRecord.setRecordId(1L);
        testMaintenanceRecord.setName("发动机维修记录");        testMaintenanceRecord.setDescription("更换了机油和机滤");
        testMaintenanceRecord.setRepairManId(1L);
        testMaintenanceRecord.setWorkHours(120L); // 2小时

        // 设置测试工资记录
        testWage = new Wage();
        testWage.setId(1L);
        testWage.setRepairmanId(1L);
        testWage.setYear(2024);
        testWage.setMonth(12);
        testWage.setTotalWorkHours(40.0);
        testWage.setTotalIncome(3200.0);
        testWage.setRepairmanName("测试维修工");
        testWage.setRepairmanType("MECHANIC");
        testWage.setHourlyRate(80.0);
    }

    @Test
    void getAdminInfo_Success() throws Exception {
        when(adminService.findById(1L)).thenReturn(Optional.of(testAdmin));

        mockMvc.perform(get("/api/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.adminId").value(1))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.name").value("管理员"));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(adminService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取用户列表成功"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].userId").value(1))
                .andExpect(jsonPath("$.data[0].username").value("testuser"))
                .andExpect(jsonPath("$.data[0].name").value("测试用户"));
    }

    @Test
    void getAllRepairmen_Success() throws Exception {
        List<Repairman> repairmen = Arrays.asList(testRepairman);
        when(adminService.getAllRepairmen()).thenReturn(repairmen);

        mockMvc.perform(get("/api/admin/repairmen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取维修人员列表成功"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].repairmanId").value(1))
                .andExpect(jsonPath("$.data[0].username").value("repairman"))
                .andExpect(jsonPath("$.data[0].name").value("测试维修工"));
    }

    @Test
    void getAllCars_Success() throws Exception {
        List<Car> cars = Arrays.asList(testCar);
        when(adminService.getAllCars()).thenReturn(cars);

        mockMvc.perform(get("/api/admin/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取车辆列表成功"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].carId").value(1))
                .andExpect(jsonPath("$.data[0].brand").value("Toyota"))
                .andExpect(jsonPath("$.data[0].model").value("Camry"))
                .andExpect(jsonPath("$.data[0].licensePlate").value("京A12345"));
    }

    @Test
    void getAllMaintenanceItems_Success() throws Exception {
        List<MaintenanceItem> items = Arrays.asList(testMaintenanceItem);
        when(adminService.getAllMaintenanceItems()).thenReturn(items);

        mockMvc.perform(get("/api/admin/maintenance-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取维修工单列表成功"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].itemId").value(1))
                .andExpect(jsonPath("$.data[0].name").value("发动机维修"))
                .andExpect(jsonPath("$.data[0].description").value("发动机需要检修"));
    }

    @Test
    void getAllMaintenanceRecords_Success() throws Exception {
        List<MaintenanceRecord> records = Arrays.asList(testMaintenanceRecord);
        when(adminService.getAllMaintenanceRecords()).thenReturn(records);

        mockMvc.perform(get("/api/admin/maintenance-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取历史维修记录列表成功"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].recordId").value(1))                .andExpect(jsonPath("$.data[0].name").value("发动机维修记录"))
                .andExpect(jsonPath("$.data[0].description").value("更换了机油和机滤"))
                .andExpect(jsonPath("$.data[0].repairManId").value(1))
                .andExpect(jsonPath("$.data[0].workHours").value(120));
    }

    @Test
    void getAllWages_Success() throws Exception {
        List<Wage> wages = Arrays.asList(testWage);
        when(adminService.getAllWages()).thenReturn(wages);

        mockMvc.perform(get("/api/admin/wages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取工时费发放记录列表成功"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].repairmanId").value(1))
                .andExpect(jsonPath("$.data[0].year").value(2024))
                .andExpect(jsonPath("$.data[0].month").value(12))
                .andExpect(jsonPath("$.data[0].totalWorkHours").value(40.0))
                .andExpect(jsonPath("$.data[0].totalIncome").value(3200.0))
                .andExpect(jsonPath("$.data[0].repairmanName").value("测试维修工"))
                .andExpect(jsonPath("$.data[0].repairmanType").value("MECHANIC"))
                .andExpect(jsonPath("$.data[0].hourlyRate").value(80.0));
    }
}
