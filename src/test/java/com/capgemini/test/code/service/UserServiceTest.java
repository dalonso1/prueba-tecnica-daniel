package com.capgemini.test.code.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capgemini.test.code.dto.request.CreateUserRequest;
import com.capgemini.test.code.dto.response.CreateUserResponse;
import com.capgemini.test.code.entity.Room;
import com.capgemini.test.code.entity.User;
import com.capgemini.test.code.exception.RoomNotFoundException;
import com.capgemini.test.code.repository.RoomRepository;
import com.capgemini.test.code.repository.UserRepository;
import com.capgemini.test.code.service.notification.NotificationService;

@ExtendWith (MockitoExtension.class)
public class UserServiceTest {
    
    @Mock 
    private UserRepository userRepository;

    @Mock 
    private RoomRepository roomRepository;

    @Mock 
    private  UserValidationService userValidationService;

    @Mock 
    private  NotificationService notificationService;

    @InjectMocks 
    private UserService userService;

    @Test 
    void shouldThrowExceptionWhenRoomDoesNotExist() {

        Long roomId = 999L;

        CreateUserRequest request = new CreateUserRequest();

        when(roomRepository.findById(roomId))
            .thenReturn(Optional.empty());

        assertThrows(
            RoomNotFoundException.class, 
            () -> userService.createUser(roomId, request)
        );

        verifyNoInteractions(
            userValidationService,
            userRepository,
            notificationService
        );
    }

    @Test 
    void shouldCreateUserSuccessfully() {

        Long roomId = 1L;

        CreateUserRequest request = new CreateUserRequest();
        request.setName("Daniel");
        request.setEmail("daniel@email.com");
        request.setPhone("677998899");
        request.setRol("admin");
        request.setDni("12345678A");

        Room room = new Room();
        room.setId(roomId);
        room.setName("Sala 1");

        when(roomRepository.findById(roomId))
            .thenReturn(Optional.of(room));

        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(3L);
                return user;
            });
        
        CreateUserResponse response = userService.createUser(roomId, request);

        assertEquals("3", response.getId());

        verify(userValidationService).validate(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User saveUser = userCaptor.getValue();

        assertEquals("Daniel", saveUser.getName());
        assertEquals("daniel@email.com", saveUser.getEmail());
        assertEquals("677998899", saveUser.getPhone());
        assertEquals("admin", saveUser.getRole());
        assertEquals("12345678A", saveUser.getDni());
        assertEquals(room, saveUser.getRoom());

        verify(notificationService).notifyUser(
            "admin", 
            "daniel@email.com",
            "677998899"
        );
    }
}
