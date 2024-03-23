package com.niedzwiadek.parking.config.jwt.api;

import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtOperations {
  String generateToken(@NonNull UserDetails details);
}
