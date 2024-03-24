package com.niedzwiadek.parking.account.api;

public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException(final AccountId accountId) {
    super("Account not found for id: " + accountId);
  }

  public AccountNotFoundException(final String email) {
    super("Account not found for email: " + email);
  }
}
