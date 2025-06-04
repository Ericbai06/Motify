// package org.example.motify.Service;

// import org.example.motify.Entity.User;
// import org.example.motify.Repository.UserRepository;
// import org.example.motify.Repository.CarRepository;
// import org.example.motify.Repository.MaintenanceItemRepository;
// import org.example.motify.Entity.Car;
// import org.example.motify.Entity.MaintenanceItem;
// import org.example.motify.Enum.MaintenanceStatus;
// import org.example.motify.Repository.RepairmanRepository;
// import org.example.motify.Entity.Repairman;
// import org.example.motify.Exception.AuthenticationException;
// import org.example.motify.Exception.BadRequestException;
// import org.example.motify.Exception.ResourceNotFoundException;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.example.motify.util.PasswordEncoder;

// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.*;

// class UserServiceTest {

//     @Mock
//     private UserRepository userRepository;

//     @Mock
//     private CarRepository carRepository;

//     @Mock
//     private MaintenanceItemRepository maintenanceItemRepository;

//     @Mock
//     private RepairmanRepository repairmanRepository;

//     @InjectMocks
//     private UserService userService;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     void registerUser_Success() {
//         User user = new User();
//         user.setUsername("testUser");
//         user.setPassword("password123");
//         user.setPhone("13800138000");

//         when(userRepository.existsByUsername(anyString())).thenReturn(false);
//         when(userRepository.save(any(User.class))).thenReturn(user);

//         User result = userService.register(user);
//         assertNotNull(result);
//         assertEquals("testUser", result.getUsername());
//         verify(userRepository).save(any(User.class));
//     }

//     @Test
//     void registerUser_DuplicateUsername() {
//         User user = new User();
//         user.setUsername("existingUser");
//         user.setPassword("password123");
//         user.setPhone("13800138000");

//         when(userRepository.existsByUsername(anyString())).thenReturn(true);

//         assertThrows(BadRequestException.class, () -> userService.register(user));
//         verify(userRepository, never()).save(any(User.class));
//     }

//     @Test
//     void loginUser_Success() {
//         User user = new User();
//         user.setUsername("testUser");
//         user.setPassword(PasswordEncoder.encode("password123"));
//         user.setPhone("13800138000");

//         when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

//         Optional<User> result = userService.login("testUser", "password123");
//         assertTrue(result.isPresent());
//         assertEquals("testUser", result.get().getUsername());
//     }

//     @Test
//     void loginUser_Failure() {
//         User user = new User();
//         user.setUsername("testUser");
//         user.setPassword(PasswordEncoder.encode("password123"));

//         when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

//         assertThrows(AuthenticationException.class, () -> userService.login("testUser", "wrongPassword"));
//     }

//     @Test
//     void getUserById_Success() {
//         User user = new User();
//         user.setUserId(1L);
//         user.setUsername("testUser");
//         user.setPhone("13800138000");

//         when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

//         User result = userService.getUserById(1L);

//         assertNotNull(result);
//         assertEquals(1L, result.getUserId());
//         assertEquals("testUser", result.getUsername());
//     }

//     @Test
//     void getUserById_NotFound() {
//         when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
//     }

//     @Test
//     void updateUser_Success() {
//         User user = new User();
//         user.setUserId(1L);
//         user.setUsername("testUser");
//         user.setPhone("13800138000");

//         when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//         when(userRepository.existsByUsername(anyString())).thenReturn(false);
//         when(userRepository.save(any(User.class))).thenReturn(user);

//         User result = userService.updateUser(1L, user);

//         assertNotNull(result);
//         assertEquals(1L, result.getUserId());
//         assertEquals("testUser", result.getUsername());
//     }

//     @Test
//     void updateUser_NotFound() {
//         User user = new User();
//         user.setUserId(1L);

//         when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, user));
//     }

