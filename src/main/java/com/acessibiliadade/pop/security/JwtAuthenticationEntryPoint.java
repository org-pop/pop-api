package com.acessibiliadade.pop.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

// Sem entry point customizado, o Spring Security cai no Http403ForbiddenEntryPoint
// (default quando não há .httpBasic()/.formLogin()) para requests sem autenticação
// em rotas protegidas — devolvia 403 em vez de 401, confundindo "sem token" com
// "sem permissão". Também reaproveitado pelo JwtAuthenticationFilter para token
// inválido/expirado, pra manter o mesmo formato de erro nos dois casos.
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        write(response, "Autenticação necessária para acessar este recurso");
    }

    public void write(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "message", message
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
