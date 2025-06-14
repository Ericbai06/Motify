package org.example.motify.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.motify.Entity.*;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Enum.RepairmanType;
import org.example.motify.Enum.MaterialType;
import org.example.motify.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MaintenanceWorkflowIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CarRepository carRepository;

        @Autowired
        private MaintenanceItemRepository maintenanceItemRepository;

        @Autowired
        private RepairmanRepository repairmanRepository;

        @Autowired
        private RequiredRepairmanTypeRepository requiredTypeRepository;

        @Autowired
        private MaterialRepository materialRepository;

        @Autowired
        private MaintenanceRecordRepository recordRepository;

        @Autowired
        private SalaryRepository salaryRepository;

        @BeforeEach
        public void setUp() {
                // 检查并初始化salaries表数据
                initializeSalariesIfNeeded();
        }

        private void initializeSalariesIfNeeded() {
                // 为每种维修工种创建工资标准记录
                for (RepairmanType type : RepairmanType.values()) {
                        if (!salaryExistsForType(type.name())) {
                                Salary salary = new Salary();
                                salary.setType(type);
                                salary.setHourlyRate(getDefaultHourlyRate(type));
                                salaryRepository.save(salary);
                        }
                }
        }

        private float getDefaultHourlyRate(RepairmanType type) {
                switch (type) {
                        case MECHANIC:
                                return 80.0f;
                        case ELECTRICIAN:
                                return 60.0f;
                        case PAINTER:
                                return 100.0f;
                        default:
                                return 50.0f;
                }
        }

        private boolean salaryExistsForType(String typeName) {
                try {
                        return salaryRepository.existsByType(typeName);
                } catch (ClassCastException e) {
                        // 捕获类型转换异常，使用替代方法
                        return salaryRepository.findAll().stream()
                                        .anyMatch(s -> s.getType().name().equals(typeName));
                }
        }

        @Test
        public void testCompleteMaintenanceWorkflow() throws Exception {
                // 1. 准备测试数据
                // 1.1 创建用户和车辆
                User user = createTestUser();
                Car car = createTestCar(user);

                // 1.2 创建维修人员
                Repairman mechanic1 = createTestRepairman("mechanic1", "机械师1", RepairmanType.MECHANIC);
                Repairman mechanic2 = createTestRepairman("mechanic2", "机械师2", RepairmanType.MECHANIC);
                Repairman electrician = createTestRepairman("electrician", "电工", RepairmanType.ELECTRICIAN);

                // 1.3 创建测试材料
                Material oil = findOrCreateMaterial("机油", 100.0);
                Material filter = findOrCreateMaterial("机油滤芯", 50.0);
                Material spark = findOrCreateMaterial("火花塞", 30.0);

                // 2. 用户提交维修项目
                Map<String, Object> maintenanceRequest = new HashMap<>();
                maintenanceRequest.put("userId", user.getUserId());
                maintenanceRequest.put("carId", car.getCarId());
                maintenanceRequest.put("name", "发动机保养");
                maintenanceRequest.put("description", "更换机油、机滤和火花塞");

                // 添加工种需求
                Map<String, Integer> requiredTypes = new HashMap<>();
                requiredTypes.put("MECHANIC", 2); // 需要2名机械师
                requiredTypes.put("ELECTRICIAN", 1); // 需要1名电工
                maintenanceRequest.put("requiredTypes", requiredTypes);

                MvcResult result = mockMvc.perform(post("/api/repair/submit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(maintenanceRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andReturn();

                // 解析返回的工单ID
                String responseContent = result.getResponse().getContentAsString();
                @SuppressWarnings("unchecked")
                Map<String, Object> responseMap = objectMapper.readValue(responseContent, Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
                Integer itemId = (Integer) dataMap.get("itemId");

                // 3. 管理员审核并分配工种和人数
                // 3.1 查询工单详情
                MaintenanceItem item = maintenanceItemRepository.findById(itemId.longValue()).orElseThrow();
                assertEquals(MaintenanceStatus.PENDING, item.getStatus());

                // 3.2 验证工种需求是否正确创建
                List<RequiredRepairmanType> requirements = requiredTypeRepository
                                .findByMaintenanceItem_ItemId(itemId.longValue());
                assertEquals(2, requirements.size());

                // 4. 一个维修人员拒绝工单
                // 假设mechanic1拒绝工单
                mockMvc.perform(post("/api/repairman/{repairmanId}/maintenance-items/{itemId}/reject",
                                mechanic1.getRepairmanId(), itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "reason", "工作量过大"))))
                                .andExpect(status().isOk());

                // 5. 验证系统是否自动分配下一个维修人员
                item = maintenanceItemRepository.findById(itemId.longValue()).orElseThrow();
                assertEquals(MaintenanceStatus.AWAITING_ASSIGNMENT, item.getStatus());

                // 验证mechanic1已被标记为拒绝
                mockMvc.perform(post("/api/repairman/rejected-items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "repairmanId", mechanic1.getRepairmanId()))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));

                // 6. 剩余维修人员接受工单
                // mechanic2接受工单
                mockMvc.perform(post("/api/repairman/maintenance-items/accept")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "repairmanId", mechanic2.getRepairmanId(),
                                                "itemId", itemId))))
                                .andExpect(status().isOk());

                // electrician接受工单
                mockMvc.perform(post("/api/repairman/maintenance-items/accept")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "repairmanId", electrician.getRepairmanId(),
                                                "itemId", itemId))))
                                .andExpect(status().isOk());

                // 7. 验证工单状态是否更新为IN_PROGRESS
                item = maintenanceItemRepository.findById(itemId.longValue()).orElseThrow();
                assertEquals(MaintenanceStatus.IN_PROGRESS, item.getStatus());

                // 8. 更新维修进度
                // mechanic2更新进度
                mockMvc.perform(put("/api/repairman/{repairmanId}/maintenance-items/{itemId}/progress",
                                mechanic2.getRepairmanId(), itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "progress", 30,
                                                "description", "已更换机油"))))
                                .andExpect(status().isOk());

                // 9. 添加维修记录和材料使用
                // mechanic2添加维修记录
                Map<String, Object> recordRequest = new HashMap<>();
                recordRequest.put("maintenanceItemId", itemId);
                recordRequest.put("repairmanId", mechanic2.getRepairmanId());
                recordRequest.put("name", "更换机油和滤芯");
                recordRequest.put("description", "使用5W-30全合成机油和原厂滤芯");
                recordRequest.put("workHours", 60); // 60分钟
                recordRequest.put("startTime", new Date().toInstant().toString());

                // 添加材料使用信息
                List<Map<String, Object>> materials = new ArrayList<>();
                materials.add(Map.of("materialId", oil.getMaterialId(), "amount", 1));
                materials.add(Map.of("materialId", filter.getMaterialId(), "amount", 1));
                recordRequest.put("materials", materials);

                mockMvc.perform(post("/api/repairman/maintenance-records/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(recordRequest)))
                                .andExpect(status().isOk());

                // electrician添加维修记录
                recordRequest = new HashMap<>();
                recordRequest.put("maintenanceItemId", itemId);
                recordRequest.put("repairmanId", electrician.getRepairmanId());
                recordRequest.put("name", "更换火花塞");
                recordRequest.put("description", "更换NGK铱金火花塞");
                recordRequest.put("workHours", 30); // 30分钟
                recordRequest.put("startTime", new Date().toInstant().toString());

                // 添加材料使用信息
                materials = new ArrayList<>();
                materials.add(Map.of("materialId", spark.getMaterialId(), "amount", 4));
                recordRequest.put("materials", materials);

                mockMvc.perform(post("/api/repairman/maintenance-records/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(recordRequest)))
                                .andExpect(status().isOk());

                // 10. 更新进度至75%
                mockMvc.perform(put("/api/repairman/{repairmanId}/maintenance-items/{itemId}/progress",
                                electrician.getRepairmanId(), itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                                "progress", 75,
                                                "description", "已完成火花塞更换"))))
                                .andExpect(status().isOk());

                // 11. 完成维修工单
                Map<String, Object> completeRequest = new HashMap<>();
                completeRequest.put("result", "发动机保养完成，运行正常");
                completeRequest.put("workingHours", 1.5); // 1.5小时

                mockMvc.perform(post("/api/repairman/{repairmanId}/maintenance-items/{itemId}/complete",
                                mechanic2.getRepairmanId(), itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(completeRequest)))
                                .andExpect(status().isOk());

                // 12. 验证工单状态是否更新为COMPLETED
                item = maintenanceItemRepository.findById(itemId.longValue()).orElseThrow();
                assertEquals(MaintenanceStatus.COMPLETED, item.getStatus());
                assertEquals(100, item.getProgress());
                assertNotNull(item.getCompleteTime());

                // 13. 验证费用计算是否正确
                // 材料费: 机油(100) + 滤芯(50) + 火花塞(30*4=120) = 270
                // 工时费: 机械师(1.5小时) + 电工(0.5小时) = 机械师时薪*1.5 + 电工时薪*0.5
                double expectedMaterialCost = 270.0;
                assertNotNull(item.getMaterialCost());
                assertEquals(expectedMaterialCost, item.getMaterialCost(), 0.01);

                // 14. 验证维修记录是否正确保存
                List<MaintenanceRecord> records = recordRepository.findByMaintenanceItem_ItemId(itemId.longValue());
                assertEquals(2, records.size());
        }

        // 辅助方法: 创建测试用户
        private User createTestUser() {
                User user = new User();
                user.setUsername("testuser_" + System.currentTimeMillis());
                user.setName("测试用户");
                user.setPassword("password");
                return userRepository.save(user);
        }

        // 辅助方法: 创建测试车辆
        private Car createTestCar(User user) {
                Car car = new Car();
                car.setUser(user);
                car.setBrand("测试品牌");
                car.setModel("测试型号");
                car.setLicensePlate("测试车牌" + System.currentTimeMillis());
                return carRepository.save(car);
        }

        // 辅助方法: 创建测试维修人员
        private Repairman createTestRepairman(String username, String name, RepairmanType type) {
                // 检查是否已存在
                Optional<Repairman> existing = repairmanRepository.findByUsername(username);
                if (existing.isPresent()) {
                        return existing.get();
                }

                Repairman repairman = new Repairman();
                repairman.setUsername(username);
                repairman.setName(name);
                repairman.setPassword("password");
                repairman.setGender("男");
                repairman.setType(type);

                // 设置时薪
                switch (type) {
                        case MECHANIC:
                                repairman.setHourlyRate(80.0f);
                                break;
                        case ELECTRICIAN:
                                repairman.setHourlyRate(60.0f);
                                break;
                        case PAINTER:
                                repairman.setHourlyRate(100.0f);
                                break;
                        default:
                                repairman.setHourlyRate(50.0f);
                }

                return repairmanRepository.save(repairman);
        }

        // 辅助方法: 查找或创建材料
        private Material findOrCreateMaterial(String name, double price) {
                // 使用 findAll() 然后手动过滤，或者在 MaterialRepository 中添加 findByName 方法
                List<Material> materials = materialRepository.findAll().stream()
                                .filter(m -> m.getName().equals(name))
                                .toList();

                if (!materials.isEmpty()) {
                        return materials.get(0);
                }

                Material material = new Material();
                material.setName(name);
                material.setPrice(price);
                material.setStock(100);

                // 使用 MaterialType 枚举而非字符串
                MaterialType type;
                if (name.contains("机油")) {
                        type = MaterialType.OIL;
                } else if (name.contains("滤芯")) {
                        type = MaterialType.FILTER;
                } else if (name.contains("火花塞")) {
                        type = MaterialType.ELECTRICAL;
                } else {
                        type = MaterialType.OTHER;
                }
                material.setType(type);

                return materialRepository.save(material);
        }
}
