package com.niedzwiadek.parking.rest;

import com.niedzwiadek.parking.account.api.AccountAlreadyExistException;
import com.niedzwiadek.parking.account.api.AccountNotFoundException;
import com.niedzwiadek.parking.car.api.CarNotFoundException;
import com.niedzwiadek.parking.rest.exceptions.InvalidUserCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(InvalidUserCredentialsException.class)
  public ResponseEntity<String> handleBadRequestException(final Exception ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({CarNotFoundException.class, AccountNotFoundException.class})
  public ResponseEntity<String> handleNotFoundException(final Exception ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(AccountAlreadyExistException.class)
  public ResponseEntity<String> handleConflictException(final Exception ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
  }
}
