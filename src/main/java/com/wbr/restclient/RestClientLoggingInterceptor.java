package com.wbr.restclient;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final Tracer tracer;

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        long start = System.nanoTime();

        var span = tracer.currentSpan();
        String traceId = span != null ? span.context().traceId() : "-";

        log.info(
                "--> {} {} traceId={} contentLength={}",
                request.getMethod(),
                request.getURI(),
                traceId,
                body.length);

        ClientHttpResponse response = null;

        try {
            response = execution.execute(request, body);
            return response;
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            int status = response != null ? response.getStatusCode().value() : -1;

            span = tracer.currentSpan();
            traceId = span != null ? span.context().traceId() : traceId;
            String spanId = span != null ? span.context().spanId() : "-";

            log.info(
                    "<-- {} {} {} ({}ms) traceId={} spanId={}",
                    status,
                    request.getMethod(),
                    request.getURI(),
                    durationMs,
                    traceId,
                    spanId);
        }
    }
}