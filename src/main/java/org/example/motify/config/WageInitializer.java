package org.example.motify.config;

import org.example.motify.Entity.MaintenanceRecord;
import org.example.motify.Entity.Wage;
import org.example.motify.Repository.MaintenanceRecordRepository;
import org.example.motify.Repository.WageRepository;
import org.example.motify.Service.WageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 在应用启动时初始化工资数据
 * 检查系统中所有维修记录，确保所有月份的工资都已结算
 */
@Component
public class WageInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(WageInitializer.class);

    @Autowired
    private WageService wageService;

    @Autowired
    private WageRepository wageRepository;

    @Autowired
    private MaintenanceRecordRepository recordRepository;

    @Value("${motify.wage.initialize-on-startup:true}")
    private boolean initializeOnStartup;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!initializeOnStartup) {
            logger.info("工资初始化已禁用，跳过初始化过程");
            return;
        }

        logger.info("开始检查并初始化工资数据...");

        try {
            // 1. 获取系统中最早的维修记录时间
            LocalDateTime earliestRecord = getEarliestRecordDate();
            if (earliestRecord == null) {
                logger.info("系统中没有维修记录，无需结算工资");
                return;
            }

            // 2. 获取最早记录的年月
            int startYear = earliestRecord.getYear();
            int startMonth = earliestRecord.getMonthValue();

            // 3. 获取当前时间的上个月（最近一个应结算的月份）
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            int endYear = lastMonth.getYear();
            int endMonth = lastMonth.getMonthValue();

            logger.info("将检查从 {}-{} 到 {}-{} 的工资数据", startYear, startMonth, endYear, endMonth);

            // 4. 获取已经结算过的年月集合
            Set<String> calculatedMonths = getCalculatedMonths();

            // 5. 遍历所有需要检查的月份
            LocalDate currentDate = LocalDate.of(startYear, startMonth, 1);
            LocalDate endDate = LocalDate.of(endYear, endMonth, 1);

            while (!currentDate.isAfter(endDate)) {
                int year = currentDate.getYear();
                int month = currentDate.getMonthValue();
                String yearMonthKey = year + "-" + month;

                // 检查该月是否有维修记录
                boolean hasRecords = hasMaintenanceRecords(year, month);

                // 如果有记录但未结算，则进行结算
                if (hasRecords && !calculatedMonths.contains(yearMonthKey)) {
                    logger.info("开始结算 {}-{} 的工资", year, month);
                    wageService.calculateMonthlyWages(year, month);
                    logger.info("{}-{} 工资结算完成", year, month);
                }

                // 移至下一个月
                currentDate = currentDate.plusMonths(1);
            }

            logger.info("工资数据初始化完成");

        } catch (Exception e) {
            logger.error("工资初始化过程中发生错误", e);
        }
    }

    /**
     * 获取系统中最早的维修记录日期
     */
    private LocalDateTime getEarliestRecordDate() {
        List<MaintenanceRecord> records = recordRepository.findTop1ByOrderByStartTimeAsc();
        return records.isEmpty() ? null : records.get(0).getStartTime();
    }

    /**
     * 检查指定年月是否已经结算过工资
     */
    private Set<String> getCalculatedMonths() {
        List<Wage> allWages = wageRepository.findAll();
        return allWages.stream()
                .map(wage -> wage.getYear() + "-" + wage.getMonth())
                .collect(Collectors.toSet());
    }

    /**
     * 检查指定年月是否有维修记录
     */
    private boolean hasMaintenanceRecords(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return recordRepository.countByStartTimeBetween(startOfMonth, endOfMonth) > 0;
    }
}