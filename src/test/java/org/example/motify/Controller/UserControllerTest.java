package org.example.motify.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.motify.Entity.Car;
import org.example.motify.Entity.MaintenanceRecord;
import org.example.motify.Entity.User;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    void register_success() throws Exception {
        User user = new User();
        user.setUsername("testUser1");
        user.setPassword("password123");
        user.setPhone("13800138001");
        when(userService.register(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/todo/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testUser1"));
    }

    @Test
    void login_success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser2");
        user.setPhone("13800138002");
        when(userService.login(anyString(), anyString())).thenReturn(java.util.Optional.of(user));

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testUser2");
        loginRequest.put("password", "password123");

        mockMvc.perform(post("/api/todo/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testUser2"));
    }

    @Test
    void getUser_success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser3");
        user.setPhone("13800138003");
        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/todo/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testUser3"));
    }

    @Test
    void updateUser_success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser4");
        user.setPhone("13800138004");
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/todo/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testUser4"));
    }

    @Test
    void getUserCars_success() throws Exception {
        Car car1 = new Car();
        car1.setCarId(1L);
        car1.setBrand("Toyota");
        car1.setModel("Camry");
        car1.setLicensePlate("京A12345");

        Car car2 = new Car();
        car2.setCarId(2L);
        car2.setBrand("Honda");
        car2.setModel("Accord");
        car2.setLicensePlate("京B67890");

        List<Car> cars = Arrays.asList(car1, car2);
        when(userService.getUserCars(1L)).thenReturn(cars);

        mockMvc.perform(get("/api/todo/users/1/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].brand").value("Toyota"))
                .andExpect(jsonPath("$.data[1].brand").value("Honda"));
    }

    @Test
    void addCar_success() throws Exception {
        Car car = new Car();
        car.setCarId(1L);
        car.setBrand("Toyota");
        car.setModel("Camry");
        car.setLicensePlate("京A12345");
        when(userService.addCar(anyLong(), any(Car.class))).thenReturn(car);

        mockMvc.perform(post("/api/todo/users/1/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.brand").value("Toyota"));
    }

    @Test
    void getUserMaintenanceRecords_success() throws Exception {
        MaintenanceRecord record = new MaintenanceRecord();
        record.setRecordId(1L);
        record.setDescription("发动机维修");
        record.setProgress(50);

        Car car = new Car();
        car.setCarId(1L);
        car.setBrand("Toyota");
        car.setModel("Camry");
        car.setLicensePlate("京A12345");
        record.setCar(car);

        List<MaintenanceRecord> records = Arrays.asList(record);
        when(userService.getUserMaintenanceRecords(1L)).thenReturn(records);

        mockMvc.perform(get("/api/todo/users/1/maintenance-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].description").value("发动机维修"));
    }

    @Test
    void submitRepairRequest_success() throws Exception {
        MaintenanceRecord record = new MaintenanceRecord();
        record.setRecordId(1L);
        record.setDescription("发动机维修");
        record.setProgress(0);
        record.setStatus(MaintenanceStatus.PENDING);

        Car car = new Car();
        car.setCarId(1L);
        car.setBrand("Toyota");
        car.setModel("Camry");
        car.setLicensePlate("京A12345");
        record.setCar(car);

        when(userService.submitRepairRequest(anyLong(), anyLong(), anyString())).thenReturn(record);

        Map<String, Object> request = new HashMap<>();
        request.put("carId", 1L);
        request.put("description", "发动机维修");

        mockMvc.perform(post("/api/todo/users/1/maintenance-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.description").value("发动机维修"));
    }
}