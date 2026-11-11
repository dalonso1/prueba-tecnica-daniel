package com.capgemini.test.code.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.capgemini.test.code.clients.CheckDniRequest;
import com.capgemini.test.code.clients.DniClient;
import com.capgemini.test.code.dto.request.CreateUserRequest;
import com.capgemini.test.code.exception.UserValidationException;
import com.capgemini.test.code.repository.UserRepository;

import feign.Feign;
import feign.FeignException;

@ExtendWith (MockitoExtension.class)
public class UserValidationServiceTest {
    
    @Mock 
    private UserRepository userRepository;

    @Mock 
    private DniClient dniClient;

    @InjectMocks 
    private UserValidationService userValidationService;

    @Test 
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        CreateUserRequest request = createValidRequest();

        when(userRepository.existsByEmail("daniel@test.com"))
            .thenReturn(true);

        UserValidationException exception = assertThrows(
            UserValidationException.class, 
            () -> userValidationService.validate(request)
        );

        assertEquals(
            "error validation email", 
            exception.getMessage());

        verify(userRepository).existsByEmail("daniel@test.com");

        verifyNoInteractions(dniClient);
    }
    
    @ParameterizedTest 
    @ValueSource (strings = {"Alejandro", "   "})
    @NullAndEmptySource 
    void shouldThrowExceptionWhenNameIsInvalid(String name) {

        CreateUserRequest request = createValidRequest();
        request.setName(name);

        UserValidationException exception = assertThrows(
            UserValidationException.class, 
            () -> userValidationService.validate(request)
        );

        assertEquals(
            "error validation userName", 
            exception.getMessage()
        );

        verifyNoInteractions(userRepository, dniClient);
    }

    @ParameterizedTest 
    @ValueSource (strings = {
        "correo",
        "correo.com",
        "correo@dominio",
    })
    @NullAndEmptySource 
    void shouldThrowExceptionWhenEmailIsInvalid(String email) {

        CreateUserRequest request = createValidRequest();
        request.setEmail(email);

        UserValidationException exception = assertThrows(
            UserValidationException.class, 
            () -> userValidationService.validate(request)
        );

        assertEquals(
            "error validation email", 
            exception.getMessage()
        );

        verifyNoInteractions(userRepository, dniClient);
    }

    @ParameterizedTest 
    @ValueSource (strings = {
        "user",
        "ADMIN",
        "SUPERADMIN"
    })
    @NullAndEmptySource 
    void shouldThrowExceptionWhenRoleIsInvalid(String role) {

        CreateUserRequest request = createValidRequest();
        request.setRol(role);

        UserValidationException exception = assertThrows(
            UserValidationException.class, 
            () -> userValidationService.validate(request)
        );

        assertEquals(
            "error validation rol", 
            exception.getMessage()
        );

        verify(userRepository).existsByEmail("daniel@test.com");

        verifyNoInteractions(dniClient);
    }

    @ParameterizedTest 
    @ValueSource (strings = {"admin", "superadmin"})
    void shouldValidateUserSuccessfully(String role) {

        CreateUserRequest request = createValidRequest();
        request.setRol(role);
        
        assertDoesNotThrow(
            () -> userValidationService.validate(request)
        );

        verify(userRepository).existsByEmail("daniel@test.com");
        verify(dniClient).check(any(CheckDniRequest.class));
    }

    @Test 
    void shouldThrowExceptionWhenDniIsInvalid() {

        CreateUserRequest request = createValidRequest();

        FeignException exception = creaFeignException(409);

        when(dniClient.check(any(CheckDniRequest.class)))
            .thenThrow(exception);

        UserValidationException validationException = assertThrows(
            UserValidationException.class, 
            () -> userValidationService.validate(request)
        );

        assertEquals(
            "error validation dni", 
            validationException.getMessage()
        );

        verify(dniClient).check(any(CheckDniRequest.class));
    }

        @Test 
    void shouldPropagateExceptionWhenDniServiceFails() {

        CreateUserRequest request = createValidRequest();

        FeignException exception = creaFeignException(500);

        when(dniClient.check(any(CheckDniRequest.class)))
            .thenThrow(exception);

        FeignException thrownException = assertThrows(
            FeignException.class, 
            () -> userValidationService.validate(request)
        );

        assertEquals(500, thrownException.status());

        verify(dniClient).check(any(CheckDniRequest.class));
    }

    @ParameterizedTest 
    @NullAndEmptySource 
    void shouldThrowExceptionWhenDniIsEmpty(String dni) {

        CreateUserRequest request = createValidRequest();
        request.setDni(dni);

        UserValidationException exception = assertThrows(
            UserValidationException.class, 
            () -> userValidationService.validate(request)
        );

        assertEquals(
            "error validation dni", 
            exception.getMessage()
        );
        
        verifyNoInteractions(dniClient);
    }

    private FeignException creaFeignException(int status) {

        feign.Request request = feign.Request.create(
            feign.Request.HttpMethod.PATCH, 
            "/check-dni", 
            java.util.Map.of(), 
            null, 
            java.nio.charset.StandardCharsets.UTF_8,
            null
        );

        feign.Response response = feign.Response.builder()
            .status(status)
            .reason(HttpStatus.valueOf(status).getReasonPhrase())
            .request(request)
            .build();
        
            return FeignException.errorStatus("DniClient#check", response);
    }

    private CreateUserRequest createValidRequest() {
        
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Daniel");
        request.setEmail("daniel@test.com");
        request.setRol("admin");
        request.setDni("12345678A");

        return request;
    }
}
