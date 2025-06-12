package org.example.motify.config;

import org.example.motify.Entity.MaintenanceRecord;
import org.example.motify.Entity.Wage;
import org.example.motify.Repository.MaintenanceRecordRepository;
import org.example.motify.Repository.WageRepository;
import org.example.motify.Service.WageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WageInitializerTest {

    @Mock
    private WageService wageService;

    @Mock
    private WageRepository wageRepository;

    @Mock
    private MaintenanceRecordRepository recordRepository;

    @Mock
    private ApplicationArguments args;

    @InjectMocks
    private WageInitializer wageInitializer;

    private MaintenanceRecord oldRecord;
    private Wage existingWage;
    private LocalDate lastMonth;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        oldRecord = new MaintenanceRecord();
        oldRecord.setRecordId(1L);
        oldRecord.setStartTime(LocalDateTime.of(2023, 1, 15, 10, 0));

        MaintenanceRecord newRecord = new MaintenanceRecord();
        newRecord.setRecordId(2L);
        newRecord.setStartTime(LocalDateTime.of(2023, 5, 20, 14, 0));

        existingWage = new Wage();
        existingWage.setId(1L);
        existingWage.setYear(2023);
        existingWage.setMonth(1);

        // 计算上个月
        lastMonth = LocalDate.now().minusMonths(1);

        // 默认启用初始化功能
        ReflectionTestUtils.setField(wageInitializer, "initializeOnStartup", true);
    }

    @Test
    void run_ShouldCalculateWagesForMissingMonths() throws Exception {
        // 模拟最早记录
        when(recordRepository.findTop1ByOrderByStartTimeAsc())
                .thenReturn(Collections.singletonList(oldRecord));

        // 模拟已存在的工资记录
        when(wageRepository.findAll())
                .thenReturn(Collections.singletonList(existingWage));

        // 模拟每个月都有维修记录
        when(recordRepository.countByStartTimeBetween(any(), any()))
                .thenReturn(1L);

        // 执行初始化
        wageInitializer.run(args);

        // 计算应该结算的月份数量（从2023年2月到上个月）
        int expectedMonths = 0;
        LocalDate currentDate = LocalDate.of(2023, 2, 1);
        while (!currentDate.isAfter(lastMonth)) {
            expectedMonths++;
            currentDate = currentDate.plusMonths(1);
        }

        // 验证调用了正确次数的结算方法
        verify(wageService, times(expectedMonths)).calculateMonthlyWages(anyInt(), anyInt());
    }

    @Test
    void run_WithNoRecords_ShouldNotCalculateWages() throws Exception {
        // 模拟没有维修记录
        when(recordRepository.findTop1ByOrderByStartTimeAsc())
                .thenReturn(Collections.emptyList());

        // 执行初始化
        wageInitializer.run(args);

        // 验证没有调用结算方法
        verify(wageService, never()).calculateMonthlyWages(anyInt(), anyInt());
    }

    @Test
    void run_WithAllMonthsCalculated_ShouldNotRecalculate() throws Exception {
        // 模拟最早记录
        when(recordRepository.findTop1ByOrderByStartTimeAsc())
                .thenReturn(Collections.singletonList(oldRecord));

        // 创建所有月份的工资记录
        List<Wage> allWages = new ArrayList<>();
        LocalDate currentDate = LocalDate.of(2023, 1, 1);

        while (!currentDate.isAfter(lastMonth)) {
            Wage wage = new Wage();
            wage.setYear(currentDate.getYear());
            wage.setMonth(currentDate.getMonthValue());
            allWages.add(wage);
            currentDate = currentDate.plusMonths(1);
        }

        // 模拟已存在所有月份的工资记录
        when(wageRepository.findAll()).thenReturn(allWages);

        // 模拟每个月都有维修记录
        lenient().when(recordRepository.countByStartTimeBetween(any(), any()))
                .thenReturn(1L);

        // 执行初始化
        wageInitializer.run(args);

        // 验证没有调用结算方法
        verify(wageService, never()).calculateMonthlyWages(anyInt(), anyInt());
    }

    @Test
    void run_WithInitializeDisabled_ShouldNotCalculateWages() throws Exception {
        // 设置属性为false
        ReflectionTestUtils.setField(wageInitializer, "initializeOnStartup", false);

        // 执行初始化
        wageInitializer.run(args);

        // 验证没有调用结算方法
        verify(wageService, never()).calculateMonthlyWages(anyInt(), anyInt());
        // 验证没有调用repository方法
        verify(recordRepository, never()).findTop1ByOrderByStartTimeAsc();
    }
}