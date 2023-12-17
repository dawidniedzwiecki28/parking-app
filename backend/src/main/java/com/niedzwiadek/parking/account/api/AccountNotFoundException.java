package com.niedzwiadek.parking.account.api;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(AccountId accountId) {
        super("account not found for id: " + accountId);
    }
}
