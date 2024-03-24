package com.niedzwiadek.parking.auth.api;

import lombok.Builder;
import lombok.NonNull;

public interface AuthOperations {
  AuthenticationResponse register(@NonNull RegisterRequest request);

  AuthenticationResponse login(@NonNull AuthenticationRequest request);

  @Builder
  record RegisterRequest(
      @NonNull String name,
      @NonNull String email,
      @NonNull String password) {
  }

  record AuthenticationRequest(@NonNull String email, @NonNull String password) {
  }

  record AuthenticationResponse(@NonNull String token) {
  }
}
