package com.vincula.controller;

import com.vincula.dto.unidadeSaude.*;
import com.vincula.enums.TipoServico;
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
    @GetMapping("/filtrados/buscas")
    public ResponseEntity<List<UnidadeSaudeShortResponseDTO>> listarTodosFiltradosPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(unidadeSaudeService.listarTodosFiltradosPorNome(nome));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public ResponseEntity <UnidadesResponseDTO> listarTodos() {
        return ResponseEntity.ok(unidadeSaudeService.listarUnidades());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/ubs")
    public ResponseEntity<List<UnidadeSaudeShortResponseDTO>> listarTodasUbs() {
        return ResponseEntity.ok(unidadeSaudeService.listarTodosPorServico(TipoServico.UBS));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/outro")
    public ResponseEntity<List<UnidadeSaudeShortResponseDTO>> listarTodosOutro() {
        return ResponseEntity.ok(unidadeSaudeService.listarTodosPorServico(TipoServico.OUTRO));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/especializado")
    public ResponseEntity<List<UnidadeSaudeShortResponseDTO>> listarTodosEspecializado() {
        return ResponseEntity.ok(unidadeSaudeService.listarTodosPorServico(TipoServico.SERVICO_ESPECIALIZADO));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/outros")
    public ResponseEntity<List<UnidadeSaudeShortResponseDTO>> listarTodosOutros() {
        return ResponseEntity.ok(unidadeSaudeService.listarTodosOutros());
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