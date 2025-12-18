package com.abernathyclinic.medilabo_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtCookieAuthFilter extends OncePerRequestFilter {

    private static final String JWT_COOKIE = "AUTH_TOKEN";
    private final JwtService jwtService;

    @Autowired
    public JwtCookieAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = readJwtFromCookie(request);
        if (token != null) {
            try {
                Jws<Claims> jws = jwtService.parse(token);

                String username = jws.getBody().getSubject();
                Object rolesClaim = jws.getBody().get("roles");

                List<String> roles = new ArrayList<>();
                if (rolesClaim instanceof List<?>) {
                    for (Object r : (List<?>) rolesClaim) {
                        roles.add(String.valueOf(r));
                    }
                }

                var authorities = roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList();

                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
                request.setAttribute("Authorization", "Bearer " + token);

                log.info("JWT parsed successfully for user: {} with roles: {}", username, roles);

            } catch (JwtException e) {
                log.warn("JWT parsing failed: {}", e.getMessage());
                clearJwtCookie(response);
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }

    private String readJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (JWT_COOKIE.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JWT_COOKIE, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }
}