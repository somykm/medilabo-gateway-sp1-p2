//package com.abernathyclinic.medilabo_gateway.filters;
//
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.http.HttpCookie;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class JwtCookieToHeaderFilter implements GlobalFilter {
//
//    private static final String JWT_COOKIE = "AUTH_TOKEN";
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        HttpCookie cookie = exchange.getRequest()
//                .getCookies()
//                .getFirst(JWT_COOKIE);
//
//        if (cookie != null && !cookie.getValue().isBlank()) {
//            String token = cookie.getValue();
//
//            ServerHttpRequest mutatedRequest = exchange.getRequest()
//                    .mutate()
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                    .build();
//
//            ServerWebExchange mutatedExchange = exchange.mutate()
//                    .request(mutatedRequest)
//                    .build();
//
//            return chain.filter(mutatedExchange);
//        }
//
//        return chain.filter(exchange);
//    }
//}