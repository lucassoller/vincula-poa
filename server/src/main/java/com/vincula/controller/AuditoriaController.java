package com.vincula.controller;

import com.vincula.dto.auditoria.AuditoriaDTO;
import com.vincula.dto.auditoria.FiltroAuditoriaRequestDTO;
import com.vincula.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<Page<AuditoriaDTO>> listarLogs(Pageable pageable) {
        return ResponseEntity.ok(auditoriaService.listarTodos(pageable));
    }

    @GetMapping("/servidor/{servidorId}")
    public ResponseEntity<Page<AuditoriaDTO>> listarPorServidor(@PathVariable Long servidorId, Pageable pageable) {
        return ResponseEntity.ok(auditoriaService.listarPorServidor(servidorId, pageable));
    }

    @GetMapping("/periodo")
    public ResponseEntity<Page<AuditoriaDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            Pageable pageable
    ) {
        return ResponseEntity.ok(auditoriaService.listarPorPeriodo(inicio, fim, pageable));
    }

    @GetMapping("/servidor/{servidorId}/periodo")
    public ResponseEntity<Page<AuditoriaDTO>> listarPorServidorEPeriodo(
            @PathVariable Long servidorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            Pageable pageable
    ) {
        return ResponseEntity.ok(auditoriaService.listarPorServidorEPeriodo(servidorId, inicio, fim, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/filtrados")
    public ResponseEntity<Page<AuditoriaDTO>> listarTodosFiltrados(
            @RequestBody FiltroAuditoriaRequestDTO filtro,
            Pageable pageable) {
        return ResponseEntity.ok(auditoriaService.listarTodosFiltrados(filtro, pageable));
    }
}