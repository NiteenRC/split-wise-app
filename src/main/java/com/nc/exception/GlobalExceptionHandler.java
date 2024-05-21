package com.nc.exception;

import com.nc.utility.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CreationException.class)
    public ResponseEntity<HttpResponse> handleCreationException(CreationException ex) {
        HttpResponse response = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<HttpResponse> handleConflictException(DuplicateException ex) {
        HttpResponse response = new HttpResponse(HttpStatus.CONFLICT.name(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<HttpResponse> handleNotFoundException(NotFoundException ex) {
        HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND.name(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<HttpResponse> handleHttpClientErrorExceptionUnauthorized(UnauthorizedException ex) {
        HttpResponse response = new HttpResponse(HttpStatus.UNAUTHORIZED.name(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<HttpResponse> handleIllegalStateException(IllegalStateException ex) {
        HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND.name(), null, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

