package com.example.patientdomainservice;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    Map<String, Bucket> endpointBuckets = new HashMap<>();

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
        endpointBuckets.put(
            "name/GET",
            createGetByNameBucket()
        );

        endpointBuckets.put(
            "pageNumberpageSize/GET",
            createGetPageBucket()
        );

        endpointBuckets.put(
            "/POST",
            createAddPatientBucket()
        );

        return (String key) -> endpointBuckets.get(key).tryConsume(1);
    }

    private Bucket createAddPatientBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(1000)
            .refillGreedy(100, Duration.ofMillis(100))
            .build();

        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    private Bucket createGetPageBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(100)
            .refillGreedy(10, Duration.ofMillis(100))
            .build();

        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    private Bucket createGetByNameBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(10)
            .refillGreedy(1, Duration.ofMillis(100))
            .build();

        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor(rateLimiter()));
    }
}