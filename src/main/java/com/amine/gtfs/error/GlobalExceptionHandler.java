package com.amine.gtfs.error;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(GtfsImportException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(GtfsImportException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            Instant.now().toString(),
            422,
            "Unprocessable Entity",
            ex.getMessage(),
            request.getRequestURI()
        );
            return ResponseEntity.status(422).body(errorResponse);
        }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            Instant.now().toString(),
            500,
            "Internal Server Error",
            "An unexpected error occurred. Please contact support.",
            request.getRequestURI()
        );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }