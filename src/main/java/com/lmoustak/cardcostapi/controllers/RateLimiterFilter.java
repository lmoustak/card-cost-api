package com.lmoustak.cardcostapi.controllers;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

  private final Map<String, RateLimiter> rateLimiterByIp = new HashMap<>();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String ip = request.getRemoteAddr();

    RateLimiter rateLimiter = rateLimiterByIp.computeIfAbsent(ip, k -> RateLimiter.create(20));
    if (!rateLimiter.tryAcquire()) {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.getWriter().write("Too many requests. Please try again later");
      return;
    }

    filterChain.doFilter(request, response);
  }
}
