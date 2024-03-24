package com.niedzwiadek.parking.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedzwiadek.parking.account.api.AccountNotFoundException;
import com.niedzwiadek.parking.auth.api.AuthOperations;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.niedzwiadek.parking.rest.auth.AuthController.LOGIN_PATH;
import static com.niedzwiadek.parking.rest.auth.AuthController.REGISTER_PATH;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private AuthOperations authOperations;

  @Nested
  class Register {
    @Test
    void shouldRegister() throws Exception {
      // given
      final var request = AuthOperations.RegisterRequest.builder()
          .name("Name")
          .email("email@pollub.pl")
          .password("Password")
          .build();
      given(authOperations.register(request))
          .willReturn(new AuthOperations.AuthenticationResponse("jwt-token"));

      // when
      mockMvc.perform(post(REGISTER_PATH)
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)))

          // then
          .andExpect(status().isOk())
          .andExpect(content().json("""
               
                 {
                 "token": "jwt-token"
               }
                
              """
          ));
    }

    @ParameterizedTest
    @MethodSource("invalidRegisterParams")
    void shouldReturnBadRequestForInvalidCredentialsWhenRegister(final String email,
                                                                 final String password,
                                                                 final String errorMessage) throws Exception {
      // given
      final var request = AuthOperations.RegisterRequest.builder()
          .name("Name")
          .email(email)
          .password(password)
          .build();

      // when
      mockMvc.perform(post(REGISTER_PATH)
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)))

          // then
          .andExpect(status().isBadRequest())
          .andExpect(content().string(errorMessage));
    }

    private static Stream<Arguments> invalidRegisterParams() {
      return invalidCredentials();
    }
  }

  @Nested
  class Login {
    @Test
    void shouldLogin() throws Exception {
      // given
      final var request = new AuthOperations.AuthenticationRequest("email@pollub.pl", "password");
      given(authOperations.login(request))
          .willReturn(new AuthOperations.AuthenticationResponse("jwt-token"));

      // when
      mockMvc.perform(post(LOGIN_PATH)
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)))

          // then
          .andExpect(status().isOk())
          .andExpect(content().json("""
              {
                "token": "jwt-token"
              }
              """
          ));
    }

    @Test
    void shouldReturnNotFoundWhenLogin() throws Exception {
      // given
      final var email = "email@pollub.pl";
      final var request = new AuthOperations.AuthenticationRequest(email, "password");
      given(authOperations.login(request)).willThrow(new AccountNotFoundException(email));

      // when
      mockMvc.perform(post(LOGIN_PATH)
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)))

          // then
          .andExpect(status().isNotFound())
          .andExpect(content().string("Account not found for email: " + email));
    }

    @ParameterizedTest
    @MethodSource("invalidLoginCredentials")
    void shouldReturnBadRequestForInvalidCredentialsWhenLogin(final String email,
                                                              final String password,
                                                              final String errorMessage) throws Exception {
      // given
      final var request = new AuthOperations.AuthenticationRequest(email, password);

      // when
      mockMvc.perform(post(LOGIN_PATH)
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(request)))

          // then
          .andExpect(status().isBadRequest())
          .andExpect(content().string(errorMessage));
    }

    private static Stream<Arguments> invalidLoginCredentials() {
      return invalidCredentials();
    }
  }

  static Stream<Arguments> invalidCredentials() {
    return Stream.of(
        Arguments.of("email", "", "Password and email are required"),
        Arguments.of("", "password", "Password and email are required"),
        Arguments.of("email", "password", "Invalid email format: email"),
        Arguments.of("example.com", "password", "Invalid email format: example.com"),
        Arguments.of("user@example", "password", "Invalid email format: user@example"),
        Arguments.of("user@ex ample.com", "password", "Invalid email format: user@ex ample.com"),
        Arguments.of("user@exam!ple.com", "password", "Invalid email format: user@exam!ple.com"),
        Arguments.of("user@example.c", "password", "Invalid email format: user@example.c"),
        Arguments.of("user@example.abcdefghi", "password", "Invalid email format: user@example.abcdefghi"),
        Arguments.of("user@example.com", "aaa", "Password must consist of at least 6 characters")
    );
  }
}
