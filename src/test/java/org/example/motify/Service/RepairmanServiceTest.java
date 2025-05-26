package org.example.motify.Service;

import org.example.motify.Entity.Repairman;
import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Repository.RepairmanRepository;
import org.example.motify.Repository.MaintenanceItemRepository;
import org.example.motify.Repository.MaterialRepository;
import org.example.motify.Repository.CarRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

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

    @InjectMocks
    private RepairmanService repairmanService;

    private Repairman testRepairman;
    private MaintenanceItem testMaintenanceItem;

    @BeforeEach
    void setUp() {
        testRepairman = new Repairman();
        testRepairman.setRepairmanId(1L);
        testRepairman.setUsername("testRepairman");
        testRepairman.setPassword(PasswordEncoder.encode("password123"));
        testRepairman.setName("Test Repairman");
        testRepairman.setPhone("1234567890");
        testRepairman.setSpecialty("Engine");
        testRepairman.setType("MECHANIC");

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
        lenient().when(repairmanRepository.existsByUsername(anyString())).thenReturn(false);
        lenient().when(repairmanRepository.save(any(Repairman.class))).thenReturn(testRepairman);

        Repairman result = repairmanService.register(testRepairman);

        assertNotNull(result);
        assertEquals(testRepairman.getUsername(), result.getUsername());
        verify(repairmanRepository).save(any(Repairman.class));
    }

    @Test
    void register_DuplicateUsername() {
        lenient().when(repairmanRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            repairmanService.register(testRepairman);
        });
    }

    @Test
    void login_Success() {
        lenient().when(repairmanRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(testRepairman));

        Optional<Repairman> result = repairmanService.login("testRepairman", "password123");
        assertTrue(result.isPresent());
        assertEquals("testRepairman", result.get().getUsername());
    }

    @Test
    void login_WrongPassword() {
        lenient().when(repairmanRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(testRepairman));

        assertThrows(AuthenticationException.class, 
            () -> repairmanService.login("testRepairman", "wrongPassword"));
    }

    @Test
    void getRepairmanById_Success() {
        lenient().when(repairmanRepository.findById(anyLong())).thenReturn(Optional.of(testRepairman));
        Optional<Repairman> result = repairmanService.getRepairmanById(1L);
        assertTrue(result.isPresent());
        assertEquals(testRepairman.getRepairmanId(), result.get().getRepairmanId());
    }

    @Test
    void getRepairmanById_NotFound() {
        lenient().when(repairmanRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            repairmanService.getRepairmanById(1L);
        });
    }

    @Test
    void updateMaintenanceItem_Success() {
        lenient().when(maintenanceItemRepository.findById(anyLong())).thenReturn(Optional.of(testMaintenanceItem));
        lenient().when(maintenanceItemRepository.save(any(MaintenanceItem.class))).thenReturn(testMaintenanceItem);
        MaintenanceItem result = repairmanService.updateMaintenanceItem(1L, testMaintenanceItem);
        assertNotNull(result);
        assertEquals(testMaintenanceItem.getItemId(), result.getItemId());
    }

    @Test
    void getRepairmanCurrentRecords_Success() {
        testRepairman.setMaintenanceItems(Collections.singletonList(testMaintenanceItem));
        lenient().when(repairmanRepository.findById(anyLong())).thenReturn(Optional.of(testRepairman));
        List<MaintenanceItem> results = repairmanService.getRepairmanCurrentRecords(1L);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void calculateTotalIncome_Success() {
        testRepairman.setMaintenanceItems(Collections.singletonList(testMaintenanceItem));
        lenient().when(repairmanRepository.findById(anyLong())).thenReturn(Optional.of(testRepairman));
        org.example.motify.Entity.RecordInfo recordInfo = new org.example.motify.Entity.RecordInfo();
        recordInfo.setTotalAmount(100.0);
        testMaintenanceItem.setRecordInfo(recordInfo);
        testMaintenanceItem.setProgress(100);
        double totalIncome = repairmanService.calculateTotalIncome(1L);
        assertEquals(100.0, totalIncome);
    }

    @Test
    void acceptMaintenanceItem_Success() {
        lenient().when(repairmanRepository.findById(anyLong())).thenReturn(Optional.of(testRepairman));
        lenient().when(maintenanceItemRepository.findById(anyLong())).thenReturn(Optional.of(testMaintenanceItem));
        lenient().when(maintenanceItemRepository.save(any(MaintenanceItem.class))).thenReturn(testMaintenanceItem);
        testMaintenanceItem.setRepairmen(new java.util.ArrayList<>());
        org.example.motify.Entity.RecordInfo recordInfo = new org.example.motify.Entity.RecordInfo();
        recordInfo.setTotalAmount(100.0);
        testMaintenanceItem.setRecordInfo(recordInfo);
        MaintenanceItem result = repairmanService.acceptMaintenanceItem(1L, 1L);
        assertNotNull(result);
        assertEquals(10, result.getProgress());
    }
} 