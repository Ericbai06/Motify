package org.example.motify.Controller;

import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Entity.Car;
import org.example.motify.Entity.User;
import org.example.motify.Enum.RepairmanType;
import org.example.motify.Service.RepairmanService;
import org.example.motify.Exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RepairControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairmanService repairmanService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultRequest(MockMvcRequestBuilders.get("/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(new MediaType(
                                MediaType.APPLICATION_JSON.getType(),
                                MediaType.APPLICATION_JSON.getSubtype(),
                                java.nio.charset.StandardCharsets.UTF_8)))
                .build();
    }

    @Test
    void submitRepairRequest_Success() throws Exception {
        // 准备模拟数据
        MaintenanceItem mockItem = new MaintenanceItem();
        mockItem.setItemId(1L);
        mockItem.setName("Auto Assignment Test");
        mockItem.setDescription("Testing auto assignment of repairmen");

        // 创建Car和User对象
        Car car = new Car();
        car.setCarId(12L);
        User user = new User();
        user.setUserId(10L);
        car.setUser(user);
        mockItem.setCar(car);

        // 准备工种需求Map
        Map<RepairmanType, Integer> requiredTypes = new HashMap<>();
        requiredTypes.put(RepairmanType.MECHANIC, 2);
        requiredTypes.put(RepairmanType.PAINTER, 1);
        requiredTypes.put(RepairmanType.APPRENTICE, 1);

        // 模拟服务方法调用
        when(repairmanService.submitRepairRequest(
                eq(10L), eq(12L),
                eq("Auto Assignment Test"),
                eq("Testing auto assignment of repairmen"),
                any(Map.class))).thenReturn(mockItem);

        // 构建请求JSON
        String json = "{"
                + "\"userId\": 10,"
                + "\"carId\": 12,"
                + "\"name\": \"Auto Assignment Test\","
                + "\"description\": \"Testing auto assignment of repairmen\","
                + "\"requiredTypes\": {"
                + "  \"MECHANIC\": 2,"
                + "  \"PAINTER\": 1,"
                + "  \"APPRENTICE\": 1"
                + "}"
                + "}";

        // 执行测试
        mockMvc.perform(post("/api/repair/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.itemId").value(1L))
                .andExpect(jsonPath("$.data.name").value("Auto Assignment Test"))
                .andExpect(jsonPath("$.data.car.carId").value(12L))
                .andExpect(jsonPath("$.data.car.user.userId").value(10L));
    }

    @Test
    void submitRepairRequest_BadRequest() throws Exception {
        // 模拟服务方法调用，抛出BadRequestException
        when(repairmanService.submitRepairRequest(
                any(Long.class), any(Long.class),
                any(String.class), any(String.class),
                any(Map.class)))
                .thenThrow(new BadRequestException("缺少必要参数"));

        // 构建请求JSON（缺少必要字段）
        String json = "{"
                + "\"userId\": 10,"
                + "\"name\": \"Auto Assignment Test\"" // 缺少carId和requiredTypes
                + "}";

        // 执行测试
        mockMvc.perform(post("/api/repair/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void submitRepairRequest_ResourceNotFound() throws Exception {
        // 模拟服务方法调用，抛出ResourceNotFoundException
        when(repairmanService.submitRepairRequest(
                any(Long.class), any(Long.class),
                any(String.class), any(String.class),
                any(Map.class)))
                .thenThrow(new org.example.motify.Exception.ResourceNotFoundException("无法找到指定的维修人员"));

        // 构建请求JSON
        String json = "{"
                + "\"userId\": 10,"
                + "\"carId\": 12,"
                + "\"name\": \"Auto Assignment Test\","
                + "\"description\": \"Testing auto assignment of repairmen\","
                + "\"requiredTypes\": {"
                + "  \"BODYWORKER\": 5" // 请求过多的车身技师，假设系统中没有这么多
                + "}"
                + "}";

        // 执行测试
        mockMvc.perform(post("/api/repair/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }
}