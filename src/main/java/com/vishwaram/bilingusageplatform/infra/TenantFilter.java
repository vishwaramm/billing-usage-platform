package com.vishwaram.bilingusageplatform.infra;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TenantFilter extends OncePerRequestFilter {
    private static final String HEADER = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String raw = request.getHeader(HEADER);

        if (raw == null || raw.isBlank()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Missing header: " + HEADER);
            return;
        }

        try {
            TenantContext.set(UUID.fromString(raw));
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("Invalid TenantId: " + raw);
        } finally {
            TenantContext.clear();
        }
    }
}
