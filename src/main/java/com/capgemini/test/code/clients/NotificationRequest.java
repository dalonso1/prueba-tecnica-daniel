package com.capgemini.test.code.clients;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
@JsonInclude (JsonInclude.Include.NON_NULL)
public class NotificationRequest {
    
    private String email;
    private String phone;
    private String message;
}
