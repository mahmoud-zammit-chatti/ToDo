package com.mahmoud.todo.api.exceptions;


import com.mahmoud.todo.domain.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final HttpServletRequest request;

    public GlobalExceptionHandler(HttpServletRequest request) {
        this.request = request;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        HttpStatus.CONFLICT.value(),
                        "Conflict",
                        ex.getMessage(),
                        request.getRequestURI(),
                        null
                        )

                );
    }

    @ExceptionHandler(RessourceNotFoundException.class)
    public ResponseEntity<ApiError> handleRessourceNotFoundException(RessourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI(), null ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> genericException(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage(), request.getRequestURI(), null));
    }
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> invalidPasswordException(InvalidPasswordException ex){
         List<ValidationErrorDetail> errorDetails = new java.util.ArrayList<>();
         ValidationErrorDetail errorDetail=new ValidationErrorDetail("password","Invalid Password");
         errorDetails.add(errorDetail);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new ApiError(HttpStatus.NOT_ACCEPTABLE.value(), "Invalid Password", ex.getMessage(), request.getRequestURI(),errorDetails));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ApiError> handleNotAuthorizedException(NotAuthorizedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError(HttpStatus.FORBIDDEN.value(), "Forbidden", ex.getMessage(), request.getRequestURI(), null));
    }

    @ExceptionHandler(OperationNotPossibleException.class)
    public ResponseEntity<ApiError> handleOperationNotPossibleException(OperationNotPossibleException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(HttpStatus.BAD_REQUEST.value(),"Bad Request", ex.getMessage(), request.getRequestURI(), null));
    }
}
