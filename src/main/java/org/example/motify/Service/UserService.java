package org.example.motify.Service;

import org.example.motify.Entity.*;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Repository.*;
import org.example.motify.util.PasswordEncoder;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;
    
    @Autowired
    private RepairmanRepository repairmanRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
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
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
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
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
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
    public List<Car> getUserCars(Long userId) {
        User user = getUserById(userId);
        return user.getCars();
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
    public List<MaintenanceRecord> getUserMaintenanceRecords(Long userId) {
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        // 通过Repository查询该用户的所有维修记录
        return maintenanceRecordRepository.findByCar_User_UserId(userId);
    }

    /**
     * 提交维修请求
     * 
     * @param userId 用户ID
     * @param carId 车辆ID
     * @param description 维修描述
     * @return 创建的维修记录
     * @throws ResourceNotFoundException 当用户或车辆不存在时
     * @throws BadRequestException 当车辆不属于用户或描述为空时
     */
    public MaintenanceRecord submitRepairRequest(Long userId, Long carId, String description) {
        // 验证用户和车辆
        User user = getUserById(userId);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));
        
        // 验证车辆是否属于该用户
        if (!car.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("该车辆不属于当前用户");
        }
        
        // 验证描述
        if (description == null || description.trim().isEmpty()) {
            throw new BadRequestException("维修描述不能为空");
        }
        
        // 创建维修记录
        MaintenanceRecord record = new MaintenanceRecord();
        record.setCar(car);
        record.setDescription(description);
        record.setStatus(MaintenanceStatus.PENDING); // 使用枚举类型设置待处理状态
        record.setProgress(0); // 初始进度
        record.setRepairman(new java.util.ArrayList<>()); // 初始化维修人员列表
        
        // 创建记录信息
        RecordInfo recordInfo = new RecordInfo();
        recordInfo.setMaintenanceRecord(record);
        record.setRecordInfo(recordInfo);
        // 随机分配维修人员
        List<Repairman> availableRepairmen = repairmanRepository.findAll();
        if (availableRepairmen.isEmpty()) {
            throw new BadRequestException("当前没有可用的维修人员");
        }
        
        // 随机选择一个维修人员
        int randomIndex = (int) (Math.random() * availableRepairmen.size());
        record.getRepairman().add(availableRepairmen.get(randomIndex));
        
        return maintenanceRecordRepository.save(record);
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
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
