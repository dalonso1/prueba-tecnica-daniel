package com.capgemini.test.code.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.capgemini.test.code.dto.request.CreateUserRequest;
import com.capgemini.test.code.dto.response.CreateUserResponse;
import com.capgemini.test.code.dto.response.UserResponse;
import com.capgemini.test.code.exception.GlobalExceptionHandler;
import com.capgemini.test.code.exception.RoomNotFoundException;
import com.capgemini.test.code.exception.UserNotFoundException;
import com.capgemini.test.code.exception.UserValidationException;
import com.capgemini.test.code.service.UserService;

@ExtendWith (MockitoExtension.class)
public class UserControllerTest {
    
    @Mock 
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach 
    void setUp() {
        UserController userController = new UserController(userService);

        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test 
    void shouldReturn201WhenUserIsCreated() throws Exception {

        Long roomId = 1L;

        String requestJson = """
            {
                "name": "Daniel",
                "email": "daniel@email.com",
                "phone": "677998899",
                "rol": "admin",
                "dni": "12345678A"
            }
        """;

        CreateUserResponse response = new CreateUserResponse("3");

        when(userService.createUser(eq(roomId), any(CreateUserRequest.class)))
            .thenReturn(response);

        ResultActions result = mockMvc.perform(
            post("/rooms/{roomId}/users", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        );

        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("3"));

        verify(userService).createUser(
            eq(roomId), 
            any(CreateUserRequest.class)
        );
    }

    @Test 
    void shouldReturn200WhenUserExists() throws Exception {

        Long roomId = 1L;
        Long userId = 3L;

        UserResponse response = new UserResponse(
            userId,
            "daniel",
            "daniel@email.com",
            "677998899",
            "admin",
            "12345678A",
            roomId
        );

        when(userService.getUser(roomId, userId))
            .thenReturn(response);

        mockMvc.perform(
            get("/rooms/{roomId}/users/{userId}", roomId, userId)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(3))
        .andExpect(jsonPath("$.name").value("daniel"))
        .andExpect(jsonPath("$.email").value("daniel@email.com"))
        .andExpect(jsonPath("$.phone").value("677998899"))
        .andExpect(jsonPath("$.rol").value("admin"))
        .andExpect(jsonPath("$.dni").value("12345678A"))
        .andExpect(jsonPath("$.roomId").value(1L));

        verify(userService).getUser(roomId, userId);
    }

    @Test 
    void shouldReturn409WhenValidationFails() throws Exception {

        Long roomId = 1L;

        String requestJson = """
            {
                "name": "Daniel",
                "email": "invalid-email",
                "phone": "677998899",
                "rol": "admin",
                "dni": "12345678A"
            }
        """;

        when(userService.createUser(
            eq(roomId), 
            any(CreateUserRequest.class)
        )).thenThrow(new UserValidationException("email"));

        mockMvc.perform(
            post("/rooms/{roomId}/users", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value(409))
        .andExpect(jsonPath("$.message").value("error validation email"));

        verify(userService).createUser(
            eq(roomId), 
            any(CreateUserRequest.class)
        );
    }

    @Test 
    void shouldReturn404WhenRoomDoesNotExist() throws Exception {

        Long roomId = 999L;

        String requestJson = """
            {
                "name": "daniel",
                "email": "daniel@email.com",
                "phone": "677998899",
                "rol": "admin",
                "dni": "12345678A"
            }
        """;

        when(userService.createUser(
            eq(roomId), 
            any(CreateUserRequest.class)
        )).thenThrow(new RoomNotFoundException(roomId));

        mockMvc.perform(
            post("/rooms/{roomId}/users", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(404))
        .andExpect(jsonPath("$.message").value("Room not found: 999"));

        verify(userService).createUser(
            eq(roomId), 
            any(CreateUserRequest.class)
        );
    }

    @Test 
    void shouldReturn404WhenUserDoesNotExist() throws Exception {

        Long roomId = 1L;
        Long userId = 999L;

        when(userService.getUser(roomId, userId))
            .thenThrow(new UserNotFoundException(userId, roomId));

        mockMvc.perform(
            get("/rooms/{roomId}/users/{userId}", roomId, userId)
        )
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(404))
        .andExpect(jsonPath("$.message").value("User 999 not found in room 1"));

        verify(userService).getUser(roomId, userId);
    }
}
