package com.example.personnes.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class EmaildejaUtiliserException extends RuntimeException{

    public EmaildejaUtiliserException(String message) {
        super(message);
    }
}
