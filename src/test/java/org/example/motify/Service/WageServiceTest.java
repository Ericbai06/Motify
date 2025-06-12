package org.example.motify.Service;

import org.example.motify.Entity.MaintenanceRecord;
import org.example.motify.Entity.Repairman;
import org.example.motify.Entity.Salary;
import org.example.motify.Entity.Wage;
import org.example.motify.Enum.RepairmanType;
import org.example.motify.Repository.MaintenanceRecordRepository;
import org.example.motify.Repository.RepairmanRepository;
import org.example.motify.Repository.WageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WageServiceTest {

    @InjectMocks
    private WageService wageService;

    @Mock
    private RepairmanRepository repairmanRepository;

    @Mock
    private MaintenanceRecordRepository recordRepository;

    @Mock
    private WageRepository wageRepository;

    @Mock
    private Repairman repairman1;

    @Mock
    private Repairman repairman2;

    @Mock
    private Salary salary1;

    @Mock
    private Salary salary2;

    private MaintenanceRecord record1;
    private MaintenanceRecord record2;
    private MaintenanceRecord record3;
    private List<Wage> existingWages;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 配置mock对象的行为
        when(repairman1.getRepairmanId()).thenReturn(1L);
        when(repairman1.getName()).thenReturn("张师傅");
        when(repairman1.getType()).thenReturn(RepairmanType.MECHANIC);
        when(repairman1.getSalary()).thenReturn(salary1);
        when(salary1.getHourlyRate()).thenReturn(80.0f);
        when(repairman1.getHourlyRate()).thenReturn(80.0);

        when(repairman2.getRepairmanId()).thenReturn(2L);
        when(repairman2.getName()).thenReturn("李师傅");
        when(repairman2.getType()).thenReturn(RepairmanType.PAINTER);
        when(repairman2.getSalary()).thenReturn(salary2);
        when(salary2.getHourlyRate()).thenReturn(100.0f);
        when(repairman2.getHourlyRate()).thenReturn(100.0);

        // 创建维修记录
        record1 = new MaintenanceRecord();
        record1.setRecordId(1L);
        record1.setRepairManId(1L);
        record1.setWorkHours(120L); // 2小时
        record1.setStartTime(LocalDateTime.of(2023, 6, 15, 10, 0));

        record2 = new MaintenanceRecord();
        record2.setRecordId(2L);
        record2.setRepairManId(1L);
        record2.setWorkHours(180L); // 3小时
        record2.setStartTime(LocalDateTime.of(2023, 6, 20, 14, 0));

        record3 = new MaintenanceRecord();
        record3.setRecordId(3L);
        record3.setRepairManId(2L);
        record3.setWorkHours(240L); // 4小时
        record3.setStartTime(LocalDateTime.of(2023, 6, 25, 9, 0));

        // 创建现有工资记录
        Wage existingWage = new Wage();
        existingWage.setId(1L);
        existingWage.setRepairmanId(1L);
        existingWage.setYear(2023);
        existingWage.setMonth(6);
        existingWages = new ArrayList<>(Arrays.asList(existingWage));
    }

    @Test
    void calculateMonthlyWages_ShouldCalculateCorrectly() {
        // 配置Mock返回值
        when(repairmanRepository.findAll()).thenReturn(Arrays.asList(repairman1, repairman2));

        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

        when(wageRepository.findByYearAndMonth(2023, 6)).thenReturn(existingWages);
        when(recordRepository.findByStartTimeBetween(startDate, endDate))
                .thenReturn(Arrays.asList(record1, record2, record3));

        // 模拟保存行为
        when(wageRepository.save(any(Wage.class))).thenAnswer(invocation -> {
            Wage wage = invocation.getArgument(0);
            wage.setId(100L); // 设置一个假ID
            return wage;
        });

        // 执行测试
        List<Wage> result = wageService.calculateMonthlyWages(2023, 6);

        // 验证结果
        assertEquals(2, result.size(), "应该有2个工资记录");

        // 验证删除操作
        verify(wageRepository).deleteAll(existingWages);

        // 验证保存操作
        ArgumentCaptor<Wage> wageCaptor = ArgumentCaptor.forClass(Wage.class);
        verify(wageRepository, times(2)).save(wageCaptor.capture());

        List<Wage> capturedWages = wageCaptor.getAllValues();

        // 验证第一个维修人员的工资计算
        Wage wage1 = capturedWages.stream()
                .filter(w -> w.getRepairmanId().equals(1L))
                .findFirst()
                .orElseThrow();

        assertEquals(1L, wage1.getRepairmanId());
        assertEquals(2023, wage1.getYear());
        assertEquals(6, wage1.getMonth());
        assertEquals(5.0, wage1.getTotalWorkHours(), 0.01); // 2小时 + 3小时
        assertEquals(400.0, wage1.getTotalIncome(), 0.01); // 5小时 * 80元/小时
        assertEquals("张师傅", wage1.getRepairmanName());
        assertEquals("MECHANIC", wage1.getRepairmanType());
        assertEquals(80.0, wage1.getHourlyRate(), 0.01);
        verify(repairman1).getRepairmanId(); // 验证获取了ID
        verify(repairman1).getName(); // 验证获取了姓名
        verify(repairman1).getType(); // 验证获取了类型

        // 验证第二个维修人员的工资计算
        Wage wage2 = capturedWages.stream()
                .filter(w -> w.getRepairmanId().equals(2L))
                .findFirst()
                .orElseThrow();

        assertEquals(2L, wage2.getRepairmanId());
        assertEquals(4.0, wage2.getTotalWorkHours(), 0.01); // 4小时
        assertEquals(400.0, wage2.getTotalIncome(), 0.01); // 4小时 * 100元/小时
    }

    @Test
    void calculateMonthlyWages_NoRecords_ShouldReturnEmptyList() {
        // 配置Mock返回值
        when(repairmanRepository.findAll()).thenReturn(Arrays.asList(repairman1, repairman2));

        LocalDateTime startDate = LocalDateTime.of(2023, 7, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

        when(wageRepository.findByYearAndMonth(2023, 7)).thenReturn(new ArrayList<>());
        when(recordRepository.findByStartTimeBetween(startDate, endDate))
                .thenReturn(new ArrayList<>()); // 没有记录

        // 执行测试
        List<Wage> result = wageService.calculateMonthlyWages(2023, 7);

        // 验证结果
        assertTrue(result.isEmpty(), "应该没有工资记录");

        // 验证没有调用save方法
        verify(wageRepository, never()).save(any(Wage.class));
    }
}