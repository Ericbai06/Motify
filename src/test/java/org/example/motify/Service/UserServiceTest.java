package org.example.motify.Service;

import org.example.motify.Entity.User;
import org.example.motify.Repository.UserRepository;
import org.example.motify.Repository.CarRepository;
import org.example.motify.Repository.MaintenanceItemRepository;
import org.example.motify.Entity.Car;
import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Repository.RepairmanRepository;
import org.example.motify.Entity.Repairman;
import org.example.motify.Exception.AuthenticationException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.example.motify.util.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private MaintenanceItemRepository maintenanceItemRepository;

    @Mock
    private RepairmanRepository repairmanRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Car testCar;
    private MaintenanceItem testMaintenanceItem;
    private Repairman testRepairman;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置测试数据
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword(PasswordEncoder.encode("password123"));
        testUser.setPhone("13800138000");
        testUser.setName("测试用户");
        testUser.setEmail("test@example.com");
        
        testCar = new Car();
        testCar.setCarId(1L);
        testCar.setBrand("Toyota");
        testCar.setModel("Camry");
        testCar.setLicensePlate("京A12345");
        testCar.setUser(testUser);
        
        testMaintenanceItem = new MaintenanceItem();
        testMaintenanceItem.setItemId(1L);
        testMaintenanceItem.setName("发动机维修");
        testMaintenanceItem.setDescription("发动机需要检修");
        testMaintenanceItem.setStatus(MaintenanceStatus.PENDING);
        testMaintenanceItem.setProgress(0);
        testMaintenanceItem.setCost(0.0);
        testMaintenanceItem.setCreateTime(LocalDateTime.now());
        testMaintenanceItem.setCar(testCar);
        
        testRepairman = new Repairman();
        testRepairman.setRepairmanId(1L);
        testRepairman.setUsername("repairman1");
        testRepairman.setName("维修工1");
        testRepairman.setType("发动机维修");
    }

    @Test
    void registerUser_Success() {
        User user = new User();
        user.setUsername("newUser");
        user.setPassword("password123");
        user.setPhone("13800138000");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(user);
        
        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateUsername() {
        User user = new User();
        user.setUsername("existingUser");
        user.setPassword("password123");
        user.setPhone("13800138000");

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.register(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmptyUsername() {
        User user = new User();
        user.setUsername("");
        user.setPassword("password123");
        user.setPhone("13800138000");

        assertThrows(BadRequestException.class, () -> userService.register(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.login("testUser", "password123");
        
        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());
    }

    @Test
    void loginUser_WrongPassword() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(AuthenticationException.class, () -> 
            userService.login("testUser", "wrongPassword"));
    }

    @Test
    void loginUser_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> 
            userService.login("nonExistentUser", "password123"));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_Success() {
        User updateDetails = new User();
        updateDetails.setUsername("updatedUser");
        updateDetails.setPhone("13900139000");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(1L, updateDetails);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NotFound() {
        User updateDetails = new User();
        updateDetails.setUsername("updatedUser");

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            userService.updateUser(1L, updateDetails));
    }

    @Test
    void getUserCarsSafe_Success() {
        Object[] carData = {1L, "Toyota", "Camry", "京A12345", 1L, "testUser", "测试用户", "13800138000", "test@example.com"};
        List<Object[]> carResults = Arrays.<Object[]>asList(carData);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(carRepository.findCarBasicInfoByUserId(anyLong())).thenReturn(carResults);

        List<Map<String, Object>> result = userService.getUserCarsSafe(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        Map<String, Object> carMap = result.get(0);
        assertEquals(1L, carMap.get("carId"));
        assertEquals("Toyota", carMap.get("brand"));
        assertEquals("Camry", carMap.get("model"));
        assertEquals("京A12345", carMap.get("licensePlate"));
    }

    @Test
    void getUserCarsSafe_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> 
            userService.getUserCarsSafe(1L));
    }

    @Test
    void addCar_Success() {
        Car newCar = new Car();
        newCar.setBrand("Honda");
        newCar.setModel("Accord");
        newCar.setLicensePlate("京B67890");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(carRepository.save(any(Car.class))).thenReturn(newCar);

        Car result = userService.addCar(1L, newCar);

        assertNotNull(result);
        assertEquals("Honda", result.getBrand());
        assertEquals("Accord", result.getModel());
        assertEquals("京B67890", result.getLicensePlate());
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void addCar_UserNotFound() {
        Car newCar = new Car();
        newCar.setBrand("Honda");
        newCar.setModel("Accord");
        newCar.setLicensePlate("京B67890");

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            userService.addCar(1L, newCar));
    }

    @Test
    void addCar_EmptyBrand() {
        Car newCar = new Car();
        newCar.setBrand("");
        newCar.setModel("Accord");
        newCar.setLicensePlate("京B67890");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        assertThrows(BadRequestException.class, () -> 
            userService.addCar(1L, newCar));
    }

    @Test
    void getUserMaintenanceItems_Success() {
        List<MaintenanceItem> items = Arrays.asList(testMaintenanceItem);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(maintenanceItemRepository.findByCar_User_UserId(anyLong())).thenReturn(items);

        List<MaintenanceItem> result = userService.getUserMaintenanceItems(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("发动机维修", result.get(0).getName());
    }

    @Test
    void getUserMaintenanceItems_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> 
            userService.getUserMaintenanceItems(1L));
    }

    @Test
    void getUserCurrentMaintenanceItems_Success() {
        // 创建不同状态的维修项目
        MaintenanceItem pendingItem = new MaintenanceItem();
        pendingItem.setItemId(1L);
        pendingItem.setName("刹车片更换");
        pendingItem.setStatus(MaintenanceStatus.PENDING);
        
        MaintenanceItem inProgressItem = new MaintenanceItem();
        inProgressItem.setItemId(2L);
        inProgressItem.setName("发动机维修");
        inProgressItem.setStatus(MaintenanceStatus.IN_PROGRESS);
        
        MaintenanceItem completedItem = new MaintenanceItem();
        completedItem.setItemId(3L);
        completedItem.setName("轮胎更换");
        completedItem.setStatus(MaintenanceStatus.COMPLETED);
        
        List<MaintenanceItem> allItems = Arrays.asList(pendingItem, inProgressItem, completedItem);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(maintenanceItemRepository.findByCar_User_UserId(anyLong())).thenReturn(allItems);

        List<MaintenanceItem> result = userService.getUserCurrentMaintenanceItems(1L);

        assertNotNull(result);
        assertEquals(1, result.size()); // 只有一个正在进行的项目
        assertEquals("发动机维修", result.get(0).getName());
        assertEquals(MaintenanceStatus.IN_PROGRESS, result.get(0).getStatus());
    }

    @Test
    void getUserCurrentMaintenanceItems_NoCurrentItems() {
        // 创建没有正在进行的维修项目
        MaintenanceItem pendingItem = new MaintenanceItem();
        pendingItem.setItemId(1L);
        pendingItem.setName("刹车片更换");
        pendingItem.setStatus(MaintenanceStatus.PENDING);
        
        MaintenanceItem completedItem = new MaintenanceItem();
        completedItem.setItemId(2L);
        completedItem.setName("轮胎更换");
        completedItem.setStatus(MaintenanceStatus.COMPLETED);
        
        List<MaintenanceItem> allItems = Arrays.asList(pendingItem, completedItem);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(maintenanceItemRepository.findByCar_User_UserId(anyLong())).thenReturn(allItems);

        List<MaintenanceItem> result = userService.getUserCurrentMaintenanceItems(1L);

        assertNotNull(result);
        assertEquals(0, result.size()); // 没有正在进行的项目
    }

    @Test
    void getUserCurrentMaintenanceItems_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> 
            userService.getUserCurrentMaintenanceItems(1L));
    }

    @Test
    void submitRepairRequest_Success() {
        List<Repairman> repairmen = Arrays.asList(testRepairman);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(testCar));
        when(repairmanRepository.findAll()).thenReturn(repairmen);
        when(maintenanceItemRepository.save(any(MaintenanceItem.class)))
            .thenAnswer(i -> i.getArgument(0));

        MaintenanceItem result = userService.submitRepairRequest(1L, 1L, "发动机维修", "发动机需要检修");

        assertNotNull(result);
        assertEquals("发动机维修", result.getName());
        assertEquals("发动机需要检修", result.getDescription());
        assertEquals(MaintenanceStatus.PENDING, result.getStatus());
        assertEquals(0, result.getProgress());
        assertEquals(0.0, result.getCost());
        assertNotNull(result.getCreateTime());
        assertEquals(testCar, result.getCar());
        assertFalse(result.getRepairmen().isEmpty());
        assertEquals(testRepairman, result.getRepairmen().get(0));
        
        verify(maintenanceItemRepository).save(any(MaintenanceItem.class));
    }

    @Test
    void submitRepairRequest_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            userService.submitRepairRequest(1L, 1L, "维修项目", "维修描述"));
    }

    @Test
    void submitRepairRequest_CarNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            userService.submitRepairRequest(1L, 1L, "维修项目", "维修描述"));
    }

    @Test
    void submitRepairRequest_CarNotBelongToUser() {
        User anotherUser = new User();
        anotherUser.setUserId(2L);
        anotherUser.setUsername("anotherUser");
        
        Car anotherCar = new Car();
        anotherCar.setCarId(1L);
        anotherCar.setUser(anotherUser);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(anotherCar));

        assertThrows(BadRequestException.class, () -> 
            userService.submitRepairRequest(1L, 1L, "维修项目", "维修描述"));
    }

    @Test
    void submitRepairRequest_EmptyDescription() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(testCar));

        assertThrows(BadRequestException.class, () -> 
            userService.submitRepairRequest(1L, 1L, "维修项目", ""));
    }

    @Test
    void submitRepairRequest_EmptyName() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(testCar));

        assertThrows(BadRequestException.class, () -> 
            userService.submitRepairRequest(1L, 1L, "", "维修描述"));
    }

    @Test
    void resetPassword_Success() {
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertDoesNotThrow(() -> 
            userService.resetPassword("13800138000", "123456", "newPassword123"));
        
        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPassword_UserNotFound() {
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            userService.resetPassword("13800138000", "123456", "newPassword123"));
    }

    @Test
    void resetPassword_InvalidPhone() {
        assertThrows(BadRequestException.class, () -> 
            userService.resetPassword("invalid-phone", "123456", "newPassword123"));
    }

    @Test
    void resetPassword_WeakPassword() {
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(BadRequestException.class, () -> 
            userService.resetPassword("13800138000", "123456", "123"));
    }

    // Test for submitRushOrder method
    @Test
    void testSubmitRushOrder_Success() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        String reminderMessage = "请加急处理，明天需要用车";
        
        User user = new User();
        user.setUserId(userId);
        
        Car car = new Car();
        car.setCarId(1L);
        car.setUser(user);
        
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(itemId);
        item.setName("刹车片更换");
        item.setStatus(MaintenanceStatus.IN_PROGRESS);
        item.setCar(car);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(maintenanceItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(maintenanceItemRepository.save(any(MaintenanceItem.class))).thenReturn(item);
        
        // Act
        MaintenanceItem result = userService.submitRushOrder(userId, itemId, reminderMessage);
        
        // Assert
        assertNotNull(result);
        assertEquals(reminderMessage, result.getReminder());
        assertNotNull(result.getUpdateTime());
        verify(maintenanceItemRepository, times(1)).save(item);
    }

    @Test
    void testSubmitRushOrder_EmptyReminderMessage() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        String reminderMessage = "";
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> userService.submitRushOrder(userId, itemId, reminderMessage));
        assertEquals("催单信息不能为空", exception.getMessage());
    }

    @Test
    void testSubmitRushOrder_UserNotFound() {
        // Arrange
        Long userId = 999L;
        Long itemId = 1L;
        String reminderMessage = "请加急处理";
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> userService.submitRushOrder(userId, itemId, reminderMessage));
        assertTrue(exception.getMessage().contains("User"));
    }

    @Test
    void testSubmitRushOrder_ItemNotFound() {
        // Arrange
        Long userId = 1L;
        Long itemId = 999L;
        String reminderMessage = "请加急处理";
        
        User user = new User();
        user.setUserId(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(maintenanceItemRepository.findById(itemId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
            () -> userService.submitRushOrder(userId, itemId, reminderMessage));
        assertTrue(exception.getMessage().contains("MaintenanceItem"));
    }

    @Test
    void testSubmitRushOrder_NotOwner() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        String reminderMessage = "请加急处理";
        
        User user = new User();
        user.setUserId(userId);
        
        User otherUser = new User();
        otherUser.setUserId(2L);
        
        Car car = new Car();
        car.setCarId(1L);
        car.setUser(otherUser);
        
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(itemId);
        item.setCar(car);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(maintenanceItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> userService.submitRushOrder(userId, itemId, reminderMessage));
        assertEquals("无权限操作该维修项目", exception.getMessage());
    }

    @Test
    void testSubmitRushOrder_CompletedItem() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        String reminderMessage = "请加急处理";
        
        User user = new User();
        user.setUserId(userId);
        
        Car car = new Car();
        car.setCarId(1L);
        car.setUser(user);
        
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(itemId);
        item.setStatus(MaintenanceStatus.COMPLETED);
        item.setCar(car);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(maintenanceItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> userService.submitRushOrder(userId, itemId, reminderMessage));
        assertEquals("该维修项目已完成或已取消，无法催单", exception.getMessage());
    }

    // Test for submitServiceRating method
    @Test
    void testSubmitServiceRating_Success() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        Integer score = 5;
        
        User user = new User();
        user.setUserId(userId);
        
        Car car = new Car();
        car.setCarId(1L);
        car.setUser(user);
        
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(itemId);
        item.setName("刹车片更换");
        item.setStatus(MaintenanceStatus.COMPLETED);
        item.setScore(null); // 未评分
        item.setCar(car);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(maintenanceItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(maintenanceItemRepository.save(any(MaintenanceItem.class))).thenReturn(item);
        
        // Act
        MaintenanceItem result = userService.submitServiceRating(userId, itemId, score);
        
        // Assert
        assertNotNull(result);
        assertEquals(score, result.getScore());
        assertNotNull(result.getUpdateTime());
        verify(maintenanceItemRepository, times(1)).save(item);
    }

    @Test
    void testSubmitServiceRating_InvalidScore() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        Integer score = 6; // 超出范围
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> userService.submitServiceRating(userId, itemId, score));
        assertEquals("评分必须在1-5分之间", exception.getMessage());
        
        // 测试负数评分
        Integer zeroScore = 0;
        BadRequestException exception2 = assertThrows(BadRequestException.class, 
            () -> userService.submitServiceRating(userId, itemId, zeroScore));
        assertEquals("评分必须在1-5分之间", exception2.getMessage());
    }

    @Test
    void testSubmitServiceRating_NotCompletedItem() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        Integer score = 5;
        
        User user = new User();
        user.setUserId(userId);
        
        Car car = new Car();
        car.setCarId(1L);
        car.setUser(user);
        
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(itemId);
        item.setStatus(MaintenanceStatus.IN_PROGRESS); // 未完成
        item.setCar(car);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(maintenanceItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> userService.submitServiceRating(userId, itemId, score));
        assertEquals("只有已完成的维修项目才能评分", exception.getMessage());
    }

    @Test
    void testSubmitServiceRating_AlreadyRated() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        Integer score = 5;
        
        User user = new User();
        user.setUserId(userId);
        
        Car car = new Car();
        car.setCarId(1L);
        car.setUser(user);
        
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(itemId);
        item.setStatus(MaintenanceStatus.COMPLETED);
        item.setScore(4); // 已经评分
        item.setCar(car);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(maintenanceItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> userService.submitServiceRating(userId, itemId, score));
        assertEquals("该维修项目已经评分，无法重复评分", exception.getMessage());
    }

    // Test for getMaintenanceItemDetail method
    @Test
    void testGetMaintenanceItemDetail_Success() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        
        User user = new User();
        user.setUserId(userId);
        
        Car car = new Car();
        car.setCarId(1L);
        car.setUser(user);
        
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(itemId);
        item.setName("刹车片更换");
        item.setDescription("更换前后刹车片");
        item.setStatus(MaintenanceStatus.COMPLETED);
        item.setScore(5);
        item.setReminder("请加急处理");
        item.setCar(car);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(maintenanceItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        
        // Act
        MaintenanceItem result = userService.getMaintenanceItemDetail(userId, itemId);
        
        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        assertEquals("刹车片更换", result.getName());
        assertEquals(5, result.getScore());
        assertEquals("请加急处理", result.getReminder());
    }

    @Test
    void testGetMaintenanceItemDetail_NotOwner() {
        // Arrange
        Long userId = 1L;
        Long itemId = 1L;
        
        User user = new User();
        user.setUserId(userId);
        
        User otherUser = new User();
        otherUser.setUserId(2L);
        
        Car car = new Car();
        car.setCarId(1L);
        car.setUser(otherUser);
        
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(itemId);
        item.setCar(car);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(maintenanceItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        
        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, 
            () -> userService.getMaintenanceItemDetail(userId, itemId));
        assertEquals("无权限查看该维修项目", exception.getMessage());
    }
}