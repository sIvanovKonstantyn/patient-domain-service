package com.example.patientdomainservice;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

@Configuration
@EnableCaching
public class ApplicationConfig implements WebMvcConfigurer {
    Bandwidth limit = Bandwidth.builder()
        .capacity(10)
        .refillGreedy(1, Duration.ofMillis(100))
        .build();

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("patientsByNames");
        caffeineCacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .maximumSize(1000));

        return caffeineCacheManager;
    }

    @Bean
    public RateLimiter rateLimiter() {
        Bucket bucket = Bucket.builder()
            .addLimit(limit)
            .build();

        return () -> bucket.tryConsume(1);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor(rateLimiter()));
    }
}