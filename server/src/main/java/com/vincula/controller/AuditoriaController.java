package com.vincula.controller;

import com.vincula.dto.auditoria.AuditoriaDTO;
import com.vincula.dto.auditoria.FiltroAuditoriaRequestDTO;
import com.vincula.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/filtrados")
    public ResponseEntity<Page<AuditoriaDTO>> listarTodosFiltrados(
            @RequestBody FiltroAuditoriaRequestDTO filtro,
            Pageable pageable) {
        return ResponseEntity.ok(auditoriaService.listarTodosFiltrados(filtro, pageable));
    }
}