package com.vincula.controller;

import com.vincula.dto.unidadeSaude.UnidadeSaudeDTO;
import com.vincula.dto.unidadeSaude.UnidadeSaudeResponseDTO;
import com.vincula.dto.unidadeSaude.UnidadeSaudeShortResponseDTO;
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

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @PostMapping
    public ResponseEntity<UnidadeSaudeResponseDTO> criar(@Valid @RequestBody UnidadeSaudeDTO dto) {
        UnidadeSaudeResponseDTO criada = unidadeSaudeService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<Page<UnidadeSaudeResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(unidadeSaudeService.listarTodos(pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filtradas/{filtro}")
    public ResponseEntity<Page<UnidadeSaudeResponseDTO>> listarTodosFiltrados(@PathVariable String filtro, Pageable pageable) {
        return ResponseEntity.ok(unidadeSaudeService.listarTodosFiltrados(filtro, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public ResponseEntity<List<UnidadeSaudeShortResponseDTO>> listarTodos() {
        return ResponseEntity.ok(unidadeSaudeService.listarTodos());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeSaudeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeSaudeService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @PutMapping("/{id}")
    public ResponseEntity<UnidadeSaudeResponseDTO> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody UnidadeSaudeDTO dto) {
        return ResponseEntity.ok(unidadeSaudeService.atualizar(id, dto));
    }

    @PreAuthorize("hasRole('GESTAO_MUNICIPAL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        unidadeSaudeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}