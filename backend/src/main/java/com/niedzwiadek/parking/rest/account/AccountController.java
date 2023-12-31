package com.niedzwiadek.parking.rest.account;

import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.account.api.AccountOperations;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountOperations accountOperations;

    @GetMapping()
    AccountDto getCurrent(Principal principal) {
        final var email = principal.getName();
        log.info("Received request to get current user {}", email);
        final var accountId = accountOperations.findAccountIdByEmail(email);
        final var accountData = accountOperations.getById(accountId);
        return new AccountDto(accountData.getAccountId().serialize(), accountData.getName(), email);
    }

    @PutMapping("/{accountId}")
    void update(@PathVariable String accountId, @RequestBody AccountUpdateDto updateDto) {
        log.info("Received request to rename user {}", accountId);
        accountOperations.rename(AccountId.ofNullable(accountId), updateDto.name());
    }

    @DeleteMapping("/{accountId}")
    void delete(@PathVariable String accountId) {
        log.info("Received request to delete user {}", accountId);
        accountOperations.delete(AccountId.ofNullable(accountId));
    }

    record AccountUpdateDto(String name) {
    }

    record AccountDto(String id, String name, String email) {
    }
}
