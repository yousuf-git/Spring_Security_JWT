// This class is used to handle the exceptions thrown by the application. It is annotated with @RestControllerAdvice to make it a global exception handler.

package com.learning.security.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.learning.security.dtos.ResponseMessage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<?> handleValidationExceptions(Exception ex) {
    //     log.error(ex.getClass() + ex.getMessage());
    //     return new ResponseEntity<>(new ResponseMessage("Server Error, Try later !"), HttpStatus.INTERNAL_SERVER_ERROR);
    // }
}