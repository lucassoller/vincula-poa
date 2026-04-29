package com.vincula.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.ErrorResponseDTO;
import com.vincula.util.AuditoriaFacade;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final AuditoriaFacade auditoriaFacade;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper, AuditoriaFacade auditoriaFacade) {
        this.objectMapper = objectMapper;
        this.auditoriaFacade = auditoriaFacade;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Não autenticado",
                null,
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        auditoriaFacade.acessoNegado("Não autenticado para URL: " + request.getRequestURI());

        objectMapper.writeValue(response.getWriter(), error);
    }
}