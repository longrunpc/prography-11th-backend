package com.longrunpc.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.longrunpc.common.error.ErrorCode;
import com.longrunpc.common.error.GlobalErrorCode;
import com.longrunpc.common.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
            .body(ApiResponse.error(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ErrorCode errorCode = GlobalErrorCode.INTERNAL_ERROR;
        return ResponseEntity.status(errorCode.getStatus())
            .body(ApiResponse.error(errorCode));
    }
}
