package com.capgemini.test.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class ErrorResponse {
    
    private int code;
    private String message;
}
