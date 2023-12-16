package com.niedzwiadek.parking.account.api;

import lombok.NonNull;

import java.util.UUID;

public record AccountId(@NonNull UUID value) {

    public static AccountId random() {
        return new AccountId(UUID.randomUUID());
    }

    public static AccountId from(UUID id) {
        return new AccountId(id);
    }

    public static AccountId fromString(final String string) {
        return AccountId.from(UUID.fromString(string));
    }

    public static AccountId ofNullable(final String accountId) {
        return accountId != null ? AccountId.fromString(accountId) : null;
    }

    public static AccountId ofNullable(final UUID accountId) {
        return accountId != null ? AccountId.from(accountId) : null;
    }

    public String serialize() {
        return value.toString();
    }
}
