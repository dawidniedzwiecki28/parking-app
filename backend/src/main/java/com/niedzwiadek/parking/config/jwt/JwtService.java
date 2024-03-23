package com.niedzwiadek.parking.config.jwt;

import com.niedzwiadek.parking.config.jwt.api.JwtOperations;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService implements JwtOperations {

  @Value("${parking.config.jwt.durationMilis}")
  private long jwtDurationMilis;

  @Value("${parking.config.jwt.secretKey}")
  private String SECRET_KEY;

  @Override
  public String generateToken(@NonNull final UserDetails details) {
    return Jwts
        .builder()
        .subject(details.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtDurationMilis))
        .signWith(getSigningKey())
        .compact();
  }

  String extractUsername(@NonNull final String jwt) {
    return extractClaim(jwt, Claims::getSubject);
  }

  <T> T extractClaim(@NonNull final String token, @NonNull final Function<Claims, T> claimsResolver) {
    final var claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  boolean isTokenValid(@NonNull final String token, @NonNull final String email) {
    final var username = extractUsername(token);
    return username.equals(email) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(final String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date(System.currentTimeMillis()));
  }

  private Claims extractAllClaims(final String token) {
    return Jwts
        .parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
