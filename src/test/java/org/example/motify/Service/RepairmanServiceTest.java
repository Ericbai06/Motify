package org.example.motify.Service;

import org.example.motify.Entity.*;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Enum.RepairmanType;
import org.example.motify.Repository.*;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.example.motify.util.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairmanServiceTest {

    @Mock
    private RepairmanRepository repairmanRepository;

    @Mock
    private MaintenanceItemRepository maintenanceItemRepository;

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Mock
    private RecordMaterialRepository recordMaterialRepository;

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private RequiredRepairmanTypeRepository requiredTypeRepository;

    @Mock
    private MaterialService materialService;

    @InjectMocks
    private RepairmanService repairmanService;

    private Repairman testRepairman;
    private MaintenanceItem testMaintenanceItem;
    private Salary testSalary;

    @BeforeEach
    void setUp() {
        testRepairman = new Repairman();
        testRepairman.setRepairmanId(1L);
        testRepairman.setUsername("testRepairman");
        testRepairman.setPassword(PasswordEncoder.encode("password123"));
        testRepairman.setName("Test Repairman");
        testRepairman.setPhone("1234567890");
        testRepairman.setGender("男");
        testRepairman.setType(RepairmanType.MECHANIC);

        testSalary = new Salary();
        testSalary.setType(RepairmanType.MECHANIC);
        testSalary.setHourlyRate(80.0f);

        testMaintenanceItem = new MaintenanceItem();
        testMaintenanceItem.setItemId(1L);
        testMaintenanceItem.setName("Test Item");
        testMaintenanceItem.setDescription("Test Description");
        testMaintenanceItem.setStatus(MaintenanceStatus.PENDING);
        testMaintenanceItem.setProgress(0);
        testMaintenanceItem.setResult("Pending");
        testMaintenanceItem.setCost(100.0);
    }

    @Test
    void register_Success() {
        when(repairmanRepository.existsByUsername(anyString())).thenReturn(false);
        when(repairmanRepository.save(any(Repairman.class))).thenReturn(testRepairman);
        when(salaryRepository.findByType(any(RepairmanType.class))).thenReturn(testSalary);

        Repairman result = repairmanService.register(testRepairman);

        assertNotNull(result);
        assertEquals(testRepairman.getUsername(), result.getUsername());
        verify(repairmanRepository, times(2)).save(any(Repairman.class));
        verify(salaryRepository).findByType(any(RepairmanType.class));
    }

    @Test
    void register_DuplicateUsername() {
        when(repairmanRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            repairmanService.register(testRepairman);
        });

        verify(repairmanRepository).existsByUsername(anyString());
        verifyNoMoreInteractions(repairmanRepository);
        verifyNoInteractions(salaryRepository);
    }

    @Test
    void login_Success() {
        when(repairmanRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(testRepairman));

        Optional<Repairman> result = repairmanService.login("testRepairman", "password123");
        assertTrue(result.isPresent());
        assertEquals("testRepairman", result.get().getUsername());
    }

    @Test
    void login_WrongPassword() {
        when(repairmanRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(testRepairman));

        assertThrows(AuthenticationException.class,
                () -> repairmanService.login("testRepairman", "wrongPassword"));
    }

    @Test
    void getRepairmanById_Success() {
        when(repairmanRepository.findById(anyLong())).thenReturn(Optional.of(testRepairman));
        Optional<Repairman> result = repairmanService.getRepairmanById(1L);
        assertTrue(result.isPresent());
        assertEquals(testRepairman.getRepairmanId(), result.get().getRepairmanId());
    }

    @Test
    void getRepairmanById_NotFound() {
        when(repairmanRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            repairmanService.getRepairmanById(1L);
        });
    }

    // @Test
    // void updateMaintenanceItem_Success() {
    // lenient().when(maintenanceItemRepository.findById(anyLong())).thenReturn(Optional.of(testMaintenanceItem));
    // lenient().when(maintenanceItemRepository.save(any(MaintenanceItem.class))).thenReturn(testMaintenanceItem);
    // MaintenanceItem result = repairmanService.updateMaintenanceItem(1L,
    // testMaintenanceItem);
    // assertNotNull(result);
    // assertEquals(testMaintenanceItem.getItemId(), result.getItemId());
    // }

    // @Test
    // void getRepairmanCurrentRecords_Success() {
    // testRepairman.setMaintenanceItems(Collections.singletonList(testMaintenanceItem));
    // lenient().when(repairmanRepository.findById(anyLong())).thenReturn(Optional.of(testRepairman));
    // List<MaintenanceItem> results =
    // repairmanService.getRepairmanCurrentRecords(1L);
    // assertNotNull(results);
    // assertFalse(results.isEmpty());
    // assertEquals(1, results.size());
    // }

    // @Test
    // void calculateTotalIncome_Success() {
    // testRepairman.setMaintenanceItems(Collections.singletonList(testMaintenanceItem));
    // lenient().when(repairmanRepository.findById(anyLong())).thenReturn(Optional.of(testRepairman));
    // org.example.motify.Entity.RecordInfo recordInfo = new
    // org.example.motify.Entity.RecordInfo();
    // recordInfo.setTotalAmount(100.0);
    // if (testMaintenanceItem.getRecordInfos() == null) {
    // testMaintenanceItem.setRecordInfos(new ArrayList<>());
    // }
    // testMaintenanceItem.getRecordInfos().add(recordInfo);
    // testMaintenanceItem.setProgress(100);
    // double totalIncome = repairmanService.calculateTotalIncome(1L);
    // assertEquals(100.0, totalIncome);
    // }

    @Test
    void acceptMaintenanceItem_Success() {
        testMaintenanceItem.setStatus(null); // 未被接收
        testMaintenanceItem.setRepairmen(new ArrayList<>());
        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));
        when(maintenanceItemRepository.findById(1L)).thenReturn(Optional.of(testMaintenanceItem));
        when(maintenanceItemRepository.save(any(MaintenanceItem.class))).thenReturn(testMaintenanceItem);

        MaintenanceItem result = repairmanService.acceptMaintenanceItem(1L, 1L);
        assertNotNull(result);
        assertEquals(MaintenanceStatus.ACCEPTED, result.getStatus());
        assertEquals(0, result.getProgress());
        assertTrue(result.getRepairmen().contains(testRepairman));
    }

    @Test
    void acceptMaintenanceItem_AlreadyAccepted() {
        testMaintenanceItem.setStatus(MaintenanceStatus.ACCEPTED);
        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));
        when(maintenanceItemRepository.findById(1L)).thenReturn(Optional.of(testMaintenanceItem));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> repairmanService.acceptMaintenanceItem(1L, 1L));
        assertEquals("工单已被其他维修人员接收", ex.getMessage());
    }

    @Test
    void acceptMaintenanceItem_RepairmanNotFound() {
        when(repairmanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> repairmanService.acceptMaintenanceItem(1L, 1L));

        verify(repairmanRepository).findById(1L);
        verifyNoInteractions(maintenanceItemRepository);
    }

    @Test
    void acceptMaintenanceItem_ItemNotFound() {
        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));
        when(maintenanceItemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> repairmanService.acceptMaintenanceItem(1L, 1L));
    }

    @Test
    void rejectMaintenanceItem_Success() {
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(1L);
        item.setStatus(MaintenanceStatus.PENDING);

        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));
        when(maintenanceItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(maintenanceItemRepository.save(any(MaintenanceItem.class))).thenAnswer(i -> i.getArgument(0));

        MaintenanceItem result = repairmanService.rejectMaintenanceItem(1L, 1L, "太忙了");

        assertNotNull(result);
        assertEquals(MaintenanceStatus.CANCELLED, result.getStatus());
        assertEquals("太忙了", result.getResult());
        verify(maintenanceItemRepository).save(any(MaintenanceItem.class));
    }

    @Test
    void rejectMaintenanceItem_InvalidStatus() {
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(1L);
        item.setStatus(MaintenanceStatus.IN_PROGRESS);

        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));
        when(maintenanceItemRepository.findById(1L)).thenReturn(Optional.of(item));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> repairmanService.rejectMaintenanceItem(1L, 1L, "太忙了"));

        assertEquals("工单当前状态不允许拒绝", ex.getMessage());
    }

    @Test
    void updateMaintenanceProgress_Success() {
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(1L);
        item.setStatus(MaintenanceStatus.IN_PROGRESS);
        item.setDescription("初始描述");
        List<Repairman> repairmen = new ArrayList<>();
        repairmen.add(testRepairman);
        item.setRepairmen(repairmen);

        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));
        when(maintenanceItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(maintenanceItemRepository.save(any(MaintenanceItem.class))).thenAnswer(i -> i.getArgument(0));

        MaintenanceItem result = repairmanService.updateMaintenanceProgress(1L, 1L, 75, "更新进度");

        assertNotNull(result);
        assertEquals(75, result.getProgress());
        assertTrue(result.getDescription().contains("更新进度"));
        verify(maintenanceItemRepository).save(any(MaintenanceItem.class));
    }

    @Test
    void updateMaintenanceProgress_NotBelongToRepairman() {
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(1L);
        item.setRepairmen(new ArrayList<>());

        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));
        when(maintenanceItemRepository.findById(1L)).thenReturn(Optional.of(item));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> repairmanService.updateMaintenanceProgress(1L, 1L, 50, "更新进度"));

        assertEquals("该工单不属于此维修人员", ex.getMessage());
    }

    @Test
    void completeMaintenanceItem_Success() {
        // 准备测试数据
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(1L);
        item.setName("测试维修项目");
        List<Repairman> repairmen = new ArrayList<>();
        repairmen.add(testRepairman);
        item.setRepairmen(repairmen);

        Material material = new Material();
        material.setMaterialId(1L);
        material.setName("测试材料");
        material.setPrice(50.0);
        material.setStock(10);

        // 设置模拟
        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));
        when(maintenanceItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));
        when(materialRepository.save(any(Material.class))).thenAnswer(i -> i.getArgument(0));
        when(maintenanceItemRepository.save(any(MaintenanceItem.class))).thenAnswer(i -> i.getArgument(0));
        when(maintenanceRecordRepository.save(any(MaintenanceRecord.class))).thenAnswer(invocation -> {
            MaintenanceRecord record = invocation.getArgument(0);
            record.setRecordId(1L);
            return record;
        });
        when(recordMaterialRepository.save(any(RecordMaterial.class))).thenAnswer(i -> i.getArgument(0));

        // 设置测试数据
        String result = "维修完成";
        Double workingHours = 2.0;
        List<Map<String, Object>> materialsUsed = new ArrayList<>();
        Map<String, Object> materialMap = new HashMap<>();
        materialMap.put("materialId", 1L);
        materialMap.put("quantity", 2);
        materialsUsed.add(materialMap);

        // 执行测试
        MaintenanceItem completedItem = repairmanService.completeMaintenanceItem(1L, 1L, result, workingHours,
                materialsUsed);

        // 验证结果
        assertNotNull(completedItem);
        assertEquals(MaintenanceStatus.COMPLETED, completedItem.getStatus());
        assertEquals(100, completedItem.getProgress());
        assertEquals(result, completedItem.getResult());
        verify(maintenanceRecordRepository).save(any(MaintenanceRecord.class));
        verify(recordMaterialRepository).save(any(RecordMaterial.class));
    }

    @Test
    void calculateIncome_Success() {
        // 准备数据
        MaintenanceItem item1 = new MaintenanceItem();
        item1.setItemId(1L);
        item1.setStatus(MaintenanceStatus.COMPLETED);
        item1.setLaborCost(160.0);
        item1.setCompleteTime(LocalDateTime.now());

        MaintenanceItem item2 = new MaintenanceItem();
        item2.setItemId(2L);
        item2.setStatus(MaintenanceStatus.COMPLETED);
        item2.setLaborCost(240.0);
        item2.setCompleteTime(LocalDateTime.now());

        List<MaintenanceItem> items = Arrays.asList(item1, item2);
        testRepairman.setMaintenanceItems(items);
        testRepairman.setType(RepairmanType.MECHANIC);

        // 模拟
        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));

        // 执行
        Map<String, Object> result = repairmanService.calculateIncome(1L, null, null);

        // 验证
        assertNotNull(result);
        assertEquals(400.0, result.get("totalIncome"));
        assertEquals(2, result.get("totalWorkOrders"));
        assertEquals(RepairmanType.MECHANIC, result.get("repairmanType"));
    }

    // 测试初始化默认薪资标准
    @Test
    void initializeDefaultSalaries_Success() {
        doNothing().when(salaryRepository).insertDefaultSalaries();

        repairmanService.initializeDefaultSalaries();

        verify(salaryRepository).insertDefaultSalaries();
    }

    // 测试根据工种类型获取薪资标准
    @Test
    void getSalaryByType_Success() {
        when(salaryRepository.findByType(RepairmanType.MECHANIC)).thenReturn(testSalary);

        Salary result = repairmanService.getSalaryByType(RepairmanType.MECHANIC);

        assertNotNull(result);
        assertEquals(RepairmanType.MECHANIC, result.getType());
        assertEquals(80.0f, result.getHourlyRate());
        verify(salaryRepository).findByType(RepairmanType.MECHANIC);
    }

    // 测试设置维修人员薪资标准
    @Test
    void setRepairmanSalary_Success() {
        when(repairmanRepository.findById(1L)).thenReturn(Optional.of(testRepairman));
        when(salaryRepository.findByType(RepairmanType.ELECTRICIAN)).thenReturn(new Salary() {
            {
                setType(RepairmanType.ELECTRICIAN);
                setHourlyRate(60.0f);
            }
        });
        when(repairmanRepository.save(any(Repairman.class))).thenReturn(testRepairman);

        Repairman result = repairmanService.setRepairmanSalary(1L, RepairmanType.ELECTRICIAN);

        assertNotNull(result);
        assertEquals(RepairmanType.ELECTRICIAN, testRepairman.getType());
        verify(repairmanRepository).findById(1L);
        verify(salaryRepository).findByType(RepairmanType.ELECTRICIAN);
        verify(repairmanRepository).save(testRepairman);
    }

    // 测试更新工种薪资标准
    @Test
    void updateSalaryStandard_Success() {
        String typeStr = RepairmanType.MECHANIC.name();
        when(salaryRepository.existsByType(typeStr)).thenReturn(true);
        when(salaryRepository.updateSalary(eq(typeStr), anyFloat())).thenReturn(1);
        when(salaryRepository.findByType(RepairmanType.MECHANIC)).thenReturn(new Salary() {
            {
                setType(RepairmanType.MECHANIC);
                setHourlyRate(90.0f);
            }
        });

        Salary result = repairmanService.updateSalaryStandard(RepairmanType.MECHANIC, 90.0f);

        assertNotNull(result);
        assertEquals(RepairmanType.MECHANIC, result.getType());
        assertEquals(90.0f, result.getHourlyRate());
        verify(salaryRepository).existsByType(typeStr);
        verify(salaryRepository).updateSalary(typeStr, 90.0f);
        verify(salaryRepository).findByType(RepairmanType.MECHANIC);
    }

    // 测试薪资为空时的注册
    @Test
    void register_WithNullSalary_Success() {
        Repairman newRepairman = new Repairman();
        newRepairman.setUsername("newuser");
        newRepairman.setPassword("password");
        newRepairman.setName("New User");
        newRepairman.setGender("男");
        newRepairman.setType(RepairmanType.APPRENTICE);

        when(repairmanRepository.existsByUsername(anyString())).thenReturn(false);
        when(repairmanRepository.save(any(Repairman.class))).thenReturn(newRepairman);
        // 无论调用多少次都返回null
        when(salaryRepository.findByType(RepairmanType.APPRENTICE)).thenReturn(null);
        doNothing().when(salaryRepository).insertDefaultSalaries();

        Repairman result = repairmanService.register(newRepairman);

        assertNotNull(result);
        assertEquals(RepairmanType.APPRENTICE, result.getType());
        verify(salaryRepository).insertDefaultSalaries();
        verify(salaryRepository, times(2)).findByType(RepairmanType.APPRENTICE);
        verify(repairmanRepository, times(2)).save(any(Repairman.class)); // 一次初始save，一次设置默认薪资
    }

    // --- 撤销功能综合测试 ---
    @Mock
    private RepairmanHistoryRepository repairmanHistoryRepository;

    @Test
    void testUndoRepairmanHistory_Success() {
        Long repairmanId = 1L;
        RepairmanHistory h1 = new RepairmanHistory();
        h1.setId(1L);
        h1.setRepairmanId(repairmanId);
        h1.setUsername("v1");
        h1.setOperationTime(LocalDateTime.now().minusMinutes(2));
        RepairmanHistory h2 = new RepairmanHistory();
        h2.setId(2L);
        h2.setRepairmanId(repairmanId);
        h2.setUsername("v2");
        h2.setOperationTime(LocalDateTime.now().minusMinutes(1));
        List<RepairmanHistory> histories = List.of(h2, h1); // 按时间降序
        Repairman repairman = new Repairman();
        repairman.setRepairmanId(repairmanId);
        when(repairmanHistoryRepository.findByRepairmanIdOrderByOperationTimeDesc(repairmanId)).thenReturn(histories);
        when(repairmanHistoryRepository.findById(1L)).thenReturn(Optional.of(h1));
        when(repairmanRepository.findById(repairmanId)).thenReturn(Optional.of(repairman));
        when(repairmanRepository.save(any())).thenReturn(repairman);
        RepairmanService service = new RepairmanService(repairmanRepository, maintenanceItemRepository,
                materialRepository, maintenanceRecordRepository, recordMaterialRepository, salaryRepository,
                requiredTypeRepository, carRepository, repairmanHistoryRepository, mock(MaterialService.class));
        Repairman result = service.undoRepairmanHistory(repairmanId);
        assertNotNull(result);
        verify(repairmanRepository).save(any());
    }

    @Test
    void testUndoRepairmanHistory_NoHistory() {
        Long repairmanId = 1L;
        when(repairmanHistoryRepository.findByRepairmanIdOrderByOperationTimeDesc(repairmanId)).thenReturn(List.of());
        RepairmanService service = new RepairmanService(repairmanRepository, maintenanceItemRepository,
                materialRepository, maintenanceRecordRepository, recordMaterialRepository, salaryRepository,
                requiredTypeRepository, carRepository, repairmanHistoryRepository, mock(MaterialService.class));
        assertThrows(BadRequestException.class, () -> service.undoRepairmanHistory(repairmanId));
    }

    @Test
    void testUndoRepairmanHistory_OnlyOneHistory() {
        Long repairmanId = 1L;
        RepairmanHistory h1 = new RepairmanHistory();
        h1.setId(1L);
        h1.setRepairmanId(repairmanId);
        h1.setUsername("v1");
        h1.setOperationTime(LocalDateTime.now());
        when(repairmanHistoryRepository.findByRepairmanIdOrderByOperationTimeDesc(repairmanId)).thenReturn(List.of(h1));
        RepairmanService service = new RepairmanService(repairmanRepository, maintenanceItemRepository,
                materialRepository, maintenanceRecordRepository, recordMaterialRepository, salaryRepository,
                requiredTypeRepository, carRepository, repairmanHistoryRepository, mock(MaterialService.class));
        assertThrows(BadRequestException.class, () -> service.undoRepairmanHistory(repairmanId));
    }

    @Test
    void testRedoRepairmanHistory_Success() {
        Long repairmanId = 1L;
        LocalDateTime t1 = LocalDateTime.now().minusMinutes(2);
        LocalDateTime t2 = LocalDateTime.now().minusMinutes(1);
        RepairmanHistory h1 = new RepairmanHistory();
        h1.setId(1L);
        h1.setRepairmanId(repairmanId);
        h1.setUsername("v1");
        h1.setOperationTime(t1);
        RepairmanHistory h2 = new RepairmanHistory();
        h2.setId(2L);
        h2.setRepairmanId(repairmanId);
        h2.setUsername("v2");
        h2.setOperationTime(t2);
        List<RepairmanHistory> histories = List.of(h2, h1); // 降序
        Repairman repairman = new Repairman();
        repairman.setRepairmanId(repairmanId);
        when(repairmanHistoryRepository.findByRepairmanIdOrderByOperationTimeDesc(repairmanId)).thenReturn(histories);
        when(repairmanHistoryRepository
                .findTop1ByRepairmanIdAndOperationTimeGreaterThanOrderByOperationTimeAsc(eq(repairmanId), eq(t2)))
                .thenReturn(h1);
        when(repairmanHistoryRepository.findById(1L)).thenReturn(Optional.of(h1));
        when(repairmanRepository.findById(repairmanId)).thenReturn(Optional.of(repairman));
        when(repairmanRepository.save(any())).thenReturn(repairman);
        RepairmanService service = new RepairmanService(repairmanRepository, maintenanceItemRepository,
                materialRepository, maintenanceRecordRepository, recordMaterialRepository, salaryRepository,
                requiredTypeRepository, carRepository, repairmanHistoryRepository, mock(MaterialService.class));
        Repairman result = service.redoRepairmanHistory(repairmanId);
        assertNotNull(result);
        verify(repairmanRepository).save(any());
    }

    @Test
    void testRedoRepairmanHistory_NoRedo() {
        Long repairmanId = 1L;
        LocalDateTime t2 = LocalDateTime.now().minusMinutes(1);
        RepairmanHistory h2 = new RepairmanHistory();
        h2.setId(2L);
        h2.setRepairmanId(repairmanId);
        h2.setUsername("v2");
        h2.setOperationTime(t2);
        List<RepairmanHistory> histories = List.of(h2);
        when(repairmanHistoryRepository.findByRepairmanIdOrderByOperationTimeDesc(repairmanId)).thenReturn(histories);
        when(repairmanHistoryRepository
                .findTop1ByRepairmanIdAndOperationTimeGreaterThanOrderByOperationTimeAsc(eq(repairmanId), eq(t2)))
                .thenReturn(null);
        RepairmanService service = new RepairmanService(repairmanRepository, maintenanceItemRepository,
                materialRepository, maintenanceRecordRepository, recordMaterialRepository, salaryRepository,
                requiredTypeRepository, carRepository, repairmanHistoryRepository, mock(MaterialService.class));
        assertThrows(BadRequestException.class, () -> service.redoRepairmanHistory(repairmanId));
    }
}

