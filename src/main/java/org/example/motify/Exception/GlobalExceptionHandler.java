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
        return ExceptionLogger.logAndCreateErrorResponse(
                logger, ex, "Parameter validation failed - 参数验证失败", "Invalid request parameters - 请求参数不合法", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ExceptionLogger.logAndCreateErrorResponse(
                logger, ex, "Resource not found - 资源未找到", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        return ExceptionLogger.logAndCreateErrorResponse(
                logger, ex, "Bad request parameter - 请求参数错误", ex.getMessage(), HttpStatus.BAD_REQUEST);
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
        return ExceptionLogger.logAndCreateErrorResponse(
                logger, ex, "System exception - 系统异常", "Internal server error - 服务器内部错误", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}