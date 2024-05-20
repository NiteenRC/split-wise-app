package com.nc.exception;

import com.nc.utility.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CreationException.class)
    public ResponseEntity<HttpResponse> handleBrandCreationException(CreationException ex) {
        HttpResponse response = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<HttpResponse> handleBrandConflictException(ConflictException ex) {
        HttpResponse response = new HttpResponse(HttpStatus.CONFLICT.name(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<HttpResponse> handleUserNotFoundException(NotFoundException ex) {
        HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND.name(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

