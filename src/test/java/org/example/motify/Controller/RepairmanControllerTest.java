package org.example.motify.Controller;

import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Entity.Repairman;
import org.example.motify.Entity.RepairmanType;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RepairmanControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RepairmanService repairmanService;

    private Repairman repairman;

    @BeforeEach
    void setUp() {
        repairman = new Repairman();
        repairman.setRepairmanId(1L);
        repairman.setUsername("testuser");
        repairman.setPassword("password123");
        repairman.setName("测试工人");
        repairman.setGender("男");
        repairman.setType(RepairmanType.BODYWORKER);
        repairman.setPhone("13800000000");
        repairman.setEmail("test@example.com");
    }

    @Test
    void registerRepairman_Success() throws Exception {
        when(repairmanService.register(any(Repairman.class))).thenReturn(repairman);
        String json = "{" +
                "\"username\":\"testuser\"," +
                "\"password\":\"password123\"," +
                "\"name\":\"测试工人\"," +
                "\"gender\":\"男\"," +
                "\"type\":\"MECHANIC\"," +
                "\"phone\":\"13800000000\"," +
                "\"email\":\"test@example.com\"}";
        mockMvc.perform(post("/api/repairman/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairmanId").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void registerRepairman_BadRequest() throws Exception {
        when(repairmanService.register(any(Repairman.class))).thenThrow(new BadRequestException("参数错误"));
        String json = "{" +
                "\"username\":\"testuser\"," +
                "\"password\":\"password123\"," +
                "\"name\":\"测试工人\"," +
                "\"gender\":\"男\"," +
                "\"type\":\"MECHANIC\"," +
                "\"phone\":\"13800000000\"," +
                "\"email\":\"test@example.com\"}";
        mockMvc.perform(post("/api/repairman/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginRepairman_Success() throws Exception {
        when(repairmanService.login(eq("testuser"), eq("password123"))).thenReturn(java.util.Optional.of(repairman));
        String json = "{" +
                "\"username\":\"testuser\"," +
                "\"password\":\"password123\"}";
        mockMvc.perform(post("/api/repairman/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairmanId").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void loginRepairman_NotFound() throws Exception {
        when(repairmanService.login(eq("testuser"), eq("password123"))).thenReturn(java.util.Optional.empty());
        String json = "{" +
                "\"username\":\"testuser\"," +
                "\"password\":\"password123\"}";
        mockMvc.perform(post("/api/repairman/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void loginRepairman_AuthException() throws Exception {
        when(repairmanService.login(eq("testuser"), eq("password123"))).thenThrow(new org.example.motify.Exception.AuthenticationException("认证失败"));
        String json = "{" +
                "\"username\":\"testuser\"," +
                "\"password\":\"password123\"}";
        mockMvc.perform(post("/api/repairman/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRepairmanInfo_Success() throws Exception {
        when(repairmanService.getRepairmanById(eq(1L))).thenReturn(java.util.Optional.of(repairman));
        String json = "{\"repairmanId\":1}";
        mockMvc.perform(post("/api/repairman/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairmanId").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getRepairmanInfo_NotFound() throws Exception {
        when(repairmanService.getRepairmanById(eq(1L))).thenReturn(java.util.Optional.empty());
        String json = "{\"repairmanId\":1}";
        mockMvc.perform(post("/api/repairman/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRepairman_Success() throws Exception {
        when(repairmanService.updateRepairman(any(Repairman.class))).thenReturn(repairman);
        String json = "{" +
                "\"repairmanId\":1," +
                "\"username\":\"testuser\"," +
                "\"password\":\"password123\"," +
                "\"name\":\"测试工人\"," +
                "\"gender\":\"男\"," +
                "\"type\":\"MECHANIC\"," +
                "\"phone\":\"13800000000\"," +
                "\"email\":\"test@example.com\"}";
        mockMvc.perform(post("/api/repairman/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairmanId").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateRepairman_NotFound() throws Exception {
        when(repairmanService.updateRepairman(any(Repairman.class))).thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
        String json = "{" +
                "\"repairmanId\":1," +
                "\"username\":\"testuser\"," +
                "\"password\":\"password123\"," +
                "\"name\":\"测试工人\"," +
                "\"gender\":\"男\"," +
                "\"type\":\"MECHANIC\"," +
                "\"phone\":\"13800000000\"," +
                "\"email\":\"test@example.com\"}";
        mockMvc.perform(post("/api/repairman/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRepairman_BadRequest() throws Exception {
        when(repairmanService.updateRepairman(any(Repairman.class))).thenThrow(new org.example.motify.Exception.BadRequestException("参数错误"));
        String json = "{" +
                "\"repairmanId\":1," +
                "\"username\":\"testuser\"," +
                "\"password\":\"password123\"," +
                "\"name\":\"测试工人\"," +
                "\"gender\":\"男\"," +
                "\"type\":\"MECHANIC\"," +
                "\"phone\":\"13800000000\"," +
                "\"email\":\"test@example.com\"}";
        mockMvc.perform(post("/api/repairman/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMaintenanceItems_Success() throws Exception {
        java.util.List<MaintenanceItem> items = java.util.Arrays.asList(new MaintenanceItem(), new MaintenanceItem());
        when(repairmanService.getRepairmanMaintenanceItems(eq(1L))).thenReturn(items);
        String json = "{\"repairmanId\":1}";
        mockMvc.perform(post("/api/repairman/maintenance-items/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    void getMaintenanceItems_NotFound() throws Exception {
        when(repairmanService.getRepairmanMaintenanceItems(eq(1L))).thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
        String json = "{\"repairmanId\":1}";
        mockMvc.perform(post("/api/repairman/maintenance-items/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveMaintenanceItem_Success() throws Exception {
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(1L);
        item.setName("测试工单");
        when(repairmanService.saveMaintenanceItem(any(MaintenanceItem.class))).thenReturn(item);
        String json = "{\"itemId\":1,\"name\":\"测试工单\"}";
        mockMvc.perform(post("/api/repairman/maintenance-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(1L))
                .andExpect(jsonPath("$.name").value("测试工单"));
    }

    @Test
    void saveMaintenanceItem_BadRequest() throws Exception {
        when(repairmanService.saveMaintenanceItem(any(MaintenanceItem.class))).thenThrow(new org.example.motify.Exception.BadRequestException("参数错误"));
        String json = "{\"itemId\":1,\"name\":\"测试工单\"}";
        mockMvc.perform(post("/api/repairman/maintenance-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void acceptMaintenanceItem_Success() throws Exception {
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(1L);
        item.setName("测试工单");
        when(repairmanService.acceptMaintenanceItem(eq(1L), eq(1L))).thenReturn(item);
        String json = "{\"repairmanId\":1,\"itemId\":1}";
        mockMvc.perform(post("/api/repairman/maintenance-items/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.itemId").value(1L));
    }

    @Test
    void acceptMaintenanceItem_BadRequest() throws Exception {
        when(repairmanService.acceptMaintenanceItem(eq(1L), eq(1L))).thenThrow(new org.example.motify.Exception.BadRequestException("参数错误"));
        String json = "{\"repairmanId\":1,\"itemId\":1}";
        mockMvc.perform(post("/api/repairman/maintenance-items/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void acceptMaintenanceItem_NotFound() throws Exception {
        when(repairmanService.acceptMaintenanceItem(eq(1L), eq(1L))).thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
        String json = "{\"repairmanId\":1,\"itemId\":1}";
        mockMvc.perform(post("/api/repairman/maintenance-items/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void getCurrentRecords_Success() throws Exception {
        java.util.List<MaintenanceItem> records = java.util.Arrays.asList(new MaintenanceItem(), new MaintenanceItem());
        when(repairmanService.getRepairmanCurrentRecords(eq(1L))).thenReturn(records);
        String json = "{\"repairmanId\":1}";
        mockMvc.perform(post("/api/repairman/current-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    void getCurrentRecords_NotFound() throws Exception {
        when(repairmanService.getRepairmanCurrentRecords(eq(1L))).thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
        String json = "{\"repairmanId\":1}";
        mockMvc.perform(post("/api/repairman/current-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCompletedRecords_Success() throws Exception {
        java.util.List<MaintenanceItem> records = java.util.Arrays.asList(new MaintenanceItem(), new MaintenanceItem());
        when(repairmanService.getRepairmanCompletedRecords(eq(1L))).thenReturn(records);
        String json = "{\"repairmanId\":1}";
        mockMvc.perform(post("/api/repairman/completed-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    void getCompletedRecords_NotFound() throws Exception {
        when(repairmanService.getRepairmanCompletedRecords(eq(1L))).thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
        String json = "{\"repairmanId\":1}";
        mockMvc.perform(post("/api/repairman/completed-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }
}
