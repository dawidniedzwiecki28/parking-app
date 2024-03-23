package com.niedzwiadek.parking.account.domain;

import com.niedzwiadek.parking.account.api.AccountAlreadyExistException;
import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.account.api.AccountNotFoundException;
import com.niedzwiadek.parking.account.api.AccountOperations;
import com.niedzwiadek.parking.account.infrastructure.AccountEntity;
import com.niedzwiadek.parking.account.infrastructure.AccountRepository;
import com.niedzwiadek.parking.carpark.api.CarOperations;
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
  private final CarOperations carOperations;
  private final AccountRepository accountRepository;

  @Override
  @Transactional
  public UserDetails save(@NonNull final String name,
                          @NonNull final String email,
                          @NonNull final String password) {
    final var account = AccountEntity.builder()
        .createdAt(Instant.now())
        .email(email)
        .name(name)
        .id(UUID.randomUUID())
        .role(AccountEntity.Role.ADMIN)
        .password(password)
        .build();
    try {
      log.info("Saving account {}", account);
      accountRepository.saveAndFlush(account);
      log.info("User saved {}", account.getId());
    } catch (Exception e) {
      if (e.getMessage().contains("accounts_email_key")) {
        throw new AccountAlreadyExistException(email);
      }
    }
    return account;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existByEmail(@NonNull final String userEmail) {
    return accountRepository.findByEmail(userEmail).isPresent();
  }

  @Override
  @Transactional
  public void rename(@NonNull final AccountId accountId, @NonNull final String newName) {
    log.info("Renaming user with accountId: {} to: {}", accountId, newName);
    var entity = accountRepository.findById(accountId.value())
        .orElseThrow(() -> new AccountNotFoundException(accountId));
    entity.setName(newName);
    accountRepository.saveAndFlush(entity);
    log.info("Renamed user with accountId: {}", accountId);
  }

  @Override
  @Transactional
  public void delete(@NonNull final AccountId accountId) {
    log.info("Deleting user with accountId: {}", accountId);
    carOperations.deleteCarsFor(accountId);
    accountRepository.deleteById(accountId.value());
    log.info("Deleted user with accountId: {}", accountId);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails getByEmail(@NonNull final String email) {
    return accountRepository.findByEmail(email)
        .orElseThrow(() -> new AccountNotFoundException(email));
  }

  @Override
  @Transactional(readOnly = true)
  public AccountId findAccountIdByEmail(@NonNull final String email) {
    return AccountId.from(accountRepository.findByEmail(email)
        .orElseThrow(() -> new AccountNotFoundException(email))
        .getId());
  }

  @Override
  @Transactional(readOnly = true)
  public AccountData getById(@NonNull final AccountId accountId) {
    final var entity = accountRepository.findById(accountId.value())
        .orElseThrow(() -> new AccountNotFoundException(accountId));
    return AccountData.builder()
        .accountId(AccountId.from(entity.getId()))
        .name(entity.getName())
        .build();
  }
}
