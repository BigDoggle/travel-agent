package com.travel.agent.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.util.List;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 检查是否为 SSE (Server-Sent Events) 请求
     */
    private boolean isSseRequest() {
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        String acceptHeader = attributes.getRequest().getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("text/event-stream");
    }

    /**
     * 处理异步请求超时异常
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<Result<Void>> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e) {
        log.warn("异步请求超时： {}", e.getMessage());
        // 对于 SSE 请求，不返回响应体
        if (isSseRequest()) {
            log.warn("SSE 请求超时，忽略响应体写入");
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(Result.error("请求处理超时，请稍后重试"));
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        log.error("业务异常： {}", e.getMessage());
        // 对于 SSE 请求，不返回响应体，因为连接可能已经建立
        if (isSseRequest()) {
            log.warn("SSE 请求中发生业务异常，忽略响应体写入");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(e.getMessage()));
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String message = allErrors.get(0).getDefaultMessage();
        log.error("参数校验异常： {}", message);
        // 对于 SSE 请求，不返回响应体
        if (isSseRequest()) {
            log.warn("SSE 请求中发生参数校验异常，忽略响应体写入");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(message));
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常： {}", e.getMessage(), e);
        // 对于 SSE 请求，不返回响应体
        if (isSseRequest()) {
            log.warn("SSE 请求中发生运行时异常，忽略响应体写入");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("服务器内部错误，请稍后重试"));
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("系统异常： {}", e.getMessage(), e);
        // 对于 SSE 请求，不返回响应体
        if (isSseRequest()) {
            log.warn("SSE 请求中发生系统异常，忽略响应体写入");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("服务器内部错误，请稍后重试"));
    }
}
