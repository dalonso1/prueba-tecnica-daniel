package com.capgemini.test.code.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capgemini.test.code.dto.request.CreateUserRequest;
import com.capgemini.test.code.dto.response.CreateUserResponse;
import com.capgemini.test.code.dto.response.UserResponse;
import com.capgemini.test.code.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController 
@RequestMapping ("/rooms")
@RequiredArgsConstructor 
public class UserController {
    
    private final UserService userService;

    @PostMapping("/{roomId}/users")
    public ResponseEntity<CreateUserResponse> createUser(
        @PathVariable  Long roomId,
        @RequestBody CreateUserRequest request) {
        
        CreateUserResponse response = userService.createUser(roomId, request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    @GetMapping("/{roomId}/users/{userId}")
    public ResponseEntity<UserResponse> getUser(
        @PathVariable Long roomId,
        @PathVariable Long userId) {

        UserResponse response = userService.getUser(roomId, userId);
        return ResponseEntity.ok(response);
    }
    
}
