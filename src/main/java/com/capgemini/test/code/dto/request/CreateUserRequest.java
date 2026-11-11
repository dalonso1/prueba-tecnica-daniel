package com.capgemini.test.code.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
public class CreateUserRequest {
    
    private String name;
    private String email;
    private String phone;
    private String rol;
    private String dni;
}
