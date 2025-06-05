package org.ms.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {
    @Bean
    KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress() != null ?
                exchange.getRequest().getRemoteAddress().getHostString().replace(".", "_") :
                "unknown";
            return Mono.just("ip_" + ip);
        };
    }
}
