package com.niedzwiadek.parking.config.jwt;

import com.niedzwiadek.parking.account.api.AccountOperations;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final AccountOperations accountOperations;

  @Override
  protected void doFilterInternal(@NonNull final HttpServletRequest request,
                                  @NonNull final HttpServletResponse response,
                                  @NonNull final FilterChain filterChain) throws ServletException, IOException {
    final var authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer")) {
      filterChain.doFilter(request, response);
      return;
    }

    final var jwt = authHeader.substring(7);
    final var userEmail = jwtService.extractUsername(jwt);

    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      if (accountOperations.existByEmail(userEmail)) {
        if (jwtService.isTokenValid(jwt, userEmail)) {
          UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken
              .authenticated(userEmail, null, null);
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
