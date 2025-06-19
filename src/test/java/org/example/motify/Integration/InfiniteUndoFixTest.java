package org.example.motify.Integration;

import org.example.motify.Entity.User;
import org.example.motify.Entity.UserHistory;
import org.example.motify.Entity.Repairman;
import org.example.motify.Entity.RepairmanHistory;
import org.example.motify.Enum.RepairmanType;
import org.example.motify.Repository.UserRepository;
import org.example.motify.Repository.UserHistoryRepository;
import org.example.motify.Repository.RepairmanRepository;
import org.example.motify.Repository.RepairmanHistoryRepository;
import org.example.motify.Repository.CarRepository;
import org.example.motify.Repository.MaintenanceItemRepository;
import org.example.motify.Repository.MaterialRepository;
import org.example.motify.Repository.MaintenanceRecordRepository;
import org.example.motify.Repository.RecordMaterialRepository;
import org.example.motify.Repository.SalaryRepository;
import org.example.motify.Repository.RequiredRepairmanTypeRepository;
import org.example.motify.Service.UserService;
import org.example.motify.Service.RepairmanService;
import org.example.motify.Service.MaterialService;
import org.example.motify.Exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 集成测试：验证无限撤销bug修复
 * 
 * 测试场景：
 * 1. 验证有足够历史记录时可以正常撤销
 * 2. 验证只有一条历史记录时不能撤销（修复无限撤销）
 * 3. 验证撤销操作不会创建新的历史记录（避免无限撤销循环）
 */
