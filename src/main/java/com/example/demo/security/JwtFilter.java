package com.example.demo.security;

import com.example.demo.dto.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailCustomService userDetailCustomService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if(jwtTokenProvider.validateToken(token)) {
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    UserPrincipal userPrincipal = (UserPrincipal) userDetailCustomService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userPrincipal,
                                    null,
                                    userPrincipal.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");

            ErrorResponse errorResponse = new ErrorResponse(
                    e.getMessage(), "UNAUTHORIZED"
            );

            String json = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(json);
        }

    }
}
