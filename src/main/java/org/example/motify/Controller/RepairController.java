package org.example.motify.Controller;

import org.example.motify.Entity.*;
import org.example.motify.Enum.RepairmanType;
import org.example.motify.Service.RepairmanService;
import org.example.motify.Service.BatchRepairService;
import org.example.motify.Exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/repair")
public class RepairController {

    @Autowired
    private RepairmanService repairmanService;

    @Autowired
    private BatchRepairService batchRepairService;

    /**
     * 提交维修工单并根据工种需求自动分配维修人员
     * <b>请求示例：</b>
     * 
     * <pre>
     * {
     *   "userId": 10,
     *   "carId": 12,
     *   "name": "Auto Assignment Test",
     *   "description": "Testing auto assignment of repairmen",
     *   "requiredTypes": {
     *     "MECHANIC": 2,
     *     "PAINTER": 1,
     *     "APPRENTICE": 1
     *   }
     * }
     * </pre>
     *
     * @param requestBody 包含工单信息和工种需求的请求体
     * @return 创建的维修工单，包含已分配的维修人员
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitRepairRequest(@RequestBody Map<String, Object> requestBody) {
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

        return ExceptionLogger.createSuccessResponse(item);
    }

    @PostMapping("/batch-submit")
    public ResponseEntity<?> batchSubmitRepairRequests(@RequestBody List<Map<String, Object>> requests) {
        List<MaintenanceItem> items = batchRepairService.batchSubmitRepairRequests(requests);
        return ExceptionLogger.createSuccessResponse(items);
    }
}