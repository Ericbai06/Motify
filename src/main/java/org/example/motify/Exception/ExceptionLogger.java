package org.example.motify.Exception;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public class ExceptionLogger {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ExceptionLogger.class);

    /**
     * 记录错误日志
     *
     * @param message 错误消息
     * @param e 异常对象
     */
    public static void logError(String message, Throwable e) {
        logger.error(message + ": {}", e.getMessage(), e);
    }

    /**
     * 记录异常并创建标准错误响应
     *
     * @param logger 日志记录器
     * @param e 捕获的异常
     * @param errorTitle 错误标题
     * @param errorMessage 错误消息
     * @param status HTTP状态码
     * @return 包含错误信息的ResponseEntity
     */
    public static ResponseEntity<?> logAndCreateErrorResponse(
            Logger logger,
            Throwable e,
            String errorTitle,
            String errorMessage,
            HttpStatus status) {
        logger.error(errorTitle + ": {}", e.getMessage(), e);
        Map<String, String> response = new HashMap<>();
        response.put("error", errorTitle);
        response.put("message", errorMessage);
        return new ResponseEntity<>(response, status);
    }

    /**
     * 记录异常并创建标准错误响应（使用默认500状态码）
     */
    public static ResponseEntity<?> logAndCreateErrorResponse(
            Logger logger,
            Throwable e,
            String errorTitle,
            String errorMessage) {
        return logAndCreateErrorResponse(logger, e, errorTitle, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @return 包含数据的ResponseEntity
     */
    public static ResponseEntity<?> createSuccessResponse(Object data) {
        return ResponseEntity.ok(data);
    }

    /**
     * 创建成功消息响应
     *
     * @param message 成功消息
     * @return 包含消息的ResponseEntity
     */
    public static ResponseEntity<?> createSuccessMessageResponse(String message) {
        return ResponseEntity.ok(Collections.singletonMap("message", message));
    }
} 