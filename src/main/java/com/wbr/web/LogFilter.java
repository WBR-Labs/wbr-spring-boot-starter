package com.wbr.web;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class LogFilter extends OncePerRequestFilter {

    @Autowired
    private Tracer tracer;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.nanoTime();

        var span = tracer.currentSpan();
        String traceId = span != null ? span.context().traceId() : "-";

        log.info(
                ">>> {} {} query={} remoteIp={} userAgent=\"{}\" contentLength={} traceId={}",
                request.getMethod(),
                request.getRequestURI(),
                Optional.ofNullable(request.getQueryString()).orElse("-"),
                request.getRemoteAddr(),
                Optional.ofNullable(request.getHeader("User-Agent")).orElse("-"),
                request.getContentLengthLong(),
                traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            span = tracer.currentSpan();
            traceId = span != null ? span.context().traceId() : traceId;
            String spanId = span != null ? span.context().spanId() : "-";

            log.info(
                    "<<< {} {} {} ({}ms) contentType={} contentLength={} traceId={} spanId={}",
                    response.getStatus(),
                    request.getMethod(),
                    request.getRequestURI(),
                    durationMs,
                    Optional.ofNullable(response.getContentType()).orElse("-"),
                    Optional.ofNullable(response.getHeader("Content-Length")).orElse("-"),
                    traceId,
                    spanId);
        }
    }
}