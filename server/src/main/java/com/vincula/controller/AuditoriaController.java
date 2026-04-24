package com.vincula.controller;

import com.vincula.dto.AuditoriaDTO;
import com.vincula.dto.EmailDTO;
import com.vincula.service.AuditoriaService;
import com.vincula.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/auditoria")
    public ResponseEntity<List<AuditoriaDTO>> listarLogs() {
        return ResponseEntity.ok(auditoriaService.listarTodos());
    }
}