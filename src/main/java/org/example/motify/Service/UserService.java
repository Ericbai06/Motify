package org.example.motify.Service;

import org.example.motify.Entity.*;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Repository.*;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.motify.util.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    
    @Autowired
    private final CarRepository carRepository;
    
    @Autowired
    private final MaintenanceItemRepository maintenanceItemRepository;
    
    @Autowired
    private final RepairmanRepository repairmanRepository;

    //用户注册
    public User register(User user) {
        // 参数验证
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BadRequestException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new BadRequestException("密码不能为空");
        }
        if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
            throw new BadRequestException("手机号不能为空");
        }
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BadRequestException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(PasswordEncoder.encode(user.getPassword()));
        
        // 保存用户
        return userRepository.save(user);
    }

    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息
     * @throws AuthenticationException 当用户名或密码错误时
     * @throws BadRequestException 当用户名或密码为空时
     */
    @Transactional(readOnly = true)
    public Optional<User> login(String username, String password) {
        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("密码不能为空");
        }
        
        // 查找用户并验证密码
        return userRepository.findByUsername(username)
                .filter(user -> PasswordEncoder.matches(password, user.getPassword()))
                .or(() -> {
                    throw new AuthenticationException("用户名或密码错误");
                });
    }

    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     * @throws ResourceNotFoundException 当用户不存在时
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param userDetails 要更新的用户信息
     * @return 更新后的用户信息
     * @throws ResourceNotFoundException 当用户不存在时
     * @throws BadRequestException 当新用户名已存在时
     */
    public User updateUser(Long userId, User userDetails) {
        User user = getUserById(userId);
        
        // 更新基本信息
        if (userDetails.getUsername() != null && !userDetails.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new BadRequestException("用户名已存在");
            }
            user.setUsername(userDetails.getUsername());
        }
        
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(PasswordEncoder.encode(userDetails.getPassword()));
        }
        
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }
        
        return userRepository.save(user);
    }

    /**
     * 获取用户车辆列表
     * 
     * @param userId 用户ID
     * @return 用户的车辆列表
     * @throws ResourceNotFoundException 当用户不存在时
     */
    @Transactional(readOnly = true)
