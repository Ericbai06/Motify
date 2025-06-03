package org.example.motify.trigger;

import jakarta.persistence.*;
import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Entity.RecordInfo;
import org.example.motify.Enum.MaintenanceStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MaintenanceItemTrigger {
    
    @PreUpdate
    public void onStatusChange(MaintenanceItem item) {
        // 检查状态是否变为已完成
        if (item.getStatus() == MaintenanceStatus.COMPLETED) {
            // 创建新的记录信息
            RecordInfo recordInfo = new RecordInfo();
            recordInfo.setMaintenanceItem(item);
            
            // 设置完成时间
            recordInfo.setCompleteTime(LocalDateTime.now());
            
            // 计算材料费用
            double materialCost = item.getMaintenanceRecords().stream()
                    .mapToDouble(record -> record.calculateMaterialCost())
                    .sum();
            recordInfo.setMaterialCost(materialCost);

            // 计算工时费用
            double laborCost = item.getRepairmen().stream()
                    .mapToDouble(repairman -> repairman.getHourlyRate() * item.getWorkHours())
                    .sum();
            recordInfo.setLaborCost(laborCost);

            // 更新总金额
            recordInfo.updateTotalAmount();
            
            // 添加到记录信息列表
            if (item.getRecordInfos() == null) {
                item.setRecordInfos(new ArrayList<>());
            }
            item.getRecordInfos().add(recordInfo);
        }
    }
} 