package com.groceryshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SseConfig {

    // SSE timeout: 30 minutes (clients will reconnect automatically)
    private static final long SSE_TIMEOUT = Duration.ofMinutes(30).toMillis();

    @Bean
    public ConcurrentHashMap<String, SseEmitter> sseEmitters() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public SseEmitter sseEmitter() {
        return new SseEmitter(SSE_TIMEOUT);
    }
}
