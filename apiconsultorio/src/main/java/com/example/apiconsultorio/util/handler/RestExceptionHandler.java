package com.example.apiconsultorio.util.handler;

import com.example.apiconsultorio.util.error.ResourceNotFoundDetails;
import com.example.apiconsultorio.util.error.ResourceNotFoundException;
import com.example.apiconsultorio.util.error.ValidateAtributesDetails;
import com.example.apiconsultorio.util.error.ValidateAtributesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handlerResourceNotFoundException(ResourceNotFoundException rfnException){
        ResourceNotFoundDetails rfnDetails = ResourceNotFoundDetails.Builder
                .newBuilder()
                .timestamp(new Date().getTime())
                .status(HttpStatus.NOT_FOUND.value())
                .title("Recurso não encontrado!")
                .details(rfnException.getMessage())
                .developerMessage(rfnException.getClass().getName())
                .build();

        return  new ResponseEntity<>(rfnDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidateAtributesException.class)
    public ResponseEntity<?> ResourceValidateAtributesException(ValidateAtributesException rfnException){
        ValidateAtributesDetails rfnDetails = ValidateAtributesDetails.Builder
                .newBuilder()
                .timestamp(new Date().getTime())
                .status(HttpStatus.BAD_REQUEST.value())
                .title("Atributo inválido!")
                .details(rfnException.getMessage())
                .developerMessage(rfnException.getClass().getName())
                .build();

        return  new ResponseEntity<>(rfnDetails, HttpStatus.BAD_REQUEST);
    }

}
