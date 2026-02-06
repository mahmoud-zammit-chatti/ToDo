package com.mahmoud.todo.domain.exceptions;

public class OperationNotPossibleException extends RuntimeException {
    public OperationNotPossibleException(String message) {
        super(message);
    }
}
