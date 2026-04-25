package com.vincula.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincula.dto.ErrorResponseDTO;
import com.vincula.util.AuditoriaFacade;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final AuditoriaFacade auditoriaFacade;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper,
                                     AuditoriaFacade auditoriaFacade) {
        this.objectMapper = objectMapper;
        this.auditoriaFacade = auditoriaFacade;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Acesso negado",
                null,
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        auditoriaFacade.acessoNegado("Acesso negado para URL: " + request.getRequestURI());

        objectMapper.writeValue(response.getWriter(), error);
    }
}