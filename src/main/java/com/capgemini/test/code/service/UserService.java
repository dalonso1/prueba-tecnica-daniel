package com.capgemini.test.code.service;

import org.springframework.stereotype.Service;

import com.capgemini.test.code.dto.request.CreateUserRequest;
import com.capgemini.test.code.dto.response.CreateUserResponse;
import com.capgemini.test.code.dto.response.UserResponse;
import com.capgemini.test.code.entity.Room;
import com.capgemini.test.code.entity.User;
import com.capgemini.test.code.exception.RoomNotFoundException;
import com.capgemini.test.code.exception.UserNotFoundException;
import com.capgemini.test.code.repository.RoomRepository;
import com.capgemini.test.code.repository.UserRepository;
import com.capgemini.test.code.service.notification.NotificationService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class UserService {
    
    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    private final UserValidationService userValidationService;

    private final NotificationService notificationService;

    public CreateUserResponse createUser(Long roomId, CreateUserRequest request) {

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RoomNotFoundException(roomId));

        userValidationService.validate(request);

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDni(request.getDni());
        user.setRole(request.getRol());
        user.setRoom(room);

        User saveUser = userRepository.save(user);

        notificationService.notifyUser(
            saveUser.getRole(),
            saveUser.getEmail(), 
            saveUser.getPhone()
        );

        return new CreateUserResponse(
            saveUser.getId().toString()
        );
    }

    public UserResponse getUser(Long roomId, Long userId) {
        
        User user = userRepository.findByIdAndRoomId(userId, roomId)
            .orElseThrow(() -> new UserNotFoundException(userId, roomId));
        
            return new UserResponse(
                user.getId(), 
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getDni(),
                user.getRoom().getId()
            );
    }
}
