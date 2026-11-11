package com.capgemini.test.code.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class UserResponse {
    
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String rol;
    private String dni;
    private Long roomId;
}
