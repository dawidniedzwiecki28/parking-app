package com.niedzwiadek.parking.auth.api;

import lombok.NonNull;

public interface AuthOperations {
    AuthenticationResponse register(@NonNull RegisterRequest request);
    AuthenticationResponse login(@NonNull AuthenticationRequest request);



    record RegisterRequest(
            @NonNull String firstName,
            @NonNull String email,
            @NonNull String password) {
    }

    record AuthenticationRequest(@NonNull String email, @NonNull String password) {
    }

    record AuthenticationResponse(@NonNull String token) {
    }
}
