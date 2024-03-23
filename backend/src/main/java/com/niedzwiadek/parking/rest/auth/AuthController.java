package com.niedzwiadek.parking.rest.auth;

import com.niedzwiadek.parking.auth.api.AuthOperations;
import com.niedzwiadek.parking.auth.api.AuthOperations.AuthenticationRequest;
import com.niedzwiadek.parking.auth.api.AuthOperations.AuthenticationResponse;
import com.niedzwiadek.parking.auth.api.AuthOperations.RegisterRequest;
import com.niedzwiadek.parking.rest.exceptions.InvalidLoginRequest;
import com.niedzwiadek.parking.rest.exceptions.InvalidRegisterRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);
  private final AuthOperations authOperations;

  @PostMapping("/register")
  public AuthenticationResponse register(@RequestBody @NonNull final RegisterRequest request) {
    log.info("Received request to register user with email {}", request.email());

    if (request.email().isBlank() || request.password().isBlank()) {
      throw new InvalidRegisterRequest("Password and email are required");
    }

    if (!isValidEmail(request.email())) {
      throw new InvalidRegisterRequest("Invalid email format" + request.email());
    }

    return authOperations.register(request);
  }

  private boolean isValidEmail(final String email) {
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    Pattern pattern = Pattern.compile(emailRegex);
    Matcher matcher = pattern.matcher(email);
    return matcher.matches();
  }

  @PostMapping("/login")
  public AuthenticationResponse login(
      @RequestBody @NonNull final AuthenticationRequest request) {
    log.info("Received request to login user with email {}", request.email());
    if (request.email().isBlank() || request.password().isBlank()) {
      throw new InvalidLoginRequest("Password and email are required");
    }
    return authOperations.login(request);
  }
}
