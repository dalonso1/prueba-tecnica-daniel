package com.capgemini.test.code.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.capgemini.test.code.clients.CheckDniRequest;
import com.capgemini.test.code.clients.DniClient;
import com.capgemini.test.code.dto.request.CreateUserRequest;
import com.capgemini.test.code.exception.UserValidationException;
import com.capgemini.test.code.repository.UserRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UserValidationService {
    
    private final UserRepository userRepository;

    private final DniClient dniClient;

    public void validate(CreateUserRequest request) {
        validateName(request.getName());

        validateEmail(request.getEmail());

        validateRole(request.getRol());

        validateDni(request.getDni());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > 6) {
            throw new UserValidationException("userName");
        }
    }

    private void validateEmail(String email) {
        
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new UserValidationException("email");
        }

        if(userRepository.existsByEmail(email)) {
            throw new UserValidationException("email");
        }
    }

    private void validateRole(String role) {

        if (!"admin".equals(role) && !"superadmin".equals(role)) {
            throw new UserValidationException("rol");
        }
    }

    private void validateDni (String dni) {
        if (dni == null || dni.isBlank()) {
            throw new UserValidationException("dni");
        }

        try {
            dniClient.check(new CheckDniRequest(dni));

        } catch (FeignException exception) {
            
            if (exception.status() == HttpStatus.CONFLICT.value()) {
                throw new UserValidationException("dni");
            }

            throw exception;
        }
    }
}
