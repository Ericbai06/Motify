package org.example.motify.Service;

import org.example.motify.Entity.*;
import org.example.motify.Repository.*;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.AuthenticationException;
import org.example.motify.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j // 添加日志注解
public class AdminService {
    @Autowired
    private final AdminRepository adminRepository;
    
    @Autowired
    private final UserRepository userRepository;
    
    @Autowired
    private final RepairmanRepository repairmanRepository;
    
    @Autowired
    private final CarRepository carRepository;
    
    @Autowired
    private final MaintenanceItemRepository maintenanceItemRepository;
    
    @Autowired
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    
    @Autowired
    private final WageRepository wageRepository;

    public Admin registerAdmin(Admin admin) {
        log.info("Starting admin registration, username: {}", admin.getUsername());

        try {
            // 验证必填字段
            log.debug("Validating username...");
            if (admin.getUsername() == null || admin.getUsername().trim().isEmpty()) {
                throw new BadRequestException("用户名不能为空");
            }

            log.debug("Validating password...");
            if (admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
                throw new BadRequestException("密码不能为空");
            }

            log.debug("Validating name...");
            if (admin.getName() == null || admin.getName().trim().isEmpty()) {
                throw new BadRequestException("姓名不能为空");
            }

            log.debug("Validating email...");
            if (admin.getEmail() == null || admin.getEmail().trim().isEmpty()) {
                throw new BadRequestException("邮箱不能为空");
            }

            // 检查用户名是否已存在
            log.debug("Checking if username exists: {}", admin.getUsername());
            Optional<Admin> existingAdmin = adminRepository.findByUsername(admin.getUsername());
            log.debug("Username exists: {}", existingAdmin.isPresent());
            if (existingAdmin.isPresent()) {
                throw new BadRequestException("用户名已存在");
            }

            // 加密密码
            log.debug("Encrypting password...");
            String originalPassword = admin.getPassword();
            admin.setPassword(PasswordEncoder.encode(originalPassword));
            log.debug("Password encryption completed");

            // 设置默认值
            log.debug("Setting default values...");
            admin.setLastLoginTime(LocalDateTime.now());
            log.debug("Setting active to true...");
            admin.setActive(true);
            log.debug("Active value: {}, type: {}", admin.getActive(), admin.getActive().getClass().getSimpleName());

            // 保存管理员
            log.info("Saving admin to database...");
            Admin savedAdmin = adminRepository.save(admin);
            log.info("Admin saved successfully, ID: {}", savedAdmin.getAdminId());

            return savedAdmin;

        } catch (Exception e) {
            log.error("Admin registration failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Admin> loginAdmin(String username, String password) {
        // 参数验证
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("密码不能为空");
        }

        // 查找管理员
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isEmpty()) {
            throw new AuthenticationException("用户名或密码错误");
        }

        Admin admin = adminOpt.get();

        // 验证密码
        if (!PasswordEncoder.matches(password, admin.getPassword())) {
            throw new AuthenticationException("用户名或密码错误");
        }

        // 检查账户是否激活
        if (!admin.isActive()) {
            throw new AuthenticationException("账户已被禁用");
        }

        // 更新最后登录时间
        admin.setLastLoginTime(LocalDateTime.now());
        adminRepository.save(admin);

        return Optional.of(admin);
    }

    /**
     * 根据ID查找管理员
     */
    @Transactional(readOnly = true)
    public Optional<Admin> findById(Long adminId) {
        if (adminId == null) {
            throw new BadRequestException("管理员ID不能为空");
        }
        return adminRepository.findById(adminId);
    }

    /**
     * 获取所有用户信息
     * 
     * @return 所有用户列表
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Admin querying all users");
        return userRepository.findAll();
    }

    /**
     * 获取所有维修人员信息
     * 
     * @return 所有维修人员列表
     */
    @Transactional(readOnly = true)
    public List<Repairman> getAllRepairmen() {
        log.info("Admin querying all repairmen");
        return repairmanRepository.findAll();
    }

    /**
     * 获取所有车辆信息
     * 
     * @return 所有车辆列表
     */
    @Transactional(readOnly = true)
    public List<Car> getAllCars() {
        log.info("Admin querying all cars");
        return carRepository.findAll();
    }

    /**
     * 获取所有维修工单信息，包含关联的维修记录
     * 
     * @return 所有维修工单列表（包含维修记录）
     */
    @Transactional(readOnly = true)
    public List<MaintenanceItem> getAllMaintenanceItems() {
        log.info("Admin querying all maintenance items with records");
        return maintenanceItemRepository.findAllWithRecords();
    }

    /**
     * 获取所有历史维修记录
     * 
     * @return 所有历史维修记录列表
     */
    @Transactional(readOnly = true)
    public List<MaintenanceRecord> getAllMaintenanceRecords() {
        log.info("Admin querying all maintenance records");
        return maintenanceRecordRepository.findAll();
    }

    /**
     * 获取所有工时费发放记录
     * 
     * @return 所有工时费发放记录列表
     */
    @Transactional(readOnly = true)
    public List<Wage> getAllWages() {
        log.info("Admin querying all wage records");
        return wageRepository.findAll();
    }
}