package com.niedzwiadek.parking.rest.auth;

import com.niedzwiadek.parking.auth.api.AuthOperations;
import com.niedzwiadek.parking.auth.api.AuthOperations.AuthenticationRequest;
import com.niedzwiadek.parking.auth.api.AuthOperations.AuthenticationResponse;
import com.niedzwiadek.parking.auth.api.AuthOperations.RegisterRequest;
import com.niedzwiadek.parking.rest.exceptions.InvalidUserCredentialsException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);
  public static final String REGISTER_PATH = "/auth/register";
  public static final String LOGIN_PATH = "/auth/account";

  private final AuthOperations authOperations;

  @PostMapping(REGISTER_PATH)
  AuthenticationResponse register(@RequestBody @NonNull final RegisterRequest request) {
    log.info("Received request to register user with email {}", request.email());
    validateCredentials(request.email(), request.password());
    return authOperations.register(request);
  }

  @PostMapping(LOGIN_PATH)
  AuthenticationResponse login(@RequestBody @NonNull final AuthenticationRequest request) {
    log.info("Received request to login user with email {}", request.email());
    validateCredentials(request.email(), request.password());
    return authOperations.login(request);
  }

  private void validateCredentials(final String email, final String password) {
    if (email.isBlank() || password.isBlank()) {
      throw new InvalidUserCredentialsException("Password and email are required");
    }

    if (!isValidEmail(email)) {
      throw new InvalidUserCredentialsException("Invalid email format: " + email);
    }

    if (password.length() < 6) {
      throw new InvalidUserCredentialsException("Password must consist of at least 6 characters");
    }
  }

  private boolean isValidEmail(final String email) {
    final var emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    final var pattern = Pattern.compile(emailRegex);
    final var matcher = pattern.matcher(email);
    return matcher.matches();
  }
}
