package org.example.motify.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 数据库触发器初始化组件
 * 负责在应用启动时读取SQL文件中的触发器定义并执行
 */
@Component
public class TriggerInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(TriggerInitializer.class);

    private final JdbcTemplate jdbcTemplate;
    private boolean initialized = false;

    @Autowired
    public TriggerInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (initialized) {
            logger.debug("触发器已初始化，跳过");
            return;
        }

        try {
            logger.info("开始初始化数据库触发器");

            // 初始化自动计算费用触发器（工单）
            initializeCostTrigger();

            // 初始化自动分配状态触发器
            initializeAssignStatusTrigger();

            // 初始化自动计算维修记录费用触发器
            initializeRecordCostTrigger();

            initialized = true;
            logger.info("触发器初始化完成");
        } catch (Exception e) {
            logger.error("初始化失败", e);
        }
    }

    private void initializeCostTrigger() throws Exception {
        // 读取SQL文件内容
        String sqlFileName = "AutoCalculateCostTrigger.sql";
        String triggerSql = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(sqlFileName).toURI())));

        // 先检查并删除已存在的触发器，防止冲突
        logger.debug("移除已存在的触发器: trg_maintenanceitem_completed");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_maintenanceitem_completed");

        // 执行创建触发器SQL
        logger.info("创建新触发器: trg_maintenanceitem_completed");
        jdbcTemplate.execute(triggerSql);
        logger.info("费用计算触发器初始化完成");
    }

    private void initializeAssignStatusTrigger() throws Exception {
        // 读取SQL文件内容
        String sqlFileName = "AutoAssignStatusTrigger.sql";
        String triggerSql = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(sqlFileName).toURI())));

        // 先检查并删除已存在的触发器，防止冲突
        logger.debug("移除已存在的触发器: trg_required_repairman_type_updated");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_required_repairman_type_updated");

        // 执行创建触发器SQL
        logger.info("创建新触发器: trg_required_repairman_type_updated");
        jdbcTemplate.execute(triggerSql);
        logger.info("自动分配状态触发器初始化完成");
    }

    private void initializeRecordCostTrigger() throws Exception {
        // 读取SQL文件内容
        String sqlFileName = "AutoCalcutateReocrdCostTrigger.sql";
        String triggerSql = new String(Files.readAllBytes(
                Paths.get(getClass().getClassLoader().getResource(sqlFileName).toURI())));

        // 先检查并删除已存在的触发器，防止冲突
        logger.debug("移除已存在的触发器: trg_maintenancerecord_cost");
        jdbcTemplate.execute("DROP TRIGGER IF EXISTS trg_maintenancerecord_cost");

        // 执行创建触发器SQL
        logger.info("创建新触发器: trg_maintenancerecord_cost");
        jdbcTemplate.execute(triggerSql);
        logger.info("维修记录费用计算触发器初始化完成");
    }
}