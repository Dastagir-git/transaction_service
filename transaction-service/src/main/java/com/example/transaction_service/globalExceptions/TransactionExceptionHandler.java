package com.example.transaction_service.globalExceptions;

import com.example.transaction_service.CustomizeException.AccountNotFoundException;
import com.example.transaction_service.CustomizeException.InsufficientBalanceException;
import com.example.transaction_service.CustomizeException.ServerDownException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TransactionExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> accountNotFound(AccountNotFoundException e){
        return new ResponseEntity<>("Account not found on this number", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServerDownException.class)
    public ResponseEntity<String> accountNotFound(ServerDownException e){
        return new ResponseEntity<>("Can't retrieve data due to Server Down", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> insufficientBalance(InsufficientBalanceException insufficientBalance){
        return new ResponseEntity<>("Transaction failed due to Insufficient Balance",HttpStatus.BAD_REQUEST);
    }
}
