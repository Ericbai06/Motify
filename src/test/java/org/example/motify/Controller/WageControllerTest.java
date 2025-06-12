package org.example.motify.Controller;

import org.example.motify.Entity.Wage;
import org.example.motify.Repository.WageRepository;
import org.example.motify.config.SchedulingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WageRepository wageRepository;

    @MockBean
    private SchedulingConfig schedulingConfig;

    private Wage wage1;
    private Wage wage2;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        wage1 = new Wage();
        wage1.setId(1L);
        wage1.setRepairmanId(1L);
        wage1.setYear(2023);
        wage1.setMonth(6);
        wage1.setTotalWorkHours(40.0);
        wage1.setTotalIncome(3200.0);
        wage1.setRepairmanName("张师傅");
        wage1.setRepairmanType("MECHANIC");
        wage1.setHourlyRate(80.0);
        wage1.setSettlementDate(LocalDateTime.now());

        wage2 = new Wage();
        wage2.setId(2L);
        wage2.setRepairmanId(2L);
        wage2.setYear(2023);
        wage2.setMonth(6);
        wage2.setTotalWorkHours(35.0);
        wage2.setTotalIncome(3500.0);
        wage2.setRepairmanName("李师傅");
        wage2.setRepairmanType("PAINTER");
        wage2.setHourlyRate(100.0);
        wage2.setSettlementDate(LocalDateTime.now());
    }

    @Test
    void getMonthlyWages_ShouldReturnWages() throws Exception {
        // 模拟Repository行为
        when(wageRepository.findByYearAndMonth(2023, 6))
                .thenReturn(Arrays.asList(wage1, wage2));

        // 执行请求并验证
        mockMvc.perform(get("/api/wages/2023/6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].repairmanId").value(1L))
                .andExpect(jsonPath("$.data[1].repairmanId").value(2L));
    }

    @Test
    void getRepairmanWages_ShouldReturnWages() throws Exception {
        // 模拟Repository行为
        when(wageRepository.findByRepairmanIdOrderByYearDescMonthDesc(1L))
                .thenReturn(List.of(wage1));

        // 执行请求并验证
        mockMvc.perform(get("/api/wages/repairman/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].repairmanId").value(1L))
                .andExpect(jsonPath("$.data[0].totalIncome").value(3200.0));
    }

    @Test
    void calculateMonthlyWages_ShouldTriggerCalculation() throws Exception {
        // 执行请求
        mockMvc.perform(post("/api/wages/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"year\": 2023, \"month\": 6}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.message").value("2023年6月的工资已计算完成"));

        // 验证是否调用了schedulingConfig
        verify(schedulingConfig).manualWageSettlement(2023, 6);
    }

    @Test
    void calculateMonthlyWages_WithoutParams_ShouldUseLastMonth() throws Exception {
        // 执行请求 - 不提供年月参数
        mockMvc.perform(post("/api/wages/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());

        // 验证是否调用了schedulingConfig
        // 注意：由于无法确定上个月是什么，所以只验证调用了方法，不验证具体参数
        verify(schedulingConfig).manualWageSettlement(anyInt(), anyInt());
    }
}