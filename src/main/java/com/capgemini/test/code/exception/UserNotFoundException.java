package com.capgemini.test.code.exception;

public class UserNotFoundException extends RuntimeException{
    
    public UserNotFoundException(Long userId, Long roomId) {
        super("User " + userId + " not found in room " + roomId);
    }
}
