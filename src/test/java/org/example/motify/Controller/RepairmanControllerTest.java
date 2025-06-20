package org.example.motify.Controller;

import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Entity.Repairman;
import org.example.motify.Enum.MaintenanceStatus;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RepairmanControllerTest {

        @Autowired
        private MockMvc mockMvc;
        @MockBean
        private RepairmanService repairmanService;
        @Autowired
        private WebApplicationContext webApplicationContext;

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
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.repairmanId").value(1L))
                                .andExpect(jsonPath("$.data.username").value("testuser"));
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
                when(repairmanService.login(eq("testuser"), eq("password123")))
                                .thenReturn(java.util.Optional.of(repairman));
                String json = "{" +
                                "\"username\":\"testuser\"," +
                                "\"password\":\"password123\"}";
                mockMvc.perform(post("/api/repairman/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.repairmanId").value(1L))
                                .andExpect(jsonPath("$.data.username").value("testuser"));
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
                when(repairmanService.login(eq("testuser"), eq("password123")))
                                .thenThrow(new org.example.motify.Exception.AuthenticationException("认证失败"));
                String json = "{" +
                                "\"username\":\"testuser\"," +
                                "\"password\":\"password123\"}";
                mockMvc.perform(post("/api/repairman/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        void getRepairmanInfo_Success() throws Exception {
                when(repairmanService.getRepairmanById(eq(1L))).thenReturn(java.util.Optional.of(repairman));
                String json = "{\"repairmanId\":1}";
                mockMvc.perform(post("/api/repairman/info")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.repairmanId").value(1L))
                                .andExpect(jsonPath("$.data.username").value("testuser"));
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
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.repairmanId").value(1L))
                                .andExpect(jsonPath("$.data.username").value("testuser"));
        }

        @Test
        void updateRepairman_NotFound() throws Exception {
                when(repairmanService.updateRepairman(any(Repairman.class)))
                                .thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
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
                when(repairmanService.updateRepairman(any(Repairman.class)))
                                .thenThrow(new org.example.motify.Exception.BadRequestException("参数错误"));
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
                java.util.List<MaintenanceItem> items = java.util.Arrays.asList(new MaintenanceItem(),
                                new MaintenanceItem());
                when(repairmanService.getRepairmanMaintenanceItems(eq(1L))).thenReturn(items);
                String json = "{\"repairmanId\":1}";
                mockMvc.perform(post("/api/repairman/maintenance-items/list")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(2)));
        }

        @Test
        void getMaintenanceItems_NotFound() throws Exception {
                when(repairmanService.getRepairmanMaintenanceItems(eq(1L)))
                                .thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
                String json = "{\"repairmanId\":1}";
                mockMvc.perform(post("/api/repairman/maintenance-items/list")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isNotFound());
        }


        @Test
        void getCurrentRecords_Success() throws Exception {
                java.util.List<MaintenanceItem> items = java.util.Arrays.asList(new MaintenanceItem(),
                                new MaintenanceItem());
                when(repairmanService.getRepairmanCurrentRecords(eq(1L))).thenReturn(items);
                String json = "{\"repairmanId\":1}";
                mockMvc.perform(post("/api/repairman/current-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(2)));
        }

        @Test
        void getCurrentRecords_NotFound() throws Exception {
                when(repairmanService.getRepairmanCurrentRecords(eq(1L)))
                                .thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
                String json = "{\"repairmanId\":1}";
                mockMvc.perform(post("/api/repairman/current-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isNotFound());
        }

        // @Test
        // void getCompletedRecords_Success() throws Exception {
        //         java.util.List<MaintenanceItem> items = java.util.Arrays.asList(new MaintenanceItem(),
        //                         new MaintenanceItem());
        //         when(repairmanService.getRepairmanCompletedRecords(eq(1L))).thenReturn(items);
        //         String json = "{\"repairmanId\":1}";
        //         mockMvc.perform(post("/api/repairman/completed-records")
        //                         .contentType(MediaType.APPLICATION_JSON)
        //                         .content(json))
        //                         .andExpect(status().isOk())
        //                         .andExpect(jsonPath("$.code").value(200))
        //                         .andExpect(jsonPath("$.data", org.hamcrest.Matchers.hasSize(2)));
        // }

        @Test
        void getCompletedRecords_NotFound() throws Exception {
                when(repairmanService.getRepairmanCompletedRecords(eq(1L)))
                                .thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
                String json = "{\"repairmanId\":1}";
                mockMvc.perform(post("/api/repairman/completed-records")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isNotFound());
        }

        @Test
        void updateMaintenanceProgress_Success() throws Exception {
                MaintenanceItem item = new MaintenanceItem();
                item.setItemId(1L);
                item.setProgress(50);
                when(repairmanService.updateMaintenanceProgress(eq(1L), eq(1L), eq(50), eq("进度更新"))).thenReturn(item);
                String json = "{\"progress\":50,\"description\":\"进度更新\"}";
                mockMvc.perform(put("/api/repairman/1/maintenance-items/1/progress")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.itemId").value(1L))
                                .andExpect(jsonPath("$.data.progress").value(50));
        }

        @Test
        void updateMaintenanceProgress_BadRequest() throws Exception {
                when(repairmanService.updateMaintenanceProgress(eq(1L), eq(1L), eq(150), anyString()))
                                .thenThrow(new BadRequestException("维修进度必须在0-100之间"));

                String json = "{\"progress\":150,\"description\":\"无效进度\"}";
                mockMvc.perform(put("/api/repairman/1/maintenance-items/1/progress")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400))
                                .andExpect(jsonPath("$.message").value("维修进度必须在0-100之间"));
        }

        @Test
        void acceptMaintenanceItem_Success() throws Exception {
                MaintenanceItem item = new MaintenanceItem();
                item.setItemId(1L);
                item.setStatus(MaintenanceStatus.ACCEPTED);
                when(repairmanService.acceptMaintenanceItem(eq(1L), eq(1L))).thenReturn(item);
                String json = "{\"repairmanId\":1,\"itemId\":1}";
                mockMvc.perform(post("/api/repairman/maintenance-items/accept")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.itemId").value(1L))
                                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
        }

        @Test
        void acceptMaintenanceItem_BadRequest() throws Exception {
                when(repairmanService.acceptMaintenanceItem(eq(1L), eq(1L)))
                                .thenThrow(new org.example.motify.Exception.BadRequestException("参数错误"));
                String json = "{\"repairmanId\":1,\"itemId\":1}";
                mockMvc.perform(post("/api/repairman/maintenance-items/accept")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        void acceptMaintenanceItem_NotFound() throws Exception {
                when(repairmanService.acceptMaintenanceItem(eq(1L), eq(1L)))
                                .thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));
                String json = "{\"repairmanId\":1,\"itemId\":1}";
                mockMvc.perform(post("/api/repairman/maintenance-items/accept")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        void rejectMaintenanceItem_Success() throws Exception {
                MaintenanceItem item = new MaintenanceItem();
                item.setItemId(1L);
                item.setStatus(MaintenanceStatus.CANCELLED);
                when(repairmanService.rejectMaintenanceItem(eq(1L), eq(1L), eq("太忙了"))).thenReturn(item);
                String json = "{\"reason\":\"太忙了\"}";
                mockMvc.perform(post("/api/repairman/1/maintenance-items/1/reject")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.itemId").value(1L))
                                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
        }

        @Test
        void completeMaintenanceItem_Success() throws Exception {
                MaintenanceItem item = new MaintenanceItem();
                item.setItemId(1L);
                item.setStatus(MaintenanceStatus.COMPLETED);
                item.setProgress(100);

                Map<String, Object> material = new HashMap<>();
                material.put("materialId", 1L);
                material.put("quantity", 2);
                java.util.List<Map<String, Object>> materials = java.util.Arrays.asList(material);

                when(repairmanService.completeMaintenanceItem(eq(1L), eq(1L), eq("完成维修"), eq(2.0), anyList()))
                                .thenReturn(item);

                String json = "{\"result\":\"完成维修\",\"workingHours\":2.0,\"materials\":[{\"materialId\":1,\"quantity\":2}]}";
                mockMvc.perform(post("/api/repairman/1/maintenance-items/1/complete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.itemId").value(1L))
                                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                                .andExpect(jsonPath("$.data.progress").value(100));
        }

        @Test
        void getIncomeStatistics_Success() throws Exception {
                Map<String, Object> incomeData = new HashMap<>();
                incomeData.put("totalIncome", 1000.0);
                incomeData.put("totalWorkOrders", 5);

                when(repairmanService.calculateIncome(eq(1L), eq("2023-01-01"), eq("2023-12-31")))
                                .thenReturn(incomeData);

                mockMvc.perform(get("/api/repairman/1/income?startDate=2023-01-01&endDate=2023-12-31"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.totalIncome").value(1000.0))
                                .andExpect(jsonPath("$.data.totalWorkOrders").value(5));
        }

        @Test
        void addMaintenanceRecord_Success() throws Exception {
                Map<String, Object> material1 = new HashMap<>();
                material1.put("materialId", 9L);
                material1.put("amount", 1);
                Map<String, Object> material2 = new HashMap<>();
                material2.put("materialId", 10L);
                material2.put("amount", 1);
                java.util.List<Map<String, Object>> materials = java.util.Arrays.asList(material1, material2);

                Map<String, Object> payload = new HashMap<>();
                payload.put("maintenanceItemId", 3L);
                payload.put("description", "补胎");
                payload.put("repairmanId", 1L);
                payload.put("workHours", 1);
                payload.put("startTime", "2024-06-02T10:00:00");
                payload.put("name", "补胎记录");
                payload.put("materials", materials);

                org.example.motify.Entity.MaintenanceRecord record = new org.example.motify.Entity.MaintenanceRecord();
                record.setRecordId(100L);
                record.setName("补胎记录");
                record.setDescription("补胎");
                record.setRepairManId(1L);
                record.setWorkHours(1L);
                record.setStartTime(java.time.LocalDateTime.parse("2024-06-02T10:00:00"));

                when(repairmanService.addMaintenanceRecord(any(Map.class))).thenReturn(record);

                String json = "{" +
                                "\"maintenanceItemId\":3," +
                                "\"description\":\"补胎\"," +
                                "\"repairmanId\":1," +
                                "\"workHours\":1," +
                                "\"startTime\":\"2024-06-02T10:00:00\"," +
                                "\"name\":\"补胎记录\"," +
                                "\"materials\":[{" +
                                "\"materialId\":9,\"amount\":1},{\"materialId\":10,\"amount\":1}]}";

                mockMvc.perform(post("/api/repairman/maintenance-records/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.name").value("补胎记录"))
                                .andExpect(jsonPath("$.data.description").value("补胎"))
                                .andExpect(jsonPath("$.data.repairManId").value(1L));
        }

        @Test
        void addMaintenanceRecord_AutoName() throws Exception {
                Map<String, Object> material1 = new HashMap<>();
                material1.put("materialId", 9L);
                material1.put("amount", 1);
                java.util.List<Map<String, Object>> materials = java.util.Arrays.asList(material1);

                Map<String, Object> payload = new HashMap<>();
                payload.put("maintenanceItemId", 3L);
                payload.put("description", "补胎");
                payload.put("repairmanId", 1L);
                payload.put("workHours", 1);
                payload.put("startTime", "2024-06-02T10:00:00");
                payload.put("materials", materials);

                org.example.motify.Entity.MaintenanceRecord record = new org.example.motify.Entity.MaintenanceRecord();
                record.setRecordId(101L);
                record.setName("补胎-2024-06-02T10:00:00");
                record.setDescription("补胎");
                record.setRepairManId(1L);
                record.setWorkHours(1L);
                record.setStartTime(java.time.LocalDateTime.parse("2024-06-02T10:00:00"));

                when(repairmanService.addMaintenanceRecord(any(Map.class))).thenReturn(record);

                String json = "{" +
                                "\"maintenanceItemId\":3," +
                                "\"description\":\"补胎\"," +
                                "\"repairmanId\":1," +
                                "\"workHours\":1," +
                                "\"startTime\":\"2024-06-02T10:00:00\"," +
                                "\"materials\":[{" +
                                "\"materialId\":9,\"amount\":1}]}";

                mockMvc.perform(post("/api/repairman/maintenance-records/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.name").value("补胎-2024-06-02T10:00:00"))
                                .andExpect(jsonPath("$.data.description").value("补胎"))
                                .andExpect(jsonPath("$.data.repairManId").value(1L));
        }

        @Test
        void acceptMaintenanceItemByPath_Success() throws Exception {
                MaintenanceItem item = new MaintenanceItem();
                item.setItemId(1L);
                item.setStatus(MaintenanceStatus.ACCEPTED);
                when(repairmanService.acceptMaintenanceItem(eq(1L), eq(1L))).thenReturn(item);

                mockMvc.perform(post("/api/repairman/1/maintenance-items/1/accept")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.itemId").value(1L))
                                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
        }

        @Test
        void acceptMaintenanceItemByPath_BadRequest() throws Exception {
                when(repairmanService.acceptMaintenanceItem(eq(1L), eq(1L)))
                                .thenThrow(new org.example.motify.Exception.BadRequestException("参数错误"));

                mockMvc.perform(post("/api/repairman/1/maintenance-items/1/accept")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        void acceptMaintenanceItemByPath_NotFound() throws Exception {
                when(repairmanService.acceptMaintenanceItem(eq(1L), eq(1L)))
                                .thenThrow(new org.example.motify.Exception.ResourceNotFoundException("未找到"));

                mockMvc.perform(post("/api/repairman/1/maintenance-items/1/accept")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.code").value(404));
        }
}
