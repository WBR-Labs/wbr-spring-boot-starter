package com.wbr.security;

import com.wbr.error.exception.ForbiddenException;
import com.wbr.error.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class SecurityEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException ex) {
        AuthError error = resolveError(ex);
        resolver.resolveException(request, response, null,
                new UnauthorizedException(error.code(), error.message()));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException ex) {
        resolver.resolveException(request, response, null, new ForbiddenException("Access denied"));
    }

    private AuthError resolveError(AuthenticationException ex) {
        if (ex instanceof InsufficientAuthenticationException) {
            return new AuthError("TOKEN_MISSING", "Bearer token is required");
        }
        if (ex instanceof InvalidBearerTokenException) {
            Throwable cause = ex.getCause();
            if (cause instanceof JwtValidationException jwtValidation) {
                String message = jwtValidation.getErrors().stream()
                        .map(e -> e.getDescription())
                        .findFirst()
                        .orElse("Token validation failed");
                return new AuthError("TOKEN_VALIDATION_FAILED", message);
            }
            if (cause instanceof BadJwtException) {
                return new AuthError("TOKEN_MALFORMED", "Malformed or unreadable token");
            }
            if (cause instanceof JwtException) {
                return new AuthError("TOKEN_INVALID", cause.getMessage());
            }
            return new AuthError("TOKEN_INVALID", "Invalid Bearer token");
        }
        return new AuthError("UNAUTHORIZED", "Authentication required");
    }

    private record AuthError(String code, String message) {
    }
}
