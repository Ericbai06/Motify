package org.example.motify.Service;

import org.example.motify.Entity.*;
import org.example.motify.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RepairmanRepository repairmanRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private MaintenanceItemRepository MaintenanceItemRepository;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
} 