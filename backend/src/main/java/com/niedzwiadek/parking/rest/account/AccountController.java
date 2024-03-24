package com.niedzwiadek.parking.rest.account;

import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.account.api.AccountOperations;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class AccountController {

  private static final Logger log = LoggerFactory.getLogger(AccountController.class);
  public static final String ACCOUNTS_PATH = "/account";
  public static final String ACCOUNT_PATH = ACCOUNTS_PATH + "/{accountId}";

  private final AccountOperations accountOperations;

  @GetMapping(ACCOUNTS_PATH)
  AccountDto getCurrent(final Principal principal) {
    final var email = principal.getName();
    log.info("Received request to get current user {}", email);
    final var accountId = accountOperations.findAccountIdByEmail(email);
    final var accountData = accountOperations.getById(accountId);
    return new AccountDto(accountData.accountId().serialize(), accountData.name(), email);
  }

  @PutMapping(ACCOUNT_PATH)
  void update(@PathVariable @NonNull final String accountId,
              @RequestBody @NonNull final AccountUpdateDto updateDto) {
    log.info("Received request to rename user {}", accountId);
    accountOperations.rename(AccountId.ofNullable(accountId), updateDto.name());
  }

  @DeleteMapping(ACCOUNT_PATH)
  void delete(@PathVariable @NonNull final String accountId) {
    log.info("Received request to delete user {}", accountId);
    accountOperations.delete(AccountId.ofNullable(accountId));
  }

  public record AccountUpdateDto(String name) {
  }

  public record AccountDto(
      @NonNull String id,
      String name,
      @NonNull String email) {
  }
}
