package org.example.motify.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        logger.error("HTTP Media Type Not Supported Exception: {}", ex.getMessage());
        logger.error("Supported media types: {}", ex.getSupportedMediaTypes());
        
        Map<String, Object> body = new HashMap<>();
        body.put("code", 415);
        body.put("message", "Unsupported Media Type - Content-Type not supported: " + ex.getContentType());
        body.put("data", null);
        body.put("supportedTypes", ex.getSupportedMediaTypes());
        
        return new ResponseEntity<>(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ExceptionLogger.createValidationErrorResponse(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String resourceName = ex.getResourceName();
        String fieldName = ex.getFieldName();
        Object fieldValue = ex.getFieldValue();
        
        logger.error("资源未找到: {} 的 {} = {}", resourceName, fieldName, fieldValue);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 404);
        response.put("message", ex.getMessage());
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        logger.error("请求参数错误: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", ex.getMessage());
        response.put("data", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        logger.error("认证错误: {}", ex.getMessage());
        return ExceptionLogger.createAuthenticationErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error("数据完整性约束违反: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "数据约束错误: 可能存在重复的唯一键或违反了外键约束");
        response.put("data", null);
        response.put("error", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        logger.error("约束验证错误: {}", ex.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", "请求参数验证失败");
        response.put("data", null);
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        String msg = ex.getMessage();
        int code = 400;
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (msg != null) {
            if (msg.contains("不存在") || msg.contains("not found")) {
                code = 404;
                status = HttpStatus.NOT_FOUND;
            } else if (msg.contains("已存在") || msg.contains("already exists")) {
                code = 400;
                status = HttpStatus.BAD_REQUEST;
            } else if (msg.contains("密码错误") || msg.contains("未登录") || msg.contains("password error") || msg.contains("not logged in")) {
                code = 401;
                status = HttpStatus.UNAUTHORIZED;
            }
        }
        body.put("code", code);
        body.put("message", msg);
        body.put("data", null);
        logger.error("Runtime Exception - 运行时异常: ", ex);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> handleAllUncaughtException(Exception ex) {
        logger.error("未捕获异常: ", ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", "服务器内部错误");
        response.put("data", null);
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}