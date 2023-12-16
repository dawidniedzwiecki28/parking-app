package com.niedzwiadek.parking.account.domain;

import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.account.api.AccountOperations;
import com.niedzwiadek.parking.account.infrastructure.AccountEntity;
import com.niedzwiadek.parking.account.infrastructure.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
class AccountOperationsImpl implements AccountOperations {

    private static final Logger log = LoggerFactory.getLogger(AccountOperationsImpl.class);
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails save(@NonNull String name, @NonNull String email, @NonNull String password) {
        final var user = AccountEntity.builder()
                .createdAt(Instant.now())
                .email(email)
                .name(name)
                .id(UUID.randomUUID())
                .role(AccountEntity.Role.ADMIN)
                .password(password)
                .build();
        log.info("Saving user {}", user);
        accountRepository.saveAndFlush(user);
        log.info("User saved {}", user.getId());
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByEmail(@NonNull String userEmail) {
        return accountRepository.findByEmail(userEmail).isPresent();
    }

    @Override
    @Transactional
    public void rename(@NonNull AccountId accountId, @NonNull String newName) {
        log.info("Renaming user with accountId: {} to: {}", accountId, newName);
        var entity = accountRepository.findById(accountId.value())
                .orElseThrow();
        entity.setName(newName);
        accountRepository.saveAndFlush(entity);
        log.info("Renamed user with accountId: {}", accountId);
    }

    @Override
    @Transactional
    public void delete(@NonNull AccountId accountId) {
        log.info("Deleting user with accountId: {}", accountId);
        accountRepository.deleteById(accountId.value());
        log.info("Deleted user with accountId: {}", accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails getByEmail(@NonNull String email) {
        return accountRepository.findByEmail(email).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountId findAccountIdByEmail(@NonNull String userEmail) {
        return AccountId.from(accountRepository.findByEmail(userEmail).orElseThrow().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountData getById(@NonNull AccountId accountId) {
        final var entity = accountRepository.findById(accountId.value()).orElseThrow();
        return AccountData.builder()
                .accountId(AccountId.from(entity.getId()))
                .name(entity.getName())
                .build();
    }
}