package org.example.motify.config;

import org.example.motify.Service.WageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    @Autowired
    private WageService wageService;

    /**
     * 每月1日凌晨2点执行上个月的工资结算
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void monthlyWageSettlement() {
        // 获取上个月的年份和月份
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        int year = lastMonth.getYear();
        int month = lastMonth.getMonthValue();

        logger.info("开始执行{}年{}月的月度工资结算", year, month);

        try {
            // 调用WageService计算并保存工资
            // 注意：Service会先删除当月已有记录，确保重复执行也是安全的
            wageService.calculateMonthlyWages(year, month);
            logger.info("{}年{}月的月度工资结算完成", year, month);
        } catch (Exception e) {
            logger.error("月度工资结算失败: " + e.getMessage(), e);
        }
    }

    /**
     * 提供手动触发月度工资结算的方法，用于测试或手动补算
     * 
     * @param year  年份
     * @param month 月份(1-12)
     */
    public void manualWageSettlement(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("月份必须在1-12之间");
        }

        logger.info("手动触发{}年{}月的月度工资结算", year, month);

        try {
            // 调用WageService计算并保存工资
            // 注意：Service会先删除当月已有记录，确保重复执行也是安全的
            wageService.calculateMonthlyWages(year, month);
            logger.info("{}年{}月的月度工资结算完成", year, month);
        } catch (Exception e) {
            logger.error("月度工资结算失败: " + e.getMessage(), e);
        }
    }
}