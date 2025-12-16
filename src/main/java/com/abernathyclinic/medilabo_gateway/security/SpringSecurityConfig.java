package com.abernathyclinic.medilabo_gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SpringSecurityConfig {

    private final JwtService jwtService = new JwtService();

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("{noop}secret")
                .roles("ADMIN").build();

        UserDetails doctor = User.withUsername("doctor")
                .password("{noop}doctor123")
                .roles("DOCTOR").build();

        return new InMemoryUserDetailsManager(admin, doctor);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtCookieAuthFilter jwtCookieAuthFilter) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/login"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/ui/**").authenticated()
                        //.requestMatchers("/api/**").authenticated()
                        .requestMatchers("/api/add**").permitAll()

                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .addFilterBefore(jwtCookieAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            var principal = (UserDetails) authentication.getPrincipal();
            var roles = principal.getAuthorities().stream()
                    .map(a -> a.getAuthority().replace("ROLE_", ""))
                    .toList();
            String token = jwtService.createToken(principal.getUsername(), roles);
            setJwtCookie(response, token);
            response.sendRedirect("/ui/add");
        };
    }

    private static final String JWT_COOKIE = "AUTH_TOKEN";

    private void setJwtCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie(JWT_COOKIE, jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }
}