package org.example.motify.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
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
                logger, ex, "参数验证失败", "请求参数不合法", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ExceptionLogger.logAndCreateErrorResponse(
                logger, ex, "资源未找到", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        return ExceptionLogger.logAndCreateErrorResponse(
                logger, ex, "请求参数错误", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        String msg = ex.getMessage();
        int code = 400;
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (msg != null) {
            if (msg.contains("不存在")) {
                code = 404;
                status = HttpStatus.NOT_FOUND;
            } else if (msg.contains("已存在")) {
                code = 400;
                status = HttpStatus.BAD_REQUEST;
            } else if (msg.contains("密码错误") || msg.contains("未登录")) {
                code = 401;
                status = HttpStatus.UNAUTHORIZED;
            }
        }
        body.put("code", code);
        body.put("message", msg);
        body.put("data", null);
        logger.error("RuntimeException: ", ex);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<?> handleAllUncaughtException(Exception ex) {
        return ExceptionLogger.logAndCreateErrorResponse(
                logger, ex, "系统异常", "服务器内部错误");
    }
} 