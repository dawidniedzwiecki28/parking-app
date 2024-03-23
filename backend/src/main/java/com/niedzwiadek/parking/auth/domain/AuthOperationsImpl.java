package com.niedzwiadek.parking.auth.domain;

import com.niedzwiadek.parking.account.api.AccountOperations;
import com.niedzwiadek.parking.auth.api.AuthOperations;
import com.niedzwiadek.parking.config.jwt.api.JwtOperations;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
class AuthOperationsImpl implements AuthOperations {

  private final PasswordEncoder passwordEncoder;
  private final AccountOperations accountOperations;
  private final JwtOperations jwtOperations;
  private final AuthenticationManager authenticationManager;

  @Override
  @Transactional
  public AuthenticationResponse register(@NonNull final RegisterRequest request) {
    final var userDetails = accountOperations.save(request.name(), request.email(), passwordEncoder.encode(request.password()));
    final var jwtToken = jwtOperations.generateToken(userDetails);
    return new AuthenticationResponse(jwtToken);
  }

  @Override
  @Transactional
  public AuthenticationResponse login(@NonNull final AuthenticationRequest request) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    final var userDetails = accountOperations.getByEmail(request.email());
    final var jwtToken = jwtOperations.generateToken(userDetails);
    return new AuthenticationResponse(jwtToken);
  }
}
