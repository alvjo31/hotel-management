package com.hotel.booking_system.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    ResponseEntity<?>handleBaseException(BaseException ex){
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("error" , ex.getErrorCode(),
                        "message", ex.getMessage()));
    }


    @ExceptionHandler(NoClassDefFoundError.class)
    ResponseEntity<?>handleException(NoClassDefFoundError ex){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error" ,"Internal Server Error",
                        "message", "Dicka shkoi gabim"));
    }
}
