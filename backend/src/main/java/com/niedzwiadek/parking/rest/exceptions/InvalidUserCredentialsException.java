package com.niedzwiadek.parking.rest.exceptions;

public class InvalidUserCredentialsException extends RuntimeException {
  public InvalidUserCredentialsException(final String message) {
    super(message);
  }
}
