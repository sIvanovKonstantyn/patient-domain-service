package com.example.patientdomainservice;

import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimiter rateLimiter;
    public RateLimitInterceptor(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
        HttpServletResponse response, Object handler) throws Exception {
        var key = generateKey(request.getRequestURI(), request.getMethod(),request.getParameterNames());
        if (rateLimiter.tryConsume(key)) {
            return true;
        } else {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
                "You have exhausted your API Request Quota");
            return false;
        }
    }

    public String generateKey(String requestURI, String method, Enumeration<String> parameterNames) {
        StringBuilder result = new StringBuilder();
        while (parameterNames.hasMoreElements()) {
            result.append(parameterNames.nextElement());
        }

        result.append(requestURI).append(method);

        return result.toString();
    }
}
