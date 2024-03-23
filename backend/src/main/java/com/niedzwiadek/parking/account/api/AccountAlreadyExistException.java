package com.niedzwiadek.parking.account.api;

public class AccountAlreadyExistException extends RuntimeException {
  public AccountAlreadyExistException(final String email) {
    super("account with email: " + email + " already exist");
  }
}
