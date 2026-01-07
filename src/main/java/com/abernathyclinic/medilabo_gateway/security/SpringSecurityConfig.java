package com.abernathyclinic.medilabo_gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.ResponseCookie;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private static final String JWT_COOKIE = "AUTH_TOKEN";
    private final JwtService jwtService;

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("{noop}secret")
                .roles("ADMIN")
                .build();

        UserDetails doctor = User.withUsername("doctor")
                .password("{noop}doctor123")
                .roles("DOCTOR")
                .build();

        return new MapReactiveUserDetailsService(admin, doctor);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                        .pathMatchers("/ui/**").authenticated()
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().permitAll()
                )
                .formLogin(form -> form
                        .authenticationSuccessHandler(jwtCookieSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((exchange, authentication) -> {
                            ResponseCookie delete = ResponseCookie.from(JWT_COOKIE, "")
                                    .path("/")
                                    .maxAge(Duration.ZERO)
                                    .httpOnly(true)
                                    .build();
                            exchange.getExchange().getResponse().addCookie(delete);
                            exchange.getExchange().getResponse()
                                    .setStatusCode(HttpStatus.SEE_OTHER);
                            exchange.getExchange().getResponse()
                                    .getHeaders().setLocation(URI.create("/login?logout"));
                            return exchange.getExchange().getResponse().setComplete();
                        })
                )
                .build();
    }

    private ServerAuthenticationSuccessHandler jwtCookieSuccessHandler() {
        return (webFilterExchange, authentication) -> {
            ServerWebExchange exchange = webFilterExchange.getExchange();

            String username = authentication.getName();
            List<String> roles = authentication.getAuthorities().stream()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .toList();

            String token = jwtService.createToken(username, roles);

            ResponseCookie cookie = ResponseCookie.from(JWT_COOKIE, token)
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .build();

            exchange.getResponse().addCookie(cookie);
            exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
            exchange.getResponse().getHeaders().setLocation(URI.create("/ui/store-token"));

            return exchange.getResponse().setComplete();
        };
    }
}