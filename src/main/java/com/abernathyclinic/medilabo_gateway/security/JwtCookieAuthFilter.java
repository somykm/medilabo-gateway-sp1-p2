package com.abernathyclinic.medilabo_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
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

@Component
public class JwtCookieAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

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
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) jws.getBody().get("roles");

                var authorities = roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList();

                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException e) {
                clearJwtCookie(response);
            }
        }
        //do nothing, let Spring Security use JSESSIONID
        chain.doFilter(request, response);
    }

    private static final String JWT_COOKIE = "AUTH_TOKEN";

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
        response.addCookie(cookie);
    }
}