public class InfiniteUndoFixTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserHistoryRepository userHistoryRepository;
    @Mock
    private RepairmanRepository repairmanRepository;
    @Mock
    private RepairmanHistoryRepository repairmanHistoryRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private MaintenanceItemRepository maintenanceItemRepository;
    @Mock
    private MaterialRepository materialRepository;
    @Mock
    private MaintenanceRecordRepository maintenanceRecordRepository;
    @Mock
    private RecordMaterialRepository recordMaterialRepository;
    @Mock
    private SalaryRepository salaryRepository;
    @Mock
    private RequiredRepairmanTypeRepository requiredRepairmanTypeRepository;
    @Mock
    private MaterialService materialService;

    private UserService userService;
    private RepairmanService repairmanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, carRepository, maintenanceItemRepository, 
                                    repairmanRepository, userHistoryRepository);
        repairmanService = new RepairmanService(repairmanRepository, maintenanceItemRepository, 
                                              materialRepository, maintenanceRecordRepository,
                                              recordMaterialRepository, salaryRepository,
                                              requiredRepairmanTypeRepository, carRepository,
                                              repairmanHistoryRepository, materialService);
    }

    @Test
    void testUserInfiniteUndoFix_OnlyOneHistoryRecord() {
        // 准备测试数据：只有一条历史记录
        Long userId = 1L;
        UserHistory singleHistory = new UserHistory();
        singleHistory.setId(1L);
        singleHistory.setUserId(userId);
        singleHistory.setUsername("user1");
        singleHistory.setOperationTime(LocalDateTime.now());

        // Mock repository 行为
        when(userHistoryRepository.findByUserIdOrderByOperationTimeDesc(userId))
            .thenReturn(List.of(singleHistory));

        // 验证：尝试撤销时应该抛出异常（修复无限撤销）
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> userService.undoUserHistory(userId));
        assertEquals("没有可撤销的历史记录", exception.getMessage());

        // 验证：没有尝试保存任何用户记录（避免创建新历史）
        verify(userRepository, never()).save(any());
        verify(userHistoryRepository, never()).save(any());
    }

    @Test
    void testRepairmanInfiniteUndoFix_OnlyOneHistoryRecord() {
        // 准备测试数据：只有一条历史记录
        Long repairmanId = 1L;
        RepairmanHistory singleHistory = new RepairmanHistory();
        singleHistory.setId(1L);
        singleHistory.setRepairmanId(repairmanId);
        singleHistory.setUsername("repairman1");
        singleHistory.setOperationTime(LocalDateTime.now());

        // Mock repository 行为
        when(repairmanHistoryRepository.findByRepairmanIdOrderByOperationTimeDesc(repairmanId))
            .thenReturn(List.of(singleHistory));

        // 验证：尝试撤销时应该抛出异常（修复无限撤销）
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> repairmanService.undoRepairmanHistory(repairmanId));
        assertEquals("没有可撤销的历史记录", exception.getMessage());

        // 验证：没有尝试保存任何维修人员记录（避免创建新历史）
        verify(repairmanRepository, never()).save(any());
        verify(repairmanHistoryRepository, never()).save(any());
    }

    @Test
    void testUserUndoDoesNotCreateNewHistory() {
        // 准备测试数据：两条历史记录
        Long userId = 1L;
        UserHistory h1 = new UserHistory();
        h1.setId(1L);
        h1.setUserId(userId);
        h1.setUsername("user_v1");
        h1.setOperationTime(LocalDateTime.now().minusMinutes(2));

        UserHistory h2 = new UserHistory();
        h2.setId(2L);
        h2.setUserId(userId);
        h2.setUsername("user_v2");
        h2.setOperationTime(LocalDateTime.now().minusMinutes(1));

        User user = new User();
        user.setUserId(userId);
        user.setUsername("user_v2");

        // Mock repository 行为
        when(userHistoryRepository.findByUserIdOrderByOperationTimeDesc(userId))
            .thenReturn(List.of(h2, h1)); // 按时间降序
        when(userHistoryRepository.findById(1L)).thenReturn(Optional.of(h1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // 执行撤销操作
        User result = userService.undoUserHistory(userId);

        // 验证：用户信息被更新
        assertNotNull(result);
        verify(userRepository).save(any(User.class));

        // 关键验证：确保撤销操作没有创建新的历史记录
        verify(userHistoryRepository, never()).save(any(UserHistory.class));
    }

    @Test
    void testRepairmanUndoDoesNotCreateNewHistory() {
        // 准备测试数据：两条历史记录
        Long repairmanId = 1L;
        RepairmanHistory h1 = new RepairmanHistory();
        h1.setId(1L);
        h1.setRepairmanId(repairmanId);
        h1.setUsername("repairman_v1");
        h1.setOperationTime(LocalDateTime.now().minusMinutes(2));

        RepairmanHistory h2 = new RepairmanHistory();
        h2.setId(2L);
        h2.setRepairmanId(repairmanId);
        h2.setUsername("repairman_v2");
        h2.setOperationTime(LocalDateTime.now().minusMinutes(1));

        Repairman repairman = new Repairman();
        repairman.setRepairmanId(repairmanId);
        repairman.setUsername("repairman_v2");

        // Mock repository 行为
        when(repairmanHistoryRepository.findByRepairmanIdOrderByOperationTimeDesc(repairmanId))
            .thenReturn(List.of(h2, h1)); // 按时间降序
        when(repairmanHistoryRepository.findById(1L)).thenReturn(Optional.of(h1));
        when(repairmanRepository.findById(repairmanId)).thenReturn(Optional.of(repairman));
        when(repairmanRepository.save(any(Repairman.class))).thenReturn(repairman);

        // 执行撤销操作
        Repairman result = repairmanService.undoRepairmanHistory(repairmanId);

        // 验证：维修人员信息被更新
        assertNotNull(result);
        verify(repairmanRepository).save(any(Repairman.class));

        // 关键验证：确保撤销操作没有创建新的历史记录
        verify(repairmanHistoryRepository, never()).save(any(RepairmanHistory.class));
    }

    @Test
    void testValidUndoWithMultipleHistoryRecords() {
        // 准备测试数据：三条历史记录
        Long userId = 1L;
        UserHistory h1 = new UserHistory();
        h1.setId(1L);
        h1.setUserId(userId);
        h1.setUsername("user_v1");
        h1.setOperationTime(LocalDateTime.now().minusMinutes(3));

        UserHistory h2 = new UserHistory();
        h2.setId(2L);
        h2.setUserId(userId);
        h2.setUsername("user_v2");
        h2.setOperationTime(LocalDateTime.now().minusMinutes(2));

        UserHistory h3 = new UserHistory();
        h3.setId(3L);
        h3.setUserId(userId);
        h3.setUsername("user_v3");
        h3.setOperationTime(LocalDateTime.now().minusMinutes(1));

        User user = new User();
        user.setUserId(userId);
        user.setUsername("user_v3");

        // Mock repository 行为
        when(userHistoryRepository.findByUserIdOrderByOperationTimeDesc(userId))
            .thenReturn(List.of(h3, h2, h1)); // 按时间降序
        when(userHistoryRepository.findById(2L)).thenReturn(Optional.of(h2));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // 执行撤销操作（应该回滚到 h2）
        User result = userService.undoUserHistory(userId);

        // 验证：撤销操作成功执行
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(userHistoryRepository, never()).save(any(UserHistory.class));
    }
}
