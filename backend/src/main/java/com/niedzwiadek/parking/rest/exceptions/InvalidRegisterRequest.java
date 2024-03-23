package com.niedzwiadek.parking.rest.exceptions;

public class InvalidRegisterRequest extends RuntimeException {
  public InvalidRegisterRequest(final String message) {
    super(message);
  }
}
