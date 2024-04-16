package com.example.patientdomainservice;

public interface RateLimiter {
    boolean tryConsume(String key);
}
