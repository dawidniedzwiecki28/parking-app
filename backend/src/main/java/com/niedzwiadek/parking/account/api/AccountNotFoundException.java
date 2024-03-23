package com.niedzwiadek.parking.account.api;

public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException(final AccountId accountId) {
    super("account not found for id: " + accountId);
  }

  public AccountNotFoundException(final String email) {
    super("account not found for email: " + email);
  }
}
