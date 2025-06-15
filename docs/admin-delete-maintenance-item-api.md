# Admin删除工单API使用说明

## 接口信息
- **URL**: `DELETE /api/admin/maintenance-items/{itemId}`
- **功能**: 删除指定的维修工单
- **权限**: 管理员
- **说明**: 利用数据库级联删除功能，删除工单时会自动删除相关的维修记录、材料使用记录、工单维修工关联等数据

## 请求参数
- **路径参数**:
  - `itemId` (Long): 要删除的工单ID

## 使用示例

### 1. cURL 示例
```bash
curl -X DELETE http://localhost:8080/api/admin/maintenance-items/123 \
  -H "Content-Type: application/json"
```

### 2. JavaScript/Fetch 示例
```javascript
// 删除ID为123的工单
fetch('http://localhost:8080/api/admin/maintenance-items/123', {
  method: 'DELETE',
  headers: {
    'Content-Type': 'application/json',
  }
})
.then(response => response.json())
.then(data => {
  if (data.success) {
    console.log('工单删除成功:', data.message);
    console.log('删除的工单ID:', data.itemId);
  } else {
    console.error('删除失败:', data.message);
  }
})
.catch(error => {
  console.error('请求失败:', error);
});
```

### 3. Ajax 示例
```javascript
$.ajax({
  url: 'http://localhost:8080/api/admin/maintenance-items/123',
  type: 'DELETE',
  contentType: 'application/json',
  success: function(data) {
    if (data.success) {
      alert('工单删除成功！');
      // 刷新工单列表或执行其他操作
      location.reload();
    } else {
      alert('删除失败：' + data.message);
    }
  },
  error: function(xhr, status, error) {
    alert('请求失败：' + error);
  }
});
```

## 响应格式

### 成功响应 (HTTP 200)
```json
{
  "success": true,
  "message": "维修工单删除成功",
  "itemId": 123
}
```

### 错误响应

#### 工单不存在 (HTTP 400)
```json
{
  "success": false,
  "message": "MaintenanceItem not found with id: 123"
}
```

#### 服务器错误 (HTTP 500)
```json
{
  "success": false,
  "message": "删除维修工单失败：[具体错误信息]"
}
```

## 级联删除说明

删除工单时，会自动删除以下相关数据：
1. **item_repairman** - 工单与维修工的关联记录
2. **maintenance_records** - 维修记录
3. **record_material** - 材料使用记录
4. **required_repairman_types** - 工种需求记录

这些删除操作通过数据库的外键约束级联删除自动完成，无需手动删除。

## 注意事项

1. **不可恢复**: 删除操作是不可逆的，请谨慎使用
2. **权限控制**: 该接口应该只对管理员开放
3. **状态检查**: 建议在删除前检查工单状态，避免删除正在进行的重要工单
4. **日志记录**: 系统会自动记录删除操作的日志，便于追踪

## 前端集成建议

### 确认对话框
```javascript
function deleteMaintenanceItem(itemId) {
  if (confirm('确定要删除这个工单吗？此操作不可撤销！')) {
    fetch(`/api/admin/maintenance-items/${itemId}`, {
      method: 'DELETE'
    })
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        alert('删除成功！');
        // 刷新列表
        loadMaintenanceItems();
      } else {
        alert('删除失败：' + data.message);
      }
    });
  }
}
```

### 批量删除
```javascript
function batchDeleteItems(itemIds) {
  if (confirm(`确定要删除选中的 ${itemIds.length} 个工单吗？`)) {
    const promises = itemIds.map(id => 
      fetch(`/api/admin/maintenance-items/${id}`, { method: 'DELETE' })
    );
    
    Promise.all(promises)
      .then(responses => Promise.all(responses.map(r => r.json())))
      .then(results => {
        const successCount = results.filter(r => r.success).length;
        alert(`成功删除 ${successCount} 个工单`);
        loadMaintenanceItems();
      });
  }
}
```
