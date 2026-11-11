package com.capgemini.test.code.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.capgemini.test.code.dto.response.ErrorResponse;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler (UserValidationException.class)
    public ResponseEntity<ErrorResponse> handleUserValidationException(
        UserValidationException exception) {
            ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage()
            );
            
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(error);
        }
}
