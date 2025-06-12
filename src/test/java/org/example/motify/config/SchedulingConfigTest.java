package org.example.motify.config;

import org.example.motify.Service.WageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulingConfigTest {

    @Mock
    private WageService wageService;

    @InjectMocks
    private SchedulingConfig schedulingConfig;

    @Test
    void monthlyWageSettlement_ShouldCallWageService() {
        // 执行定时任务
        schedulingConfig.monthlyWageSettlement();

        // 计算上个月
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        int year = lastMonth.getYear();
        int month = lastMonth.getMonthValue();

        // 验证是否调用了WageService的calculateMonthlyWages方法
        verify(wageService).calculateMonthlyWages(year, month);
    }

    @Test
    void manualWageSettlement_ShouldCallWageService() {
        // 测试参数
        int year = 2023;
        int month = 6;

        // 执行手动结算
        schedulingConfig.manualWageSettlement(year, month);

        // 验证是否调用了WageService的calculateMonthlyWages方法
        verify(wageService).calculateMonthlyWages(year, month);
    }

    @Test
    void manualWageSettlement_WithInvalidMonth_ShouldThrowException() {
        // 测试无效月份
        try {
            schedulingConfig.manualWageSettlement(2023, 13);
        } catch (IllegalArgumentException e) {
            // 期望抛出异常
            assert (e.getMessage().contains("月份必须在1-12之间"));
        }

        // 验证没有调用WageService
        verify(wageService, never()).calculateMonthlyWages(anyInt(), anyInt());
    }
}