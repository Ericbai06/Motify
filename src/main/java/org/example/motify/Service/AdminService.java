package org.example.motify.Service;

import org.example.motify.Entity.*;
import org.example.motify.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {
    @Autowired
    private final UserRepository userRepository;
    
    @Autowired
    private final RepairmanRepository repairmanRepository;
    
    @Autowired
    private final CarRepository carRepository;
    
    @Autowired
    private final MaintenanceItemRepository maintenanceItemRepository;
    
    @Autowired
    private final MaterialRepository materialRepository;
    
    @Autowired
    private final SalaryRepository salaryRepository;
} 