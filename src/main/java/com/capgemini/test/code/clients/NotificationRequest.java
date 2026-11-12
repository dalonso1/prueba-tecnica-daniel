package com.capgemini.test.code.clients;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class NotificationRequest {
    
    private String recipient;

    private String message;
}
