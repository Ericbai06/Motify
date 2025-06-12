package org.example.motify.Service;

import org.example.motify.Entity.Wage;
import org.example.motify.Entity.Repairman;
import org.example.motify.Entity.MaintenanceRecord;
import org.example.motify.Repository.RepairmanRepository;
import org.example.motify.Repository.MaintenanceRecordRepository;
import org.example.motify.Repository.WageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WageService {

    @Autowired
    private RepairmanRepository repairmanRepository;

    @Autowired
    private MaintenanceRecordRepository recordRepository;

    @Autowired
    private WageRepository wageRepository;

    /**
     * 计算并保存维修人员的月度工资
     * 
     * @param year  年份
     * @param month 月份
     * @return 计算并保存的工资记录列表
     */
    @Transactional
    public List<Wage> calculateMonthlyWages(int year, int month) {
        // 计算起止时间
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

        // 先删除该月份的所有工资记录，确保重复计算时不会有冲突
        List<Wage> existingWages = wageRepository.findByYearAndMonth(year, month);
        if (!existingWages.isEmpty()) {
            wageRepository.deleteAll(existingWages);
        }

        // 获取所有维修人员
        List<Repairman> repairmen = repairmanRepository.findAll();
        List<Wage> wages = new ArrayList<>();

        // 获取该月份所有维修记录
        List<MaintenanceRecord> allMonthRecords = recordRepository.findByStartTimeBetween(startDate, endDate);

        // 按维修人员分组
        Map<Long, List<MaintenanceRecord>> recordsByRepairman = allMonthRecords.stream()
                .collect(Collectors.groupingBy(MaintenanceRecord::getRepairManId));

        // 计算每个维修人员的工资
        for (Repairman repairman : repairmen) {
            // 获取该维修人员的记录
            List<MaintenanceRecord> records = recordsByRepairman.getOrDefault(
                    repairman.getRepairmanId(), new ArrayList<>());

            // 只有有工作记录的维修人员才计算工资
            if (!records.isEmpty()) {
                // 计算总工时（分钟）和收入
                double totalHours = records.stream()
                        .mapToLong(MaintenanceRecord::getWorkHours)
                        .sum() / 60.0; // 转换为小时

                double hourlyRate = repairman.getHourlyRate();
                double totalIncome = totalHours * hourlyRate;

                // 创建工资记录
                Wage wage = new Wage();
                wage.setRepairman(repairman); // 设置外键关系
                wage.setYear(year);
                wage.setMonth(month);
                wage.setTotalWorkHours(totalHours);
                wage.setTotalIncome(totalIncome);
                wage.setSettlementDate(LocalDateTime.now());
                wage.setRepairmanName(repairman.getName());
                wage.setRepairmanType(repairman.getType().toString());
                wage.setHourlyRate(hourlyRate);

                // 保存到数据库
                wages.add(wageRepository.save(wage));
            }
        }

        return wages;
    }

    /**
     * 获取指定月份的所有工资记录
     */
    public List<Wage> getMonthlyWages(int year, int month) {
        return wageRepository.findByYearAndMonth(year, month);
    }

    /**
     * 获取指定维修人员的所有工资记录
     */
    public List<Wage> getRepairmanWages(Long repairmanId) {
        return wageRepository.findByRepairmanIdOrderByYearDescMonthDesc(repairmanId);
    }
}
