//package com.abernathyclinic.medilabo_gateway.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SpringSecurityConfig {
//
//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("secret")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails doctor = User.withDefaultPasswordEncoder()
//                .username("doctor")
//                .password("doctor123")
//                .roles("DOCTOR")
//                .build();
//
//        return new MapReactiveUserDetailsService(admin, doctor);
//    }
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        // Define where to redirect after successful login
//        RedirectServerAuthenticationSuccessHandler successHandler =
//                new RedirectServerAuthenticationSuccessHandler("/ui/add");
//
//        http
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers("/login").permitAll()
//                        .anyExchange().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .authenticationSuccessHandler(successHandler) // use success handler
//                )
//                .logout(logout -> logout.logoutUrl("/logout"))
//                .csrf(ServerHttpSecurity.CsrfSpec::disable);
//
//        return http.build();
//    }
//}
