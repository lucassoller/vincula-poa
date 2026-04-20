package com.vincula.exception;

import com.vincula.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }

        ErrorResponseDTO response = new ErrorResponseDTO(
                "Dados inválidos",
                errors,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusiness(
            BusinessException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                ex.getMessage(),
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                ex.getMessage(),
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                ex.getMessage(),
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthorizationDenied(
            org.springframework.security.authorization.AuthorizationDeniedException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                "Acesso negado",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                "Acesso negado",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO(
                "Erro interno do servidor",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}