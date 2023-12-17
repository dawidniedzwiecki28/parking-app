package com.niedzwiadek.parking.account.api;

public class AccountAlreadyExistException extends RuntimeException{
    public AccountAlreadyExistException(String email) {
        super("account with email: " + email + " already exist");
    }
}
