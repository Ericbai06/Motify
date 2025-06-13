package org.example.motify.Enum;

public enum MaintenanceStatus {
    PENDING, // 待处理
    AWAITING_ASSIGNMENT, // 等待管理员分配维修工种和数量
    ACCEPTED, // 已接收
    CANCELLED, // 已拒绝
    IN_PROGRESS, // 维修中
    COMPLETED // 已完成
}