//     @Test
//     void getUserCars_Success() {
//         User user = new User();
//         user.setUserId(1L);
//         Car car1 = new Car();
//         car1.setCarId(1L);
//         car1.setBrand("Toyota");
//         car1.setModel("Camry");
//         car1.setLicensePlate("京A12345");
//         car1.setUser(user);
//         Car car2 = new Car();
//         car2.setCarId(2L);
//         car2.setBrand("Honda");
//         car2.setModel("Accord");
//         car2.setLicensePlate("京B67890");
//         car2.setUser(user);
        
//         user.setCars(Arrays.asList(car1, car2));

//         when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

//         List<Car> result = userService.getUserCars(1L);

//         assertNotNull(result);
//         assertEquals(2, result.size());
//         assertEquals("Toyota", result.get(0).getBrand());
//         assertEquals("Honda", result.get(1).getBrand());
//     }

//     @Test
//     void getUserCars_UserNotFound() {
//         when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(ResourceNotFoundException.class, () -> userService.getUserCars(1L));
//     }

//     @Test
//     void addCar_Success() {
//         User user = new User();
//         user.setUserId(1L);
//         Car car = new Car();
//         car.setBrand("Toyota");
//         car.setModel("Camry");
//         car.setLicensePlate("京A12345");

//         when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//         when(carRepository.save(any(Car.class))).thenReturn(car);

//         Car result = userService.addCar(1L, car);

//         assertNotNull(result);
//         assertEquals("Toyota", result.getBrand());
//         assertEquals("Camry", result.getModel());
//         assertEquals("京A12345", result.getLicensePlate());
//     }

//     @Test
//     void addCar_UserNotFound() {
//         Car car = new Car();
//         car.setBrand("Toyota");
//         car.setModel("Camry");
//         car.setLicensePlate("京A12345");

//         when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(ResourceNotFoundException.class, () -> userService.addCar(1L, car));
//     }

//     @Test
//     void getUserMaintenanceItems_Success() {
//         User user = new User();
//         user.setUserId(1L);
//         MaintenanceItem item1 = new MaintenanceItem();
//         item1.setItemId(1L);
//         MaintenanceItem item2 = new MaintenanceItem();
//         item2.setItemId(2L);

//         when(userRepository.existsById(anyLong())).thenReturn(true);
//         when(maintenanceItemRepository.findByCar_User_UserId(anyLong()))
//             .thenReturn(Arrays.asList(item1, item2));

//         List<MaintenanceItem> result = userService.getUserMaintenanceItems(1L);

//         assertNotNull(result);
//         assertEquals(2, result.size());
//     }

//     @Test
//     void getUserMaintenanceItems_UserNotFound() {
//         when(userRepository.existsById(anyLong())).thenReturn(false);

//         assertThrows(ResourceNotFoundException.class, () -> userService.getUserMaintenanceItems(1L));
//     }

//     @Test
//     void submitMaintenanceRequest_Success() {
//         User user = new User();
//         user.setUserId(1L);
//         Car car = new Car();
//         car.setCarId(1L);
//         car.setUser(user);
//         Repairman repairman = new Repairman();
//         repairman.setRepairmanId(1L);

//         when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//         when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
//         when(repairmanRepository.findAll()).thenReturn(Arrays.asList(repairman));
//         when(maintenanceItemRepository.save(any(MaintenanceItem.class)))
//             .thenAnswer(i -> i.getArgument(0));

//         MaintenanceItem result = userService.submitRepairRequest(1L, 1L, "维修描述");

//         assertNotNull(result);
//         assertEquals(MaintenanceStatus.PENDING, result.getStatus());
//         assertEquals(0, result.getProgress());
//         assertFalse(result.getRepairmen().isEmpty());
//     }

//     @Test
//     void submitMaintenanceRequest_UserNotFound() {
//         when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(ResourceNotFoundException.class, 
//             () -> userService.submitRepairRequest(1L, 1L, "维修描述"));
//     }

//     @Test
//     void submitMaintenanceRequest_CarNotFound() {
//         User user = new User();
//         user.setUserId(1L);

//         when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//         when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

//         assertThrows(ResourceNotFoundException.class, 
//             () -> userService.submitRepairRequest(1L, 1L, "维修描述"));
//     }
// } 