package org.example.motify.Service;

import org.example.motify.Entity.MaintenanceItem;
import org.example.motify.Exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.motify.Repository.MaintenanceItemRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Enum.RepairmanType;

@Service
public class BatchRepairService {

    @Autowired
    private RepairmanService repairmanService;

    @Autowired
    private MaintenanceItemRepository maintenanceItemRepository;

    @Transactional
    public List<MaintenanceItem> batchSubmitRepairRequests(List<Map<String, Object>> requests) {
        List<MaintenanceItem> createdItems = new ArrayList<>();

        try {
            for (Map<String, Object> requestBody : requests) {
                Long userId = Long.valueOf(requestBody.get("userId").toString());
                Long carId = Long.valueOf(requestBody.get("carId").toString());
                String name = (String) requestBody.get("name");
                String description = (String) requestBody.get("description");

                // 处理工种需求
                Map<String, Integer> requiredTypesRaw = (Map<String, Integer>) requestBody.get("requiredTypes");
                Map<RepairmanType, Integer> requiredTypes = new HashMap<>();

                for (Map.Entry<String, Integer> entry : requiredTypesRaw.entrySet()) {
                    requiredTypes.put(RepairmanType.valueOf(entry.getKey()), entry.getValue());
                }

                MaintenanceItem item = repairmanService.submitRepairRequest(
                        userId, carId, name, description, requiredTypes);
                if (requiredTypesRaw == null || requiredTypesRaw.size() == 0) {
                    item.setStatus(MaintenanceStatus.AWAITING_ASSIGNMENT);
                } else {
                    item.setStatus(MaintenanceStatus.PENDING);
                }
                maintenanceItemRepository.save(item);
                createdItems.add(item);
            }
            return createdItems;
        } catch (Exception e) {
            // 事务会自动回滚
            throw new BadRequestException("批量处理维修请求失败: " + e.getMessage());
        }
    }
}
