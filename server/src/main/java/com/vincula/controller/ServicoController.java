package com.vincula.controller;

import com.vincula.dto.servico.*;
import com.vincula.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PostMapping
    public ResponseEntity<ServicoResponseDTO> criar(@Valid @RequestBody ServicoDTO dto) {
        ServicoResponseDTO criada = servicoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/filtrados")
    public ResponseEntity<Page<ServicoResponseDTO>> listarTodosFiltradas(
            @RequestBody FiltroServicoRequestDTO filtro,
            Pageable pageable) {
        return ResponseEntity.ok(servicoService.listarTodosFiltrados(filtro, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public ResponseEntity <ServicosResponseDTO> listarTodos() {
        return ResponseEntity.ok(servicoService.listarServicos());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/ubs")
    public ResponseEntity<List<ServicoShortResponseDTO>> listarTodasServicos() {
        return ResponseEntity.ok(servicoService.listarTodasServicos());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/servicos")
    public ResponseEntity<List<ServicoShortResponseDTO>> listarTodosServicos() {
        return ResponseEntity.ok(servicoService.listarTodosServicos());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ServicoDTO dto) {
        return ResponseEntity.ok(servicoService.atualizar(id, dto));
    }

    @PreAuthorize("hasAnyRole('GESTAO_MUNICIPAL', 'VIGILANCIA', 'COORDENADORIA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}