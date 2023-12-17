package com.niedzwiadek.parking.rest.auth;

import com.niedzwiadek.parking.auth.api.AuthOperations;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthOperations authOperations;

    @PostMapping("/register")
    public ResponseEntity<AuthOperations.AuthenticationResponse> register(@RequestBody AuthOperations.RegisterRequest request) {
        log.info("Received request to register user with email {}", request.email());
        if (request.email().isBlank() || request.password().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authOperations.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthOperations.AuthenticationResponse> login(@RequestBody AuthOperations.AuthenticationRequest request) {
        log.info("Received request to login user with email {}", request.email());
        if (request.email().isBlank() || request.password().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authOperations.login(request));
    }
}
