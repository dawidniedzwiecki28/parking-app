package com.niedzwiadek.parking.rest.exceptions;

public class InvalidLoginRequest extends RuntimeException {
  public InvalidLoginRequest(final String message) {
    super(message);
  }
}
