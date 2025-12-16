package com.abernathyclinic.medilabo_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private final Key key = Keys.hmacShaKeyFor("hste39h@01maLqP09182naheyd517809".getBytes());
    private final long tokenExpireSeconds = 3600;

    public String createToken(String userName, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userName)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(tokenExpireSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}