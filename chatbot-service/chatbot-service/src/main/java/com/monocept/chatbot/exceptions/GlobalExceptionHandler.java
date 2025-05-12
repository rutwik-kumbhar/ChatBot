package com.monocept.chatbot.exceptions;


import com.monocept.chatbot.enums.Status;
import com.monocept.chatbot.model.response.MasterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MasterResponse<?>> handleValidationException(MethodArgumentNotValidException ex){
        BindingResult bindingResult= ex.getBindingResult();

        List<ValidationErrorResponse.FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(error -> new ValidationErrorResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(fieldErrors);
        MasterResponse<?> response = new MasterResponse<>(Status.FAILURE.name(), HttpStatus.BAD_REQUEST.value(), Status.FAILURE.name(),errorResponse);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
}
