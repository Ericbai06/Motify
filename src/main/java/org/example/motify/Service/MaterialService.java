package org.example.motify.Service;

import org.example.motify.Entity.Material;
import org.example.motify.Entity.RecordMaterial;
import org.example.motify.Repository.MaterialRepository;
import org.example.motify.Repository.RecordMaterialRepository;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 材料管理服务
 * 负责材料库存管理、材料使用记录等业务逻辑
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MaterialService {
    private static final Logger logger = LoggerFactory.getLogger(MaterialService.class);

    @Autowired
    private final MaterialRepository materialRepository;
    @Autowired
    private final RecordMaterialRepository recordMaterialRepository;

    /**
     * 检查并使用材料（减少库存）
     * @param materialsToUse 材料使用列表，包含materialId和amount
     * @param recordId 维修记录ID
     * @throws BadRequestException 当库存不足时
     */
    @Transactional
    public void useMaterials(List<Map<String, Object>> materialsToUse, Long recordId) {
        if (materialsToUse == null || materialsToUse.isEmpty()) {
            return;
        }

        logger.info("开始处理材料使用，维修记录ID: {}, 材料数量: {}", recordId, materialsToUse.size());

        // 第一步：检查所有材料库存是否充足
        List<String> insufficientMaterials = new ArrayList<>();
        for (Map<String, Object> materialInfo : materialsToUse) {
            Long materialId = Long.valueOf(materialInfo.get("materialId").toString());
            Integer amount = Integer.valueOf(materialInfo.get("amount").toString());
            
            if (amount <= 0) {
                throw new BadRequestException("材料使用数量必须大于0");
            }
            
            // 检查材料是否存在
            Material material = materialRepository.findById(materialId)
                    .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
            
            // 检查库存是否充足
            Boolean isStockSufficient = materialRepository.isStockSufficient(materialId, amount);
            if (isStockSufficient == null || !isStockSufficient) {
                Integer currentStock = materialRepository.getCurrentStock(materialId);
                insufficientMaterials.add(String.format("%s(需要:%d, 库存:%d)", 
                        material.getName(), amount, currentStock != null ? currentStock : 0));
            }
        }
        
        // 如果有库存不足的材料，抛出异常
        if (!insufficientMaterials.isEmpty()) {
            String errorMessage = "以下材料库存不足：" + String.join(", ", insufficientMaterials);
            logger.warn("材料库存不足: {}", errorMessage);
            throw new BadRequestException(errorMessage);
        }
        
        // 第二步：执行材料使用和库存减少
        for (Map<String, Object> materialInfo : materialsToUse) {
            Long materialId = Long.valueOf(materialInfo.get("materialId").toString());
            Integer amount = Integer.valueOf(materialInfo.get("amount").toString());
            
            // 减少库存
            int updatedRows = materialRepository.reduceStock(materialId, amount);
            if (updatedRows == 0) {
                // 这种情况理论上不应该发生，因为之前已经检查过库存
                Material material = materialRepository.findById(materialId).orElse(null);
                String materialName = material != null ? material.getName() : "ID:" + materialId;
                String errorMessage = "材料 " + materialName + " 库存更新失败，可能是并发冲突导致库存不足";
                logger.error(errorMessage);
                throw new BadRequestException(errorMessage);
            }
            
            // 记录材料使用
            RecordMaterial rm = new RecordMaterial();
            rm.setRecordId(recordId);
            rm.setMaterialId(materialId);
            rm.setAmount(amount);
            recordMaterialRepository.save(rm);
            
            logger.debug("成功使用材料: ID={}, 数量={}, 维修记录ID={}", materialId, amount, recordId);
        }
        
        logger.info("材料使用处理完成，维修记录ID: {}", recordId);
    }

    /**
     * 恢复材料库存（用于删除维修记录或撤销操作）
     * @param recordId 维修记录ID
     */
    @Transactional
    public void restoreMaterialStock(Long recordId) {
        logger.info("开始恢复材料库存，维修记录ID: {}", recordId);
        
        List<RecordMaterial> recordMaterials = recordMaterialRepository.findByRecordId(recordId);
        
        for (RecordMaterial rm : recordMaterials) {
            materialRepository.increaseStock(rm.getMaterialId(), rm.getAmount());
            logger.debug("恢复材料库存: 材料ID={}, 数量={}", rm.getMaterialId(), rm.getAmount());
        }
        
        logger.info("材料库存恢复完成，维修记录ID: {}, 恢复材料种类: {}", recordId, recordMaterials.size());
    }

    /**
     * 获取低库存材料列表
     * @param threshold 库存阈值，默认为10
     * @return 低库存材料列表
     */
    @Transactional(readOnly = true)
    public List<Material> getLowStockMaterials(Integer threshold) {
        if (threshold == null || threshold < 0) {
            threshold = 10; // 默认阈值
        }
        return materialRepository.findByStockRange(0, threshold);
    }

    /**
     * 检查单个材料库存是否充足
     * @param materialId 材料ID
     * @param requiredAmount 需要数量
     * @return 是否库存充足
     */
    @Transactional(readOnly = true)
    public boolean checkStockSufficiency(Long materialId, Integer requiredAmount) {
        Boolean result = materialRepository.isStockSufficient(materialId, requiredAmount);
        return result != null && result;
    }

    /**
     * 获取材料当前库存
     * @param materialId 材料ID
     * @return 当前库存数量
     */
    @Transactional(readOnly = true)
    public Integer getCurrentStock(Long materialId) {
        return materialRepository.getCurrentStock(materialId);
    }

    /**
     * 人工调整库存（入库、出库、盘点等）
     * @param materialId 材料ID
     * @param amount 调整数量（正数为增加，负数为减少）
     * @param reason 调整原因
     */
    @Transactional
    public void adjustStock(Long materialId, Integer amount, String reason) {
        // 检查材料是否存在
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", "id", materialId));
        
        if (amount == 0) {
            return; // 不需要调整
        }
        
        if (amount > 0) {
            // 增加库存
            materialRepository.increaseStock(materialId, amount);
            logger.info("增加材料库存: {} (ID: {}), 数量: {}, 原因: {}", 
                    material.getName(), materialId, amount, reason);
        } else {
            // 减少库存
            Integer currentStock = materialRepository.getCurrentStock(materialId);
            if (currentStock == null || currentStock < Math.abs(amount)) {
                throw new BadRequestException(String.format("材料 %s 当前库存 %d 不足，无法减少 %d", 
                        material.getName(), currentStock != null ? currentStock : 0, Math.abs(amount)));
            }
            
            int updatedRows = materialRepository.reduceStock(materialId, Math.abs(amount));
            if (updatedRows == 0) {
                throw new BadRequestException("库存调整失败，可能是并发冲突");
            }
            
            logger.info("减少材料库存: {} (ID: {}), 数量: {}, 原因: {}", 
                    material.getName(), materialId, Math.abs(amount), reason);
        }
    }
}
