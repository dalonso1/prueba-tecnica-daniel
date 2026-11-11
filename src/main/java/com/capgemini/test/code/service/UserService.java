package com.capgemini.test.code.service;

import org.springframework.stereotype.Service;

import com.capgemini.test.code.repository.RoomRepository;
import com.capgemini.test.code.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UserService {
    
    private final UserRepository userRepository;

    private final RoomRepository roomRepository;
}
