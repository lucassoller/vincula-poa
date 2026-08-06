package com.vincula.controller;

import com.vincula.dto.unidadeSaude.*;
import com.vincula.service.UnidadeSaudeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades-saude")
public class UnidadeSaudeController {

    private final UnidadeSaudeService unidadeSaudeService;

    public UnidadeSaudeController(UnidadeSaudeService unidadeSaudeService) {
        this.unidadeSaudeService = unidadeSaudeService;
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PostMapping
    public ResponseEntity<UnidadeSaudeResponseDTO> criar(@Valid @RequestBody UnidadeSaudeDTO dto) {
        UnidadeSaudeResponseDTO criada = unidadeSaudeService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/filtrados")
    public ResponseEntity<Page<UnidadeSaudeResponseDTO>> listarTodosFiltradas(
            @RequestBody FiltroServicoRequestDTO filtro,
            Pageable pageable) {
        return ResponseEntity.ok(unidadeSaudeService.listarTodosFiltrados(filtro, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public ResponseEntity <UnidadesResponseDTO> listarTodos() {
        return ResponseEntity.ok(unidadeSaudeService.listarServicos());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/ubs")
    public ResponseEntity<List<UnidadeSaudeShortResponseDTO>> listarTodasUnidades() {
        return ResponseEntity.ok(unidadeSaudeService.listarTodasUnidades());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/servicos")
    public ResponseEntity<List<UnidadeSaudeShortResponseDTO>> listarTodosServicos() {
        return ResponseEntity.ok(unidadeSaudeService.listarTodosServicos());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeSaudeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeSaudeService.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PutMapping("/{id}")
    public ResponseEntity<UnidadeSaudeResponseDTO> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody UnidadeSaudeDTO dto) {
        return ResponseEntity.ok(unidadeSaudeService.atualizar(id, dto));
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        unidadeSaudeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}