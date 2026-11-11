package com.capgemini.test.code.exception;

public class UserValidationException extends RuntimeException{
    
    public UserValidationException(String field) {
        super("error validation " + field);
    }
}
