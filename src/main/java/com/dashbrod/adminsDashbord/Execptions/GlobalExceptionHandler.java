package com.dashbrod.adminsDashbord.Execptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors =
                ex.getBindingResult()

                .getFieldErrors()

                .stream()

                .map(error -> error.getField() + ": " + error.getDefaultMessage())

                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);

    }
    
}
