package org.example.motify.Controller;

import org.example.motify.Entity.*;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Enum.RepairmanType;
import org.example.motify.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AutoAssignmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RepairmanRepository repairmanRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private MaintenanceItemRepository maintenanceItemRepository;

    @Autowired
    private RequiredRepairmanTypeRepository requiredTypeRepository;

    private User testUser;
    private Car testCar;
    private Repairman mechanic1, mechanic2, painter, apprentice;

    @BeforeEach
    public void setup() {
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setName("Test User");
        testUser.setPhone("13800000000");
        userRepository.save(testUser);

        // 创建测试车辆
        testCar = new Car();
        testCar.setBrand("Test Brand");
        testCar.setModel("Test Model");
        testCar.setLicensePlate("Test123");
        testCar.setUser(testUser);
        carRepository.save(testCar);

        // 创建不同工种维修人员
        // 两个机械师，一个有更多工作量
        mechanic1 = createRepairman("mechanic1", RepairmanType.MECHANIC, 0);
        mechanic2 = createRepairman("mechanic2", RepairmanType.MECHANIC, 2);

        // 一个喷漆师
        painter = createRepairman("painter1", RepairmanType.PAINTER, 0);

        // 一个学徒
        apprentice = createRepairman("apprentice1", RepairmanType.APPRENTICE, 0);
    }

    private Repairman createRepairman(String username, RepairmanType type, int workload) {
        Repairman repairman = new Repairman();
        repairman.setUsername(username);
        repairman.setPassword("password");
        repairman.setName(username);
        repairman.setType(type);
        repairman.setPhone("138" + username);
        repairman.setHourlyRate(50.0f);
        repairman.setGender("男");

        // 保存维修人员
        repairmanRepository.save(repairman);

        // 如果需要设置工作量，创建一些进行中的维修工单
        if (workload > 0) {
            for (int i = 0; i < workload; i++) {
                MaintenanceItem item = new MaintenanceItem();
                item.setName("Workload Item " + i);
                item.setDescription("Test workload");
                item.setStatus(MaintenanceStatus.IN_PROGRESS);
                item.setProgress(50);
                item.setCar(testCar);
                item.setCost(100.0);
                item.setCreateTime(java.time.LocalDateTime.now());

                // 设置维修人员
                // item.addRepairman(repairman, true);
                maintenanceItemRepository.acceptRepairman(item.getItemId(), repairman.getRepairmanId());
                maintenanceItemRepository.save(item);
            }
        }

        return repairman;
    }

    @Test
    public void testAutoAssignment() throws Exception {
        // 1. 创建工单请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userId", testUser.getUserId());
        requestBody.put("carId", testCar.getCarId());
        requestBody.put("name", "Auto Assignment Test");
        requestBody.put("description", "Testing auto assignment of repairmen");

        // 工种需求：2名机械师，1名喷漆工，1名学徒
        Map<String, Integer> requiredTypes = new HashMap<>();
        requiredTypes.put("MECHANIC", 2);
        requiredTypes.put("PAINTER", 1);
        requiredTypes.put("APPRENTICE", 1);
        requestBody.put("requiredTypes", requiredTypes);

        // 2. 提交工单
        MvcResult result = mockMvc.perform(post("/api/repair/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andReturn();

        // 3. 解析结果
        String responseJson = result.getResponse().getContentAsString();
        Map<String, Object> response = objectMapper.readValue(responseJson, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        Integer itemId = (Integer) data.get("itemId");

        // 4. 获取工单并验证分配
        MaintenanceItem savedItem = maintenanceItemRepository.findById(Long.valueOf(itemId)).get();

        // 验证共分配了4名维修人员
        assertThat(savedItem.getRepairmen()).hasSize(4);

        // 验证分配了2个机械师，并且机械师1（工作量少）被优先分配
        List<Long> repairmanIds = savedItem.getRepairmen().stream()
                .map(Repairman::getRepairmanId)
                .toList();

        // 打印信息用于调试
        System.out.println("测试用例维修人员ID: mechanic1=" + mechanic1.getRepairmanId() +
                ", mechanic2=" + mechanic2.getRepairmanId() +
                ", painter=" + painter.getRepairmanId() +
                ", apprentice=" + apprentice.getRepairmanId());
        System.out.println("实际分配的维修人员ID: " + repairmanIds);
        for (Repairman r : savedItem.getRepairmen()) {
            System.out.println(" - " + r.getRepairmanId() + " " + r.getUsername() + " " + r.getType());
        }

        // 验证mechanic1被分配
        assertThat(repairmanIds.contains(mechanic1.getRepairmanId())).isTrue();

        // 验证分配了画家
        assertThat(repairmanIds.contains(painter.getRepairmanId())).isTrue();

        // 验证分配了学徒
        assertThat(repairmanIds.contains(apprentice.getRepairmanId())).isTrue();

        // 验证分配了两个机械师（不一定是mechanic2，可能是系统中已有的其他机械师）
        long mechanicCount = savedItem.getRepairmen().stream()
                .filter(r -> r.getType() == RepairmanType.MECHANIC)
                .count();
        assertThat(mechanicCount).isEqualTo(2);

        // 5. 测试拒绝和重新分配
        // 先创建另一个机械师
        Repairman mechanic3 = createRepairman("mechanic3", RepairmanType.MECHANIC, 1);

        // mechanic1拒绝工单
        Map<String, Object> rejectRequest = new HashMap<>();
        rejectRequest.put("reason", "Too busy");

        mockMvc.perform(put("/api/repairman/" + mechanic1.getRepairmanId() + "/reject/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rejectRequest)))
                .andExpect(status().isOk());

        // 验证mechanic1被移除
        savedItem = maintenanceItemRepository.findById(Long.valueOf(itemId)).get();
        repairmanIds = savedItem.getRepairmen().stream()
                .map(Repairman::getRepairmanId)
                .toList();

        // 打印拒绝后的维修人员信息
        System.out.println("拒绝后的维修人员ID: " + repairmanIds);
        for (Repairman r : savedItem.getRepairmen()) {
            System.out.println(" - " + r.getRepairmanId() + " " + r.getUsername() + " " + r.getType());
        }

        // 验证mechanic1被移除
        assertThat(repairmanIds.contains(mechanic1.getRepairmanId())).isFalse();

        // 验证机械师数量仍然为1（被移除一个）
        mechanicCount = savedItem.getRepairmen().stream()
                .filter(r -> r.getType() == RepairmanType.MECHANIC)
                .count();
        assertThat(mechanicCount).isEqualTo(1);

        // 验证总维修人员数量变为3（移除了一个机械师）
        assertThat(savedItem.getRepairmen()).hasSize(3);
    }
}
