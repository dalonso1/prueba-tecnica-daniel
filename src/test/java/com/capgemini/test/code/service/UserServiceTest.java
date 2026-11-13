package com.capgemini.test.code.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import com.capgemini.test.code.dto.response.UserResponse;
import com.capgemini.test.code.entity.Room;
import com.capgemini.test.code.entity.User;
import com.capgemini.test.code.exception.RoomNotFoundException;
import com.capgemini.test.code.exception.UserNotFoundException;
import com.capgemini.test.code.exception.UserValidationException;
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

    @Test 
    void shouldNotSaveUserWhenValidationFails() {

        Long roomId = 1L;

        CreateUserRequest request = new CreateUserRequest();
        request.setName("Daniel");
        request.setEmail("invalid-email");

        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.findById(roomId))
            .thenReturn(Optional.of(room));
        
        doThrow(new UserValidationException("email"))
            .when(userValidationService)
            .validate(request);
        
            assertThrows(
                UserValidationException.class, 
                () -> userService.createUser(roomId, request)
            );

        verify(userValidationService).validate(request);

        verifyNoInteractions(
            userRepository,
            notificationService
        );
    }

    @Test 
    void shouldReturnUserWhenExistsInRoom() {

        Long roomId = 1L;
        Long userId = 3L;

        Room room = new Room();
        room.setId(roomId);
        room.setName("Sala 1");

        User user = new User();
        user.setId(userId);
        user.setName("Daniel");
        user.setEmail("daniel@email.com");
        user.setPhone("677998899");
        user.setRole("admin");
        user.setDni("12345678A");
        user.setRoom(room);

        when(userRepository.findByIdAndRoomId(userId, roomId))
            .thenReturn(Optional.of(user));

        UserResponse response = userService.getUser(roomId, userId);

        assertEquals(userId, response.getId());
        assertEquals("Daniel", response.getName());
        assertEquals("daniel@email.com", response.getEmail());
        assertEquals("677998899", response.getPhone());
        assertEquals("admin", response.getRol());
        assertEquals("12345678A", response.getDni());
        assertEquals(roomId, response.getRoomId());

        verify(userRepository).findByIdAndRoomId(userId, roomId);
        verifyNoInteractions(
            roomRepository,
            userValidationService,
            notificationService
        );
    }

    @Test 
    void shouldThrowExceptionWhenUserDoesNotExistsInRoom() {

        Long roomId = 1L;
        Long userId = 999L;

        when(userRepository.findByIdAndRoomId(userId, roomId))
            .thenReturn(Optional.empty());
        
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class, 
            () -> userService.getUser(roomId, userId)
        );

        assertEquals(
            "User 999 not found in room 1", 
            exception.getMessage()
        );

        verify(userRepository).findByIdAndRoomId(userId, roomId);

        verifyNoInteractions(
            roomRepository,
            userValidationService,
            notificationService
        );
    }
}
