package org.example.motify.Service;

import org.example.motify.Entity.Car;
import org.example.motify.Entity.MaintenanceRecord;
import org.example.motify.Entity.User;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Repository.CarRepository;
import org.example.motify.Repository.MaintenanceRecordRepository;
import org.example.motify.Repository.UserRepository;
import org.example.motify.Repository.RepairmanRepository;
import org.example.motify.util.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RepairmanRepository repairmanRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Car testCar;
    private MaintenanceRecord testRecord;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("password");
        testUser.setPhone("13800138000");

        testCar = new Car();
        testCar.setCarId(1L);
        testCar.setBrand("Toyota");
        testCar.setModel("Camry");
        testCar.setLicensePlate("京A12345");
        testCar.setUser(testUser);

        testRecord = new MaintenanceRecord();
        testRecord.setRecordId(1L);
        testRecord.setDescription("发动机异响");
        testRecord.setStatus(MaintenanceStatus.PENDING);
        testRecord.setProgress(0);
        testRecord.setCar(testCar);
        testRecord.setRepairman(new ArrayList<>());
    }

    @Test
    void register_Success() {
        // 准备测试数据
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 执行测试
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setPhone("13800138000");
        User result = userService.register(user);

        // 验证结果
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("13800138000", result.getPhone());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_UsernameExists() {
        // 准备测试数据
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // 执行测试并验证异常
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setPhone("13800138000");
        assertThrows(BadRequestException.class, () -> userService.register(user));
    }

    @Test
    void login_Success() {
        // 准备测试数据
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // 执行测试
        Optional<User> result = userService.login("testUser", "password");

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void login_WrongPassword() {
        // 准备测试数据
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // 执行测试并验证异常
        assertThrows(org.example.motify.Exception.AuthenticationException.class, () ->
            userService.login("testUser", "wrongPassword")
        );
    }

    @Test
    void getUserById_Success() {
        // 准备测试数据
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // 执行测试
        User result = userService.getUserById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
    }

    @Test
    void getUserById_NotFound() {
        // 准备测试数据
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> 
            userService.getUserById(1L)
        );
    }

    @Test
    void getUserCars_Success() {
        // 准备测试数据
        testUser.setCars(Arrays.asList(testCar));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // 执行测试
        List<Car> result = userService.getUserCars(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }

    @Test
    void addCar_Success() {
        // 准备测试数据
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        // 执行测试
        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Camry");
        car.setLicensePlate("京A12345");
        Car result = userService.addCar(1L, car);

        // 验证结果
        assertNotNull(result);
        assertEquals("Toyota", result.getBrand());
        assertEquals("Camry", result.getModel());
        assertEquals("京A12345", result.getLicensePlate());
    }

    @Test
    void getUserMaintenanceRecords_Success() {
        // 准备测试数据
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(maintenanceRecordRepository.findByCar_User_UserId(anyLong()))
            .thenReturn(Arrays.asList(testRecord));

        // 执行测试
        List<MaintenanceRecord> result = userService.getUserMaintenanceRecords(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MaintenanceStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void submitRepairRequest_Success() {
        // 准备测试数据
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(testCar));
        when(repairmanRepository.findAll()).thenReturn(Arrays.asList(new org.example.motify.Entity.Repairman()));
        when(maintenanceRecordRepository.save(any(MaintenanceRecord.class))).thenReturn(testRecord);

        // 执行测试
        MaintenanceRecord result = userService.submitRepairRequest(1L, 1L, "发动机异响");

        // 验证结果
        assertNotNull(result);
        assertEquals("发动机异响", result.getDescription());
        assertEquals(MaintenanceStatus.PENDING, result.getStatus());
        assertEquals(0, result.getProgress());
    }

    @Test
    void submitRepairRequest_CarNotFound() {
        // 准备测试数据
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> 
            userService.submitRepairRequest(1L, 1L, "发动机异响")
        );
    }


    @Test
    void resetPassword_Success() {
        // 准备测试数据
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        // 执行测试
        assertDoesNotThrow(() -> 
            userService.resetPassword("13800138000", "123456", "newPassword123")
        );
        
        // 验证结果
        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPassword_InvalidPhone() {
        // 执行测试并验证异常
        assertThrows(BadRequestException.class, () -> 
            userService.resetPassword("1234567890", "123456", "newPassword123")
        );
    }

    @Test
    void resetPassword_WeakPassword() {
        // 执行测试并验证异常
        assertThrows(BadRequestException.class, () -> 
            userService.resetPassword("13800138000", "123456", "123")
        );
    }

    @Test
    void resetPassword_UserNotFound() {
        // 准备测试数据
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> 
            userService.resetPassword("13800138000", "123456", "newPassword123")
        );
    }


} 