package com.example.apiconsultorio.util.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidateAtributesException extends RuntimeException{
    public ValidateAtributesException(String message){
        super(message);
    }
}

