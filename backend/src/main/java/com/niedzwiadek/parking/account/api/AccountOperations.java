package com.niedzwiadek.parking.account.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

public interface AccountOperations {
    UserDetails save(@NonNull String name, @NonNull String email, @NonNull String password);
    void rename(@NonNull AccountId id, @NonNull String newName);
    void delete(@NonNull AccountId id);
    boolean existByEmail(@NonNull String userEmail);
    UserDetails getByEmail(@NonNull String userEmail);
    AccountId findAccountIdByEmail(@NonNull String userEmail);
    AccountData getById(@NonNull AccountId accountId);

    @Data
    @Builder
    class AccountData {
        AccountId accountId;
        String name;
    }
}
