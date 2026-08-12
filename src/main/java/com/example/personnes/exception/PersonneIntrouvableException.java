package com.example.personnes.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


public class PersonneIntrouvableException extends RuntimeException {
    public PersonneIntrouvableException(String message) {
        super(message);
    }
}
