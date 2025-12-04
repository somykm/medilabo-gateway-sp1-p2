//package com.abernathyclinic.medilabo_gateway.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//
//@Configuration
//public class GatewayFilterConfig {
//
//    @Bean
//    public GlobalFilter userHeaderFilter() {
//        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
//                .map(ctx -> ctx.getAuthentication())
//                .flatMap(auth -> {
//                            if (auth != null && auth.isAuthenticated()) {
//                                exchange.getRequest().mutate()
//                                        .header("X-User-Name", auth.getName())
//                                        .build();
//                            }
//                            return chain.filter(exchange);
//                        }
//                );
//    }
//}