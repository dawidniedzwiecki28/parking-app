package com.niedzwiadek.parking.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.account.api.AccountNotFoundException;
import com.niedzwiadek.parking.account.api.AccountOperations;
import com.niedzwiadek.parking.rest.account.AccountController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.niedzwiadek.parking.rest.account.AccountController.ACCOUNTS_PATH;
import static com.niedzwiadek.parking.rest.account.AccountController.ACCOUNT_PATH;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(value = AccountControllerTest.email)
public class AccountControllerTest {

  private static final AccountId accountId = AccountId.fromString("3869d6c9-2183-4091-8cd4-4658121e8034");
  static final String email = "email@pollub.pl";

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private AccountOperations accountOperations;

  @Test
  void shouldGetCurrentUser() throws Exception {
    // given
    given(accountOperations.findAccountIdByEmail(email)).willReturn(accountId);
    given(accountOperations.getById(accountId))
        .willReturn(new AccountOperations.AccountData(accountId, "UserName"));

    // when
    mockMvc.perform(get(ACCOUNTS_PATH))

        // then
        .andExpect(status().isOk())
        .andExpect(content().json("""
            {
              "id":"3869d6c9-2183-4091-8cd4-4658121e8034",
              "name":"UserName",
              "email":"email@pollub.pl"
            }
            """
        ));
  }

  @Test
  void shouldUpdateAccount() throws Exception {
    // given
    final var updateDto = new AccountController.AccountUpdateDto("newName");

    // when
    mockMvc.perform(put(ACCOUNT_PATH, accountId.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updateDto)))

        // then
        .andExpect(status().isOk());

    // and
    then(accountOperations).should().rename(accountId, "newName");
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingAccount() throws Exception {
    // given
    final var updateDto = new AccountController.AccountUpdateDto("newName");
    willThrow(new AccountNotFoundException(accountId)).given(accountOperations).rename(accountId, "newName");

    // when
    mockMvc.perform(put(ACCOUNT_PATH, accountId.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(updateDto)))

        // then
        .andExpect(status().isNotFound())
        .andExpect(content().string("Account not found for id: AccountId[value=3869d6c9-2183-4091-8cd4-4658121e8034]"));
  }

  @Test
  void shouldDeleteAccount() throws Exception {
    // when
    mockMvc.perform(delete(ACCOUNT_PATH, accountId.value()))

        // then
        .andExpect(status().isOk());

    // and
    then(accountOperations).should().delete(accountId);
  }
}
