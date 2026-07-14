package com.manuel.vigicontrol.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Leemos header Authorization
        String authHeader = request.getHeader("Authorization");

        /**
         * Si no existe token o no empieza por Bearer
         * dejamos continuar request.
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Quitamos "Bearer "
        String token = authHeader.substring(7);

        /**
         * Validamos token JWT
         */
        if (!jwtService.validateToken(token)) {

            log.warn("Token inválido para request: {}", request.getRequestURI());

            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos username y roles
        String username = jwtService.extractUsername(token);

        List<String> roles = jwtService.extractRoles(token);

        /**
         * Si no existe autenticación previa
         * autenticamos usuario.
         */
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

            // Guardamos autenticación en Spring Security
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("Usuario autenticado: {}", username);
        }

        // Continuamos request
        filterChain.doFilter(request, response);
    }
}