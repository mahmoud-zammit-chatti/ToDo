package com.mahmoud.todo.domain.exceptions;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException(String message){
        super(message);
    }
}