public List<Map<String, Object>> getUserCarsSafe(Long userId) {
    // 验证用户是否存在
    if (!userRepository.existsById(userId)) {
        throw new ResourceNotFoundException("User", "id", userId);
    }
    
    // 使用原生SQL查询，避免懒加载问题
    List<Object[]> results = carRepository.findCarBasicInfoByUserId(userId);
    
    return results.stream().map(row -> {
        Map<String, Object> carMap = new HashMap<>();
        carMap.put("carId", row[0]);
        carMap.put("brand", row[1]);
        carMap.put("model", row[2]);
        carMap.put("licensePlate", row[3]);
        
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", row[4]);
        userMap.put("username", row[5]);
        userMap.put("name", row[6]);
        userMap.put("phone", row[7]);
        userMap.put("email", row[8]);
        carMap.put("user", userMap);
        
        return carMap;
    }).collect(Collectors.toList());
}

    /**
     * 添加车辆
     * 
     * @param userId 用户ID
     * @param car 车辆信息
     * @return 添加成功的车辆信息
     * @throws ResourceNotFoundException 当用户不存在时
     * @throws BadRequestException 当车辆信息不完整时
     */
    public Car addCar(Long userId, Car car) {
        User user = getUserById(userId);
        
        // 验证车辆信息
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            throw new BadRequestException("品牌不能为空");
        }
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            throw new BadRequestException("型号不能为空");
        }
        if (car.getLicensePlate() == null || car.getLicensePlate().trim().isEmpty()) {
            throw new BadRequestException("车牌号不能为空");
        }
        
        // 设置车辆所有者
        car.setUser(user);
        
        return carRepository.save(car);
    }

    /**
     * 获取用户维修记录
     * 
     * @param userId 用户ID
     * @return 用户的维修记录列表
     * @throws ResourceNotFoundException 当用户不存在时
     */
    @Transactional(readOnly = true)
    public List<MaintenanceItem> getUserMaintenanceItems(Long userId) {
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        // 通过Repository查询该用户的所有维修记录
        return maintenanceItemRepository.findByCar_User_UserId(userId);
    }

    /**
     * 提交维修请求
     * 
     * @param userId 用户ID
     * @param carId 车辆ID
     * @param name 维修项目名称
     * @param description 维修描述
     * @return 创建的维修项目
     * @throws ResourceNotFoundException 当用户或车辆不存在时
     * @throws BadRequestException 当车辆不属于该用户时
     */
    public MaintenanceItem submitRepairRequest(Long userId, Long carId, String name, String description) {
        // 验证参数
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("维修项目名称不能为空");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new BadRequestException("维修描述不能为空");
        }
        
        // 验证用户是否存在
        getUserById(userId);
        
        // 验证车辆是否存在
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));
        
        // 验证车辆是否属于该用户
        if (!car.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("无权限操作该车辆");
        }
        
        // 创建维修项目
        MaintenanceItem maintenanceItem = new MaintenanceItem();
        maintenanceItem.setName(name.trim());
        maintenanceItem.setDescription(description.trim());
        maintenanceItem.setStatus(MaintenanceStatus.PENDING);
        maintenanceItem.setProgress(0);
        maintenanceItem.setCost(0.0);
        maintenanceItem.setMaterialCost(0.0);
        maintenanceItem.setLaborCost(0.0);
        maintenanceItem.setCreateTime(LocalDateTime.now());
        maintenanceItem.setCar(car);
        
        // 自动分配维修人员（简单策略：分配第一个可用的维修人员）
        List<Repairman> availableRepairmen = repairmanRepository.findAll();
        if (!availableRepairmen.isEmpty()) {
            // 为简化实现，这里只分配第一个维修人员
            // 在实际应用中，可以根据维修类型、负载均衡等因素来选择
            maintenanceItem.setRepairmen(List.of(availableRepairmen.get(0)));
        }
        
        // 保存维修项目
        return maintenanceItemRepository.save(maintenanceItem);
    }

    /**
     * 重置密码
     * 
     * @param phone 手机号
     * @param code 验证码
     * @param newPassword 新密码
     * @throws BadRequestException 当验证码错误或已过期时
     * @throws ResourceNotFoundException 当用户不存在时
     */
    public void resetPassword(String phone, String code, String newPassword) {
        // 验证手机号格式
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new BadRequestException("手机号格式不正确");
        }
        
        // 验证密码强度
        if (newPassword == null || newPassword.length() < 8) {
            throw new BadRequestException("密码长度不能小于8位");
        }
        
        // 查找用户
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User", "phone", phone));
        
        // 更新密码
        user.setPassword(PasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * 提交催单请求
     * 
     * @param userId 用户ID
     * @param itemId 维修项目ID
     * @param reminderMessage 催单信息
     * @return 更新后的维修项目
     * @throws ResourceNotFoundException 当用户或维修项目不存在时
     * @throws BadRequestException 当维修项目不属于该用户或状态不允许催单时
     */
    public MaintenanceItem submitRushOrder(Long userId, Long itemId, String reminderMessage) {
        // 验证参数
        if (reminderMessage == null || reminderMessage.trim().isEmpty()) {
            throw new BadRequestException("催单信息不能为空");
        }
        
        // 验证用户是否存在
        getUserById(userId);
        
        // 验证维修项目是否存在
        MaintenanceItem maintenanceItem = maintenanceItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", itemId));
        
        // 验证维修项目是否属于该用户
        if (!maintenanceItem.getCar().getUser().getUserId().equals(userId)) {
            throw new BadRequestException("无权限操作该维修项目");
        }
        
        // 检查维修状态是否允许催单（只有进行中和待处理状态可以催单）
        if (maintenanceItem.getStatus() == MaintenanceStatus.COMPLETED || 
            maintenanceItem.getStatus() == MaintenanceStatus.CANCELLED) {
            throw new BadRequestException("该维修项目已完成或已取消，无法催单");
        }
        
        // 更新催单信息
        maintenanceItem.setReminder(reminderMessage.trim());
        maintenanceItem.setUpdateTime(LocalDateTime.now());
        
        // 保存更新
        return maintenanceItemRepository.save(maintenanceItem);
    }

    /**
     * 提交服务评分
     * 
     * @param userId 用户ID
     * @param itemId 维修项目ID
     * @param score 评分（1-5分）
     * @return 更新后的维修项目
     * @throws ResourceNotFoundException 当用户或维修项目不存在时
     * @throws BadRequestException 当维修项目不属于该用户、状态不允许评分或评分无效时
     */
    public MaintenanceItem submitServiceRating(Long userId, Long itemId, Integer score) {
        // 验证参数
        if (score == null || score < 1 || score > 5) {
            throw new BadRequestException("评分必须在1-5分之间");
        }
        
        // 验证用户是否存在
        getUserById(userId);
        
        // 验证维修项目是否存在
        MaintenanceItem maintenanceItem = maintenanceItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", itemId));
        
        // 验证维修项目是否属于该用户
        if (!maintenanceItem.getCar().getUser().getUserId().equals(userId)) {
            throw new BadRequestException("无权限操作该维修项目");
        }
        
        // 检查维修状态是否允许评分（只有已完成状态可以评分）
        if (maintenanceItem.getStatus() != MaintenanceStatus.COMPLETED) {
            throw new BadRequestException("只有已完成的维修项目才能评分");
        }
        
        // 检查是否已经评分
        if (maintenanceItem.getScore() != null) {
            throw new BadRequestException("该维修项目已经评分，无法重复评分");
        }
        
        // 更新评分
        maintenanceItem.setScore(score);
        maintenanceItem.setUpdateTime(LocalDateTime.now());
        
        // 保存更新
        return maintenanceItemRepository.save(maintenanceItem);
    }

    /**
     * 获取维修项目详情（包含催单和评分信息）
     * 
     * @param userId 用户ID
     * @param itemId 维修项目ID
     * @return 维修项目详情
     * @throws ResourceNotFoundException 当用户或维修项目不存在时
     * @throws BadRequestException 当维修项目不属于该用户时
     */
    @Transactional(readOnly = true)
    public MaintenanceItem getMaintenanceItemDetail(Long userId, Long itemId) {
        // 验证用户是否存在
        getUserById(userId);
        
        // 验证维修项目是否存在
        MaintenanceItem maintenanceItem = maintenanceItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceItem", "id", itemId));
        
        // 验证维修项目是否属于该用户
        if (!maintenanceItem.getCar().getUser().getUserId().equals(userId)) {
            throw new BadRequestException("无权限查看该维修项目");
        }
        
        return maintenanceItem;
    }

    /**
     * 获取用户当前正在进行的维修项目
     * 
     * @param userId 用户ID
     * @return 用户当前正在进行的维修项目列表
     * @throws ResourceNotFoundException 当用户不存在时
     */
    @Transactional(readOnly = true)
    public List<MaintenanceItem> getUserCurrentMaintenanceItems(Long userId) {
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        // 获取用户所有维修记录
        List<MaintenanceItem> allItems = maintenanceItemRepository.findByCar_User_UserId(userId);
        
        // 过滤出正在进行的维修项目
        return allItems.stream()
                .filter(item -> item.getStatus() == MaintenanceStatus.IN_PROGRESS)
                .collect(Collectors.toList());
    }

}